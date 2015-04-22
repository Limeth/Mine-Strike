package cz.minestrike.me.limeth.minestrike;

import ca.wacos.nametagedit.NametagAPI;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import cz.minestrike.me.limeth.minestrike.dbi.binding.MSPlayerData;
import cz.minestrike.me.limeth.minestrike.dbi.binding.MSPlayerDataContainer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.Firing;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.Reloading;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.lobby.Lobby;
import cz.minestrike.me.limeth.minestrike.util.InjectedConversationTracker;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.projectsurvive.limeth.dynamicdisplays.DynamicDisplays;
import cz.projectsurvive.limeth.dynamicdisplays.PlayerDisplay;
import cz.projectsurvive.limeth.dynamicdisplays.TimedPlayerDisplay;
import cz.projectsurvive.me.limeth.TabHeader;
import cz.projectsurvive.me.limeth.Title;
import net.darkseraphim.actionbar.ActionBarAPI;
import net.minecraft.server.v1_7_R4.EnumClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MSPlayer
{
	private static final float
			MAXIMAL_INACCURACY = 100,
			INACCURACY_MODIFIER = 0.1F,
			JUMP_INACCURACY_DURATION = 1,
			LAND_INACCURACY_DURATION = 1,
			RECOIL_RESTORATION_PER_SECOND = 2, MAXIMAL_RECOIL = 1;

	//private static final HashSet<MSPlayer> ONLINE_PLAYERS = new HashSet<MSPlayer>();

	private static Integer MOVEMENT_LOOP_ID;

	public static Set<MSPlayer> getOnlinePlayers()
	{
		Player[] players = Bukkit.getOnlinePlayers();

		return Arrays.stream(players).filter(InjectedConversationTracker::isInjected)
		              .map(InjectedConversationTracker::getMSPlayer).collect(Collectors.toSet());
	}

	public static boolean remove(Player player)
	{
		if(InjectedConversationTracker.isInjected(player))
		{
			InjectedConversationTracker.eject(player);
			return true;
		}
		else
			return false;
	}

	public static MSPlayer get(Player player, boolean register)
	{
		if(InjectedConversationTracker.isInjected(player))
			return InjectedConversationTracker.getMSPlayer(player);

		if(!register)
			return null;

		MSPlayer msPlayer;

		try
		{
			msPlayer = load(player.getName());
		}
		catch(SQLException e)
		{
			MineStrike.warn("An error occurred while loading player data for player '" + player.getName() + "'.");
			e.printStackTrace();
			return null;
		}

		if(msPlayer == null)
			msPlayer = new MSPlayer(player);

		register(msPlayer);

		return msPlayer;
	}

	public static MSPlayer get(Player player)
	{
		return get(player, false);
	}

	public static MSPlayer load(String playerName) throws SQLException
	{
		MSPlayerDataContainer dataContainer = MSPlayerDAO.loadDataContainer(playerName);

		if(dataContainer == null)
			return new MSPlayer(playerName);

		return new MSPlayer(dataContainer);
	}

	public static boolean register(MSPlayer msPlayer)
	{
		msPlayer.setJoinedAt(System.currentTimeMillis());

		Player player = msPlayer.getPlayer();

		if(!InjectedConversationTracker.isInjected(player))
		{
			InjectedConversationTracker.inject(player, msPlayer);
			return true;
		}
		else
			return false;
	}

	public static void loadOnlinePlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
			get(player, true);
	}

	public static void clearOnlinePlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
			remove(player);
	}

	public static int startMovementLoop()
	{
		return MOVEMENT_LOOP_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), () -> {
            for(MSPlayer msPlayer : getOnlinePlayers())
            {
                Player player1 = msPlayer.getPlayer();
                Location loc = player1.getLocation();
                Location lastLoc = msPlayer.lastLocation;
                msPlayer.lastLocation = loc;

                if(lastLoc == null)
                    continue;

                msPlayer.setSpeed(lastLoc.distance(loc));

                boolean onGround = ((CraftPlayer) player1).isOnGround();

                if(onGround && msPlayer.inAir)
                {
                    msPlayer.landTime = System.currentTimeMillis();
                    msPlayer.inAir = false;
                }
                else if(!onGround && !msPlayer.inAir)
                {
                    msPlayer.jumpTime = System.currentTimeMillis();
                    msPlayer.inAir = true;
                }
            }
        }, 1L, 0L);
	}
	
	public static void stopMovementLoop()
	{
		Bukkit.getScheduler().cancelTask(MOVEMENT_LOOP_ID);
	}
	
	private final HashMap<String, Object> customData = Maps.newHashMap();
	private Player player;
	private MSPlayerDataContainer dataContainer;
	private MSPlayerDataContainer previousDataContainer;
