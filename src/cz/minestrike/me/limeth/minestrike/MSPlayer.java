package cz.minestrike.me.limeth.minestrike;

import ca.wacos.nametagedit.NametagAPI;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerData;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.Firing;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.Reloading;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.lobby.Lobby;
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
import org.bukkit.util.Vector;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

public class MSPlayer
{
	private static final float
			MAXIMAL_INACCURACY = 100,
			INACCURACY_MODIFIER = 0.1F,
			JUMP_INACCURACY_DURATION = 1,
			LAND_INACCURACY_DURATION = 1,
			RECOIL_RESTORATION_PER_SECOND = 2,
			MAXIMAL_RECOIL = 1;
	private static final HashSet<MSPlayer> ONLINE_PLAYERS = new HashSet<MSPlayer>();

	private static Integer MOVEMENT_LOOP_ID;
	
	public static HashSet<MSPlayer> getOnlinePlayers()
	{
		return ONLINE_PLAYERS;
	}
	
	public static Set<MSPlayer> getOnlinePlayers(Predicate<? super MSPlayer> predicate)
	{
		return ONLINE_PLAYERS;
	}
	
	public static boolean remove(Player player)
	{
		MSPlayer msPlayer = get(player);
		
		if(msPlayer != null)
			return remove(msPlayer);
		else
			return false;
	}
	
	public static boolean remove(MSPlayer player)
	{
		return ONLINE_PLAYERS.remove(player);
	}
	
	public static MSPlayer get(String playerName, boolean register)
	{
		for(MSPlayer msPlayer : ONLINE_PLAYERS)
			if(msPlayer.getName().equals(playerName))
				return msPlayer;
		
		if(!register)
			return null;
		
		MSPlayer msPlayer;
		
		try
		{
			msPlayer = load(playerName);
		}
		catch(SQLException e)
		{
			MineStrike.warn("An error occured while loading player data for player '" + playerName + "'.");
			e.printStackTrace();
			return null;
		}
		
		if(msPlayer == null)
			msPlayer = new MSPlayer(playerName);
		
		register(msPlayer);
		
		return msPlayer;
	}
	
	public static MSPlayer get(String playerName)
	{
		return get(playerName, false);
	}
	
	public static MSPlayer load(String playerName) throws SQLException
	{
		MSPlayerDAO dao = MineStrike.getDBI().open(MSPlayerDAO.class);
		MSPlayerData data = dao.selectData(MSConfig.getMySQLTablePlayers(), playerName);

		if(data == null)
			return new MSPlayer(playerName);

		Collection<Equipment> equipment = dao.selectEquipment(MSConfig.getMySQLTableEquipment(), playerName);
		InventoryContainer container = new InventoryContainer(equipment);

		dao.close();
		
		return new MSPlayer(data, container);
	}
	
	public static boolean register(MSPlayer msPlayer)
	{
		msPlayer.setJoinedAt(System.currentTimeMillis());
		
		return ONLINE_PLAYERS.add(msPlayer);
	}
	
	public static MSPlayer get(Player player)
	{
		return get(player.getName());
	}
	
	public static void loadOnlinePlayers()
	{
		for(Player player : Bukkit.getOnlinePlayers())
			get(player.getName(), true);
	}
	
	public static void clearOnlinePlayers()
	{
		ONLINE_PLAYERS.clear();
	}
	