//	private final MSPlayerData data;
//	private InventoryContainer inventoryContainer;
	private HotbarContainer hotbarContainer;
	private ArmorContainer armorContainer;
	private Long joinedAt;
	private Location lastLocation;
	private GunTask gunTask;
	private PlayerState playerState;
	private Structure<? extends Scheme> playerStructure;
	private HashMap<MSPlayer, Double> receivedDamage = Maps.newHashMap();
	private MSPlayer lastDamageSource;
	private Equipment lastDamageWeapon;
	private HashMap<Object, Long> cooldowns = Maps.newHashMap();
	private float recoil;
	private long recoilSetTime, jumpTime, landTime;
	private int heldItemSlot;
	private double speed;
	private boolean inAir;
	private Scene lazyScene;
	private Integer $rankXP;
	private Rank $rank;
	
	public MSPlayer(MSPlayerDataContainer dataContainer)
	{
		Preconditions.checkNotNull(dataContainer);
		dataContainer.getInventory().addDefaults();

		this.dataContainer = dataContainer;
		this.playerState = PlayerState.LOBBY_SERVER;
		this.hotbarContainer = new HotbarContainer();
		this.armorContainer = new ArmorContainer();
	}
	
	public MSPlayer(String playerName)
	{
		this(new MSPlayerDataContainer(new MSPlayerData(playerName), new InventoryContainer().addDefaults()));
	}

	public MSPlayer(Player player)
	{
		this(player.getName());

		this.player = player;
	}
	
	public void redirectEvent(Event event)
	{
		if(hasPlayerStructure())
			playerStructure.redirect(event, this);
		
		Scene scene = getScene();
		
		scene.redirect(event, this);
	}
	
	public boolean joinScene(Scene scene, SceneQuitReason quitReason, boolean teleport, boolean runOnJoin)
	{
		Scene previousScene = getScene();
		boolean success = previousScene.onQuit(this, quitReason, teleport);
		
		if(!success)
			return false;
		
		if(runOnJoin)
		{
			Scene lobbyOrScene = scene != null ? scene : Lobby.getInstance();
			success = lobbyOrScene.onJoin(this);
			
			if(!success)
				return false;
		}
		
		setScene(scene);
		
		return true;
	}
	
	public boolean joinScene(Scene scene, SceneQuitReason quitReason, boolean teleport)
	{
		return joinScene(scene, quitReason, teleport, true);
	}
	
	public boolean quitScene(SceneQuitReason quitReason, boolean teleport, boolean runOnJoin)
	{
		return joinScene(null, quitReason, teleport, runOnJoin);
	}
	
	public boolean quitScene(SceneQuitReason quitReason, boolean teleport)
	{
		return quitScene(quitReason, teleport, true);
	}
	
	public String getNameTag()
	{
		String prefix = getPrefix();
		String suffix = getSuffix();
		
		return (prefix != null ? prefix : "") + getName() + (suffix != null ? suffix : "");
	}
	
	public void updateNameTag()
	{
		String prefix = getPrefix();
		String suffix = getSuffix();
		
		NametagAPI.setNametagHard(getName(), prefix, suffix);
	}
	
	public void updatePrefix()
	{
		String prefix = getPrefix();
		
		NametagAPI.setPrefix(getName(), prefix);
	}
	
	public String getPrefix()
	{
		String prefix = "";
		String rankPrefix = getRankPrefix();
		
		if(rankPrefix != null)
			prefix += rankPrefix;
		
		Scene scene = getScene();
		String scenePrefix = scene.getPrefix(this);
		
		if(scenePrefix != null)
			prefix += scenePrefix;
		
		if(prefix.length() > 16)
			prefix = prefix.substring(0, 16);
		
		return prefix;
	}
	
	public String getRankPrefix()
	{
		Rank rank = getRank();
		
		if(rank == null)
			return "";
		
		return ChatColor.DARK_GRAY + "[" + ChatColor.RESET + rank.getTag() + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	}
	
	public void updateSuffix()
	{
		String suffix = getSuffix();
		
		NametagAPI.setSuffix(getName(), suffix);
	}
	
	public String getSuffix()
	{
		String suffix = "";
		Scene scene = getScene();
		String sceneSuffix = scene.getSuffix(this);
		
		if(sceneSuffix != null)
			suffix += sceneSuffix;
		
		if(suffix.length() > 16)
			suffix = suffix.substring(0, 16);
		
		return suffix;
	}
	
	public String getTabHeader()
	{
		Scene scene = getScene();
		
		return scene.getTabHeader(this);
	}
	
	public String getTabFooter()
	{
		Scene scene = getScene();
		
		return scene.getTabFooter(this);
	}
	
	public void updateTabHeaderAndFooter()
	{
		String header = getTabHeader();
		String footer = getTabFooter();
		Player player = getPlayer();
		
		TabHeader.send(header, footer, player);
	}
	
	public void showRankInfo(long ticks)
	{
		Player player = getPlayer();
		String[] rankDisplay = getRankDisplay();
		String rankTitle = getRankTitle();
		String rankSubtitle = getRankSubtitle();
		
		PlayerDisplay display = new TimedPlayerDisplay(player)
				.startCountdown(ticks)
				.setDistance(2)
				.setLines(rankTitle, rankSubtitle, rankDisplay);
		
		DynamicDisplays.setDisplay(player, display);
	}
	
	private static final ChatColor RANK_PROGRESS_BACKGROUND = ChatColor.DARK_GRAY;
	private static final ChatColor[] RANK_PROGRESS_COLORS = new ChatColor[] {
		ChatColor.BLUE,
		ChatColor.GREEN,
		ChatColor.YELLOW,
		ChatColor.RED,
		ChatColor.LIGHT_PURPLE
	};
	
	public String[] getRankDisplay()
	{
		Rank rank = getRank();
		long lastRequiredXP = rank != null ? rank.getRequiredXP() : 0;
		Rank nextRank = Rank.getNext(rank);
		String progressBar;
		String bottom;
		
		if(nextRank == null)
			progressBar = bottom = "";
		else
		{
			long requiredXP = nextRank.getRequiredXP();
			long xp = getXP();
			long relativeXP = xp - lastRequiredXP;
			long relativeRequiredXP = requiredXP - lastRequiredXP;
			double progress = (double) relativeXP / (double) relativeRequiredXP;
			int progressBarLength = RANK_PROGRESS_COLORS.length * 8;
			progressBar = "";
			
			for(double d = 0; d < 1; d += 1D / progressBarLength)
			{
				ChatColor color;
				
				if(d < progress)
					color = RANK_PROGRESS_COLORS[(int) (d * RANK_PROGRESS_COLORS.length)];
				else
					color = RANK_PROGRESS_BACKGROUND;
				
				progressBar += color.toString() + '|';
			}
			
			bottom = Translation.DISPLAY_RANK_BOTTOM.getMessage(relativeXP, relativeRequiredXP);
		}
		
		return new String[] {
			rank != null ? rank.getTag() : Translation.DISPLAY_RANK_NOTYET_1.getMessage(),
			rank != null ? rank.getName() : Translation.DISPLAY_RANK_NOTYET_2.getMessage(),
			progressBar,
			bottom
		};
	}
	
	public String getRankTitle()
	{
		Rank rank = getRank();
		
		if(rank == null)
			return null;
		
		return Translation.DISPLAY_RANK_TITLE.getMessage(rank.getTag(), rank.getName());
	}
	
	public String getRankSubtitle()
	{
		Rank rank = getRank();
		
		if(rank == null)
			return Translation.DISPLAY_RANK_NOTYET_1.getMessage();
		
		Rank nextRank = Rank.getNext(rank);
		
		if(nextRank == null)
			return null;
		
		long lastRequiredXP = rank.getRequiredXP();
		long requiredXP = nextRank.getRequiredXP();
		long xp = getXP();
		long relativeXP = xp - lastRequiredXP;
		long relativeRequiredXP = requiredXP - lastRequiredXP;
		double progress = (double) relativeXP / (double) relativeRequiredXP;
		int progressBarLength = RANK_PROGRESS_COLORS.length * 8;
		String progressBar = "";
		
		for(double d = 0; d < 1; d += 1D / progressBarLength)
		{
			ChatColor color;
			
			if(d < progress)
				color = RANK_PROGRESS_COLORS[(int) (d * RANK_PROGRESS_COLORS.length)];
			else
				color = RANK_PROGRESS_BACKGROUND;
			
			progressBar += color.toString() + '|';
		}
		
		return Translation.DISPLAY_RANK_SUBTITLE.getMessage(progressBar, relativeXP, relativeRequiredXP);
	}

	public float updateMovementSpeed()
	{
		float speed = getMovementSpeed(heldItemSlot);
		Player player = getPlayer();

		player.setWalkSpeed(speed);

		return speed;
	}

	public float getMovementSpeed(int heldSlot)
	{
		Scene scene = getScene();
		
		if(scene instanceof Game)
		{
			Equipment equipment = hotbarContainer.getHeld(heldSlot);
			
			if(equipment != null)
			{
				return equipment.getMovementSpeed(this);
			}
		}
		
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	public float getMovementSpeed()
	{
		return getMovementSpeed(getPlayer().getInventory().getHeldItemSlot());
	}
	
	public Player getPlayer()
	{
		if(player == null)
			player = Bukkit.getPlayerExact(getName());
		
		return player;
	}
	
	public void kill()
	{
		getPlayer().setHealth(0.0);
	}
	
	public Location spawn(boolean teleport)
	{
		Scene scene = getScene();
		
		return scene.spawn(this, teleport);
	}
	
	public void respawn()
	{
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer) getPlayer()).getHandle().playerConnection.a(packet);
	}
	
	public void respawnDelayed()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), MSPlayer.this::respawn);
	}
	
	public void sendMessage(String string)
	{
		getPlayer().sendMessage(string);
	}

	public void setCustomData(String key, Object value)
	{
		int index = 0;
		String trace = "";
		StackTraceElement[] elements = new RuntimeException().getStackTrace();
		
		for(StackTraceElement element : elements)
		{
			if(element == null)
				continue;
			
			String path = element.getClassName();
			
			if(path.contains("cz.minestrike.me.limeth.minestrike"))
			{
				if(index > 0)
					trace += "\n\t" + element;
				
				if(index >= 3)
					break;
				
				index++;
			}
		}
		
		MineStrike.debug("{" + getName() + "} " + key + " -> " + value + trace);
		
		customData.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCustomData(String key)
	{
		return (T) customData.get(key);
	}
	
	public <T> T getCustomData(String key, T ifNull)
	{
		T customData = getCustomData(key);
		
		return customData != null ? customData : ifNull;
	}
	
	public boolean hasCustomData(String key)
	{
		return customData.containsKey(key);
	}
	
	public boolean hasGunTask()
	{
		return gunTask != null;
	}

	public GunTask getGunTask()
	{
		return gunTask;
	}

	public void setGunTask(GunTask gunTask)
	{
		this.gunTask = gunTask;
	}
	
	public String getName()
	{
		return dataContainer.getData().getUsername();
	}

	public void load(boolean compare)
	{
		String playerName = getName();

		if(compare && previousDataContainer == null)
			previousDataContainer = dataContainer;

		dataContainer = MSPlayerDAO.loadDataContainer(playerName);

		if(compare)
		{
			Player player = getPlayer();
			List<String> comparison = dataContainer.generateComparison(previousDataContainer);
			previousDataContainer = null;

			if(comparison != null && comparison.size() > 0)
			{
				StringBuilder changes = new StringBuilder();
				boolean first = true;

				for(String string : comparison)
				{
					if(first)
						first = false;
					else
						changes.append('\n');

					changes.append(string);
				}

				player.sendMessage(Translation.DATA_COMPARISON.getMessage(changes.toString()));
			}
		}
	}
	
	public void save(boolean compare)
	{
		MSPlayerData data = dataContainer.getData();

		data.setPlaytime(data.getPlaytime() + pullCurrentPlaytime());
		MSPlayerDAO.saveDataContainer(dataContainer);

		if(compare)
			previousDataContainer = dataContainer;
	}

	public void reload()
	{
		MSPlayerDAO.reloadDataContainer(dataContainer);
	}
	
	public void clearTemporaryContainers()
	{
		clearHotbar();
		clearArmor();
	}
	
	public void clearInventory()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		
		inv.clear();
		inv.setArmorContents(new ItemStack[4]);
	}

	public void clearArmor()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();

		inv.setArmorContents(new ItemStack[4]);
		armorContainer.clear();
	}
	
	public void clearHotbar()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		
		for(int i = 0; i < MSConstant.INVENTORY_WIDTH; i++)
			inv.setItem(i, null);
		
		hotbarContainer.clear();
	}
	
	public void damage(double amount, MSPlayer damager, Equipment weapon, BodyPart bodyPart)
	{
		if(bodyPart != null)
			amount = bodyPart.modifyDamage(amount);
		
		Player bukkitVictim = getPlayer();
		Player bukkitDamager = damager.getPlayer();
		amount = armorContainer.reduceDamage(this, amount, weapon, bodyPart, false);
		EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(bukkitDamager, bukkitVictim, DamageCause.CUSTOM, amount);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(event);
		
		if(event.isCancelled())
			return;
		
		armorContainer.reduceDamage(this, amount, weapon, bodyPart, true);
		setLastDamageSource(damager);
		setLastDamageWeapon(weapon);
		addReceivedDamage(damager, amount);
		player.setNoDamageTicks(0);
		player.damage(amount);
	}
	
	public boolean hasCooldown(Object object, long durationMillis, boolean set)
	{
		Long lastSet = cooldowns.get(object);
		long now = System.currentTimeMillis();
		
		if(lastSet == null || lastSet < now - durationMillis)
		{
			if(set)
				cooldowns.put(object, now);
			
			return false;
		}
		
		return true;
	}
	
	public void setCooldown(Object object)
	{
		long now = System.currentTimeMillis();
		
		cooldowns.put(object, now);
	}
	
	public void teleport(Location loc, boolean loadChunks)
	{
		if(loadChunks)
		{
			Chunk chunk = loc.getChunk();
			
			if(!chunk.isLoaded())
				chunk.load();
		}
		
		getPlayer().teleport(loc);
	}
	
	public void teleport(Location loc)
	{
		teleport(loc, true);
	}
	
	public Vector getInaccuracyVector(Gun gun)
	{
		float inaccuracy = getInaccuracy(gun);
		Vector vec = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

		vec.multiply(inaccuracy * INACCURACY_MODIFIER / (vec.length() * MAXIMAL_INACCURACY));
		
		return vec;
	}
	
	public float getInaccuracy(Gun gun)
	{
		Player player = getPlayer();
		Location loc = player.getLocation().clone();
		Block block = loc.getBlock();
		loc.add(0, -0.0000000000001, 0);
		Block secondBlock = loc.getBlock();
		Material blockType = block.getType();
		Material secondBlockType = secondBlock.getType();
		float base;
		
		if(blockType == Material.LADDER && secondBlockType == Material.LADDER)
		{
			base = gun.getInaccuracyLadder();
		}
		else if(player.isSneaking())
		{
			base = gun.getInaccuracySneak();
		}
		else
		{
			base = gun.getInaccuracyStand();
		}
		
		if(inAir)
		{
			float inAirDuration = (float) (System.currentTimeMillis() - jumpTime) / 1000F;
			
			if(inAirDuration < JUMP_INACCURACY_DURATION)
			{
				base += 100 * gun.getInaccuracyJump() * (JUMP_INACCURACY_DURATION - inAirDuration);
			}
		}
		else
		{
			float onGroundDuration = (float) (System.currentTimeMillis() - landTime) / 1000F;
			
			if(onGroundDuration < LAND_INACCURACY_DURATION)
			{
				base += 100 * gun.getInaccuracyLand() * (LAND_INACCURACY_DURATION - onGroundDuration);
			}
		}
		
		float maxPlayerSpeed = getMovementSpeed();
		base += speed * gun.getInaccuracyMove() / maxPlayerSpeed;
		
		return base < MAXIMAL_INACCURACY ? base : MAXIMAL_INACCURACY;
	}
	
	public void modifyByRecoil(Vector vec, Gun gun)
	{
		float recoil = getRecoil();
		double y = vec.getY();
		double z = vec.getZ();
		double theta = recoil / 100; //Pitch
		double thetaSin = Math.sin(theta);
		double thetaCos = Math.cos(theta);
		
		vec.setY(y * thetaCos - z * thetaSin);
		vec.setZ(z * thetaCos + y * thetaSin);
		
		double x = vec.getX();
		z = vec.getZ();
		double gamma = theta * (Math.random() - 0.5);
		double gammaSin = Math.sin(gamma);
		double gammaCos = Math.cos(gamma);
		
		vec.setX(x * gammaCos - z * gammaSin);
		vec.setZ(z * gammaCos + x * gammaSin);
	}
	
	/*
var rotateY3D = function(theta) {
    var sin_t = sin(theta);
    var cos_t = cos(theta);
    
    for (var n=0; n<nodes.length; n++) {
        var node = nodes[n];
        var x = node[0];
        var z = node[2];
        node[0] = x * cos_t - z * sin_t;
        node[2] = z * cos_t + x * sin_t;
    }
};

var rotateX3D = function(theta) {
    var sin_t = sin(theta);
    var cos_t = cos(theta);
    
    for (var n=0; n<nodes.length; n++) {
        var node = nodes[n];
        var y = node[1];
        var z = node[2];
        node[1] = y * cos_t - z * sin_t;
        node[2] = z * cos_t + y * sin_t;
    }
};
	 */
	
	public float increaseRecoil(float by)
	{
		setRecoil(getRecoil() + by);
		
		return recoil;
	}
	
	public float getRecoil()
	{
		long now = System.currentTimeMillis();
		long delay = now - recoilSetTime;
		float inaccuracy = this.recoil - RECOIL_RESTORATION_PER_SECOND * (delay / 1000F);
		
		return inaccuracy > 0 ? inaccuracy : 0;
	}

	public void setRecoil(float recoil)
	{
		this.recoil = recoil < MAXIMAL_RECOIL ? recoil : MAXIMAL_RECOIL;
		this.recoilSetTime = System.currentTimeMillis();
	}

	public long getRecoilSetTime()
	{
		return recoilSetTime;
	}

	public void setRecoilSetTime(long recoilSetTime)
	{
		this.recoilSetTime = recoilSetTime;
	}

	public double getSpeed()
	{
		return speed;
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	public boolean isInAir()
	{
		return inAir;
	}

	public void setInAir(boolean inAir)
	{
		this.inAir = inAir;
	}
	
	public Scene getScene()
	{
		return lazyScene != null ? lazyScene : Lobby.getInstance();
	}
	
	public void setScene(Scene scene)
	{
		this.lazyScene = scene;
	}
	
	private long pullCurrentPlaytime()
	{
		if(joinedAt == null)
			throw new IllegalStateException("The player has not joined!");
		
		long now = System.currentTimeMillis();
		Long playtime = now - joinedAt;
		joinedAt = now;
		
		return playtime;
	}
	
	public void setXP(int xp, boolean updateNametag)
	{
		dataContainer.getData().setXp(xp < 0 ? 0 : xp);
		
		if(updateNametag)
			updateNameTag();
	}
	
	public void setXP(int xp)
	{
		setXP(xp, true);
	}
	
	public int getXP()
	{
		return dataContainer.getData().getXp();
	}
	
	public Rank getRank()
	{
		int xp = getXP();
		
		if($rankXP == null || $rankXP != xp)
		{
			$rank = Rank.getForXP(xp);
			$rankXP = xp;
		}
		
		return $rank;
	}
	
	public int addXP(int amount, boolean notifyXP, boolean notifyLevel)
	{
		if(amount == 0)
			return getXP();
		
		int newXP = getXP() + amount;
		Player player = getPlayer();
		Rank oldRank = notifyLevel ? getRank() : null;
		
		setXP(newXP);
		
		if(player != null && player.isOnline())
		{
			if((notifyXP || notifyLevel) && amount > 0)
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if(notifyXP)
				if(amount > 0)
					ActionBarAPI.sendActionBar(Translation.XP_GAIN.getMessage(amount), player);
				else
					ActionBarAPI.sendActionBar(Translation.XP_LOSS.getMessage(-amount), player);
			
			if(notifyLevel)
			{
				Rank newRank = getRank();
				
				if(newRank != oldRank)
					if(amount > 0)
						Title.send(player, null, Translation.XP_LEVEL_UPGRADE.getMessage(newRank.getName()));
					else
						Title.send(player, null, Translation.XP_LEVEL_DOWNGRADE.getMessage(newRank.getName()));
			}
		}
		
		return newXP;
	}
	
	public int addXP(int amount, boolean notify)
	{
		return addXP(amount, notify, notify);
	}
	
	public int addXP(int amount)
	{
		return addXP(amount, true);
	}
	
	public int getKills()
	{
		return dataContainer.getData().getKills();
	}
	
	public void setKills(int kills)
	{
		dataContainer.getData().setKills(kills);
	}
	
	public int addKills(int amount)
	{
		int kills = getKills() + amount;
		
		setKills(kills);
		
		return kills;
	}
	
	public int getAssists()
	{
		return dataContainer.getData().getAssists();
	}
	
	public void setAssists(int assists)
	{
		dataContainer.getData().setAssists(assists);
	}
	
	public int addAssists(int amount)
	{
		int assists = getAssists() + amount;
		
		setAssists(assists);
		
		return assists;
	}
	
	public int getDeaths()
	{
		return dataContainer.getData().getDeaths();
	}
	
	public void setDeaths(int deaths)
	{
		dataContainer.getData().setDeaths(deaths);
	}
	
	public int addDeaths(int amount)
	{
		int deaths = getDeaths() + amount;
		
		setDeaths(deaths);
		
		return deaths;
	}
	
	public PlayerState getPlayerState()
	{
		return playerState;
	}

	public void setPlayerState(PlayerState playerState)
	{
		Validate.notNull(playerState, "The player state must not be null");
		
		this.playerState = playerState;
	}
	
	public boolean hasPlayerStructure()
	{
		return playerStructure != null;
	}

	public Structure<? extends Scheme> getPlayerStructure()
	{
		return playerStructure;
	}

	public void setPlayerStructure(Structure<?> playerStructure)
	{
		this.playerStructure = playerStructure;
	}

	public HashMap<String, Object> getCustomData()
	{
		return customData;
	}
	
	public InventoryContainer getInventoryContainer()
	{
		return dataContainer.getInventory();
	}

	public HotbarContainer getHotbarContainer()
	{
		return hotbarContainer;
	}

	@SuppressWarnings("unchecked")
	public <T extends Equipment> T getEquipmentInHand() throws IllegalStateException
	{
		return (T) hotbarContainer.getHeld(this);
	}

	public ItemStack getItemInHand()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = getHeldItemSlot();

		return inv.getItem(slot);
	}
	
	public double getReceivedDamage(MSPlayer msPlayer)
	{
		Double dmg = receivedDamage.get(msPlayer);
		
		return dmg != null ? dmg : 0;
	}
	
	public void setReceivedDamage(MSPlayer msPlayer, double damage)
	{
		if(msPlayer == this)
			return;
		
		receivedDamage.put(msPlayer, damage);
	}
	
	public Double removeReceivedDamage(MSPlayer msPlayer)
	{
		return receivedDamage.remove(msPlayer);
	}
	
	public Double addReceivedDamage(MSPlayer msPlayer, double amount)
	{
		if(msPlayer == this)
			return null;
		
		double dmg = getReceivedDamage(msPlayer) + amount;
		
		setReceivedDamage(msPlayer, dmg);
		
		return dmg;
	}
	
	public void clearReceivedDamage()
	{
		receivedDamage.clear();
	}
	
	public MSPlayer getPlayerAssistedInKill()
	{
		MSPlayer maxDamager = null;
		Double maxDamage = null;
		
		for(Entry<MSPlayer, Double> entry : receivedDamage.entrySet())
		{
			double damage = entry.getValue();
			
			if(maxDamage == null || damage > maxDamage)
			{
				maxDamager = entry.getKey();
				maxDamage = damage;
			}
		}
		
		if(maxDamage == null || maxDamage < 10)
			return null;
		
		return maxDamager;
	}
	
	public MSPlayer getLastDamageSource()
	{
		return lastDamageSource;
	}

	public void setLastDamageSource(MSPlayer lastDamageSource)
	{
		this.lastDamageSource = lastDamageSource;
	}

	public Equipment getLastDamageWeapon()
	{
		return lastDamageWeapon;
	}

	public void setLastDamageWeapon(Equipment lastDamageWeapon)
	{
		this.lastDamageWeapon = lastDamageWeapon;
	}
	
	public ArmorContainer getArmorContainer()
	{
		return armorContainer;
	}

	@SuppressWarnings("deprecation")
	public void updateInventory()
	{
		getPlayer().updateInventory();
	}

	public Long getJoinedAt()
	{
		return joinedAt;
	}

	public void setJoinedAt(Long joinedAt)
	{
		this.joinedAt = joinedAt;
	}
	
	public long getPlaytime()
	{
		return dataContainer.getData().getPlaytime();
	}
	
	public void setPlaytime(long playtime)
	{
		dataContainer.getData().setPlaytime(playtime);
	}

	public int getHeldItemSlot()
	{
		return heldItemSlot;
	}

	public void setHeldItemSlot(int heldItemSlot)
	{
		this.heldItemSlot = heldItemSlot;
	}

	public void clearPotionEffects()
	{
		Player player = getPlayer();

		for(PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}

	@Override
	public String toString()
	{
		return "MSPlayer{ " + player.toString() + " }";
	}
}