	public static int startMovementLoop()
	{
		return MOVEMENT_LOOP_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				for(MSPlayer msPlayer : getOnlinePlayers())
				{
					Player player = msPlayer.getPlayer();
					Location loc = player.getLocation();
					Location lastLoc = msPlayer.lastLocation;
					msPlayer.lastLocation = loc;
					
					if(lastLoc == null)
						continue;
					
					msPlayer.setSpeed(lastLoc.distance(loc));
					
					boolean onGround = ((CraftPlayer) player).isOnGround();
					
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
			}
		}, 1L, 0L);
	}
	
	public static void stopMovementLoop()
	{
		Bukkit.getScheduler().cancelTask(MOVEMENT_LOOP_ID);
	}
	
	private final HashMap<String, Object> customData = new HashMap<String, Object>();
	private final MSPlayerData data;
	private Player player;
	private InventoryContainer inventoryContainer;
	private HotbarContainer hotbarContainer;
	private ArmorContainer armorContainer;
	private Long joinedAt;
	private Location lastLocation;
	private GunTask gunTask;
	private PlayerState playerState;
	private Structure<? extends Scheme> playerStructure;
	private HashMap<MSPlayer, Double> receivedDamage = new HashMap<MSPlayer, Double>();
	private MSPlayer lastDamageSource;
	private Equipment lastDamageWeapon;
	private HashMap<Object, Long> cooldowns = new HashMap<Object, Long>();
	private float recoil;
	private long recoilSetTime, jumpTime, landTime;
	private double speed;
	private boolean inAir;
	private Scene lazyScene;
	private Integer $rankXP;
	private Rank $rank;
	
	public MSPlayer(MSPlayerData data, InventoryContainer inventoryContainer)
	{
		this.data = data;
		this.inventoryContainer = inventoryContainer;
		this.playerState = PlayerState.LOBBY_SERVER;
		this.hotbarContainer = new HotbarContainer();
		this.armorContainer = new ArmorContainer();
	}
	
	public MSPlayer(String playerName)
	{
		this(new MSPlayerData(playerName), new InventoryContainer());
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
		
		long lastRequiredXP = rank != null ? rank.getRequiredXP() : 0;
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
	
	public float updateMovementSpeed(int heldSlot)
	{
		float speed = getMovementSpeed(heldSlot);
		Player player = getPlayer();
		
		player.setWalkSpeed(speed);
		
		return speed;
	}
	
	public float updateMovementSpeed()
	{
		return updateMovementSpeed(getPlayer().getInventory().getHeldItemSlot());
	}
	
	public float getMovementSpeed(int heldSlot)
	{
		Scene scene = getScene();
		
		if(scene instanceof Game)
		{
			Equipment equipment = hotbarContainer.getHeld(heldSlot);
			
			if(equipment != null)
			{
				float speed = equipment.getMovementSpeed(this);
				
				return speed;
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
		final MSPlayer that = this;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				that.respawn();
			}
		});
	}
	
	public void sendMessage(String string)
	{
		getPlayer().sendMessage(string);
	}
	
	public void pressTrigger(Gun gun)
	{
		if(!gun.isLoaded())
			return;
		
		GunType gunType = gun.getEquipment();
		
		if(!gun.isAutomatic() && !gun.isShotDelaySatisfied())
			return;
		
		if(gunType.isLoadingContinuously() && gunTask instanceof Reloading)
			gunTask.remove();
		
		if(gun.isAutomatic())
		{
			if(gunTask == null)
				gunTask = new Firing(this, gun).startLoop();
			else if(gunTask instanceof Firing)
			{
				((Firing) gunTask).setLastTimeFired(System.currentTimeMillis());
			}
		}
		else
		{
			if(gunTask != null)
				if(gunTask instanceof Firing)
					gunTask.remove();
				else
					return;
			
			Player player = getPlayer();
			
			gun.decreaseLoadedBullets();
			shoot(gun);
			gun.apply(player.getItemInHand(), this);
		}
	}
	
	public void reload(Gun gun)
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		
		gun.setReloading(true);
		
		ItemStack is = gun.newItemStack(this);
		
		inv.setItem(slot, is);
		setGunTask(new Reloading(this, gun).startLoop());
	}
	
	public void shoot(Gun gun)
	{
		Player player = getPlayer();
		
		if(player == null)
			return;
		
		Location location = player.getEyeLocation();
		String shootSound = gun.getSoundShooting(this);
		
		gun.setLastBulletShotAt();
		SoundManager.play(shootSound, location, Bukkit.getOnlinePlayers());
		gun.shoot(this);
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
	
	public Object getCustomData(String key)
	{
		return customData.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCustomData(Class<T> clazz, String key)
	{
		return (T) getCustomData(key);
	}
	
	public <T> T getCustomData(Class<T> clazz, String key, T ifNull)
	{
		T customData = getCustomData(clazz, key);
		
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
		return data.getUsername();
	}
	
	public void save()
	{
		DBI dbi = MineStrike.getDBI();
		Handle handle = dbi.open();
		MSPlayerDAO dao = handle.attach(MSPlayerDAO.class);
		String playerName = getName();

		dao.clearEquipment(MSConfig.getMySQLTableEquipment(), playerName);
		dao.insertData(MSConfig.getMySQLTablePlayers(), data);
		dao.insertEquipment(MSConfig.getMySQLTableEquipment(), playerName, getInventoryContainer());
		dao.close();
	}
	
	public void clearContainers()
	{
		hotbarContainer.clear();
		hotbarContainer.apply(this);
		armorContainer.clear();
		armorContainer.apply(this);
	}
	
	public void clearInventory()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		
		inv.clear();
		inv.setArmorContents(new ItemStack[4]);
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
	
	private Long pullCurrentPlaytime()
	{
		if(joinedAt == null)
			return null;
		
		long now = System.currentTimeMillis();
		Long playtime = now - joinedAt;
		joinedAt = now;
		
		return playtime;
	}
	
	public void setXP(int xp, boolean updateNametag)
	{
		data.setXp(xp < 0 ? 0 : xp);
		
		if(updateNametag)
			updateNameTag();
	}
	
	public void setXP(int xp)
	{
		setXP(xp, true);
	}
	
	public int getXP()
	{
		return data.getXp();
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
		return data.getKills();
	}
	
	public void setKills(int kills)
	{
		data.setKills(kills);
	}
	
	public int addKills(int amount)
	{
		int kills = getKills() + amount;
		
		setKills(kills);
		
		return kills;
	}
	
	public int getAssists()
	{
		return data.getAssists();
	}
	
	public void setAssists(int assists)
	{
		data.setAssists(assists);
	}
	
	public int addAssists(int amount)
	{
		int assists = getAssists() + amount;
		
		setAssists(assists);
		
		return assists;
	}
	
	public int getDeaths()
	{
		return data.getDeaths();
	}
	
	public void setDeaths(int deaths)
	{
		data.setDeaths(deaths);
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
		return inventoryContainer;
	}

	public HotbarContainer getHotbarContainer()
	{
		return hotbarContainer;
	}
	
	public Equipment getEquipmentInHand()
	{
		return hotbarContainer.getHeld(this);
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
		return data.getPlaytime();
	}
	
	public void setPlaytime(long playtime)
	{
		data.setPlaytime(playtime);
	}
}
