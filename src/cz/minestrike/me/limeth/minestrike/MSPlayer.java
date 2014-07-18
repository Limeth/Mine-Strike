package cz.minestrike.me.limeth.minestrike;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.server.v1_7_R1.EnumClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import ca.wacos.nametagedit.NametagAPI;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Firing;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunManager;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Reloading;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.lobby.Lobby;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.storagemanager.Record;
import cz.minestrike.me.limeth.storagemanager.RecordData;
import cz.minestrike.me.limeth.storagemanager.RecordStructure;
import cz.minestrike.me.limeth.storagemanager.RecordStructure.RecordStructureBuilder;
import cz.minestrike.me.limeth.storagemanager.RecordStructureColumn;
import cz.minestrike.me.limeth.storagemanager.mysql.MySQLService;

public class MSPlayer implements Record
{
	private static final float
			MAXIMAL_INACCURACY = 100,
			INACCURACY_MODIFIER = 0.1F,
			JUMP_INACCURACY_DURATION = 1,
			LAND_INACCURACY_DURATION = 1,
			RECOIL_RESTORATION_PER_SECOND = 2,
			MAXIMAL_RECOIL = 1;
	private static final HashSet<MSPlayer> ONLINE_PLAYERS = new HashSet<MSPlayer>();
	public static final RecordStructure RECORD_STRUCTURE = new RecordStructureBuilder()
																.setPrimaryKey("username")
																.addColumn(new RecordStructureColumn(String.class, "username", 16))
																.addColumn(new RecordStructureColumn(Integer.class, "kills"))
																.addColumn(new RecordStructureColumn(Integer.class, "deaths"))
																.addColumn(new RecordStructureColumn(Integer.class, "balance"))
																.addColumn(new RecordStructureColumn(Integer.class, "level"))
																.addColumn(new RecordStructureColumn(Long.class, "playtime"))
																.addColumn(new RecordStructureColumn(String.class, "inventory", (int) Short.MAX_VALUE))
																.build();
	private static Integer MOVEMENT_LOOP_ID;
	
	public static HashSet<MSPlayer> getOnlinePlayers()
	{
		return ONLINE_PLAYERS;
	}
	
	public static Set<MSPlayer> getOnlinePlayers(Predicate<? super MSPlayer> predicate)
	{
		return ONLINE_PLAYERS.stream().filter(predicate).collect(Collectors.toSet());
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
			msPlayer = new MSPlayer(playerName, new RecordData(RECORD_STRUCTURE));
		
		register(msPlayer);
		
		return msPlayer;
	}
	
	public static MSPlayer get(String playerName)
	{
		return get(playerName, false);
	}
	
	public static MSPlayer load(String playerName) throws SQLException
	{
		MySQLService service = MineStrike.getService();
		RecordData[] data = service.load(RECORD_STRUCTURE, MSConfig.getMySQLTablePlayers(), "WHERE `username` = ?", playerName);
		
		if(data.length <= 0)
			return null;
		
		MSPlayer msPlayer = new MSPlayer(playerName, data[0]);
		
		return msPlayer;
	}
	
	public static boolean register(MSPlayer msPlayer)
	{
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
	private final String playerName;
	private RecordData lazyData;
	private Player player;
	private InventoryContainer lazyInventoryContainer;
	private HotbarContainer hotbarContainer;
	private ArmorContainer armorContainer;
	private Location lastLocation;
	private GunTask gunTask;
	private PlayerState playerState;
	private Structure<? extends Scheme> playerStructure;
	private MSPlayer lastDamageSource;
	private Equipment lastDamageWeapon;
	private float recoil;
	private long recoilSetTime, jumpTime, landTime;
	private double speed;
	private boolean inAir;
	private Scene lazyScene;
	
	public MSPlayer(String playerName, RecordData data)
	{
		data.set("username", playerName);
		
		this.playerName = playerName;
		this.lazyData = data;
		this.playerState = PlayerState.LOBBY_SERVER;
		this.hotbarContainer = new HotbarContainer();
		this.armorContainer = new ArmorContainer();
	}
	
	public void redirectEvent(Event event)
	{
		if(hasPlayerStructure())
			playerStructure.redirect(event, this);
		
		Scene scene = getScene();
		
		scene.redirect(event, this);
	}
	
	public String getNameTag()
	{
		String prefix = getPrefix();
		String suffix = getSuffix();
		
		return (prefix != null ? prefix : "") + playerName + (suffix != null ? suffix : "");
	}
	
	public void updateNameTag()
	{
		String prefix = getPrefix();
		String suffix = getSuffix();
		
		NametagAPI.setNametagHard(playerName, prefix, suffix);
	}
	
	public void updatePrefix()
	{
		String prefix = getPrefix();
		
		NametagAPI.setPrefix(playerName, prefix);
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
		
		return prefix;
	}
	
	public String getRankPrefix()
	{
		return "[Rank] ";
	}
	
	public void updateSuffix()
	{
		String suffix = getSuffix();
		
		NametagAPI.setSuffix(playerName, suffix);
	}
	
	public String getSuffix()
	{
		String suffix = "";
		Scene scene = getScene();
		String sceneSuffix = scene.getSuffix(this);
		
		if(sceneSuffix != null)
			suffix += sceneSuffix;
		
		return suffix;
	}
	
	public float updateMovementSpeed()
	{
		float speed = getMovementSpeed();
		Player player = getPlayer();
		
		player.setWalkSpeed(speed);
		
		return speed;
	}
	
	public float getMovementSpeed()
	{
		Scene scene = getScene();
		
		if(scene instanceof Game)
		{
			Game<?, ?, ?, ?> game = (Game<?, ?, ?, ?>) scene;
			EquipmentProvider em = game.getEquipmentProvider();
			Equipment equipment = em.getCurrentlyEquipped(this);
			
			if(equipment != null)
			{
				Equipment type = equipment.getSource();
				float speed = type.getMovementSpeed(this);
				
				return speed;
			}
		}
		
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	public Player getPlayer()
	{
		if(player == null)
			player = Bukkit.getPlayerExact(playerName);
		
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
		
		if(!gunType.isAutomatic() && !gun.isShotDelaySatisfied())
			return;
		
		if(gunType.isLoadingContinuously() && gunTask instanceof Reloading)
			gunTask.remove();
		
		if(gunType.isAutomatic())
		{
			if(gunTask == null)
				gunTask = new Firing(this, getPlayer().getInventory().getHeldItemSlot(), gunType).startLoop();
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
			gun.apply(player.getItemInHand());
		}
	}
	
	public void reload(Gun gun)
	{
		GunType gunType = gun.getEquipment();
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		
		gun.setReloading(true);
		
		ItemStack is = gun.newItemStack(this);
		
		inv.setItem(slot, is);
		setGunTask(new Reloading(this, slot, gunType).startLoop());
	}
	
	public void shoot(Gun gun)
	{
		Player player = getPlayer();
		
		if(player == null)
			return;
		
		GunType type = gun.getEquipment();
		Location location = player.getEyeLocation();
		String shootSound = type.getSoundShooting();
		
		gun.setLastBulletShotAt();
		SoundManager.play(shootSound, location, Bukkit.getOnlinePlayers());
		GunManager.shoot(location, this, type);
	}
	
	public void setCustomData(String key, Object value)
	{
		MineStrike.debug("{" + playerName + "} " + key + " -> " + value);
		
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
		return playerName;
	}

	@Override
	public RecordStructure getStructure()
	{
		return RECORD_STRUCTURE;
	}
	
	public void save() throws SQLException
	{
		MySQLService service = MineStrike.getService();
		
		service.save(getData(), MSConfig.getMySQLTablePlayers());
	}
	
	public void clearContainers()
	{
		hotbarContainer.clear();
		armorContainer.clear();
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
		player.damage(amount);
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
	
	public Vector getInaccuracyVector(GunType gunType)
	{
		float inaccuracy = getInaccuracy(gunType);
		Vector vec = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

		vec.multiply(inaccuracy * INACCURACY_MODIFIER / (vec.length() * MAXIMAL_INACCURACY));
		
		return vec;
	}
	
	public float getInaccuracy(GunType gunType)
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
			base = gunType.getInaccuracyLadder();
		}
		else if(player.isSneaking())
		{
			base = gunType.getInaccuracySneak();
		}
		else
		{
			base = gunType.getInaccuracyStand();
		}
		
		if(inAir)
		{
			float inAirDuration = (float) (System.currentTimeMillis() - jumpTime) / 1000F;
			
			if(inAirDuration < JUMP_INACCURACY_DURATION)
			{
				base += 100 * gunType.getInaccuracyJump() * (JUMP_INACCURACY_DURATION - inAirDuration);
			}
		}
		else
		{
			float onGroundDuration = (float) (System.currentTimeMillis() - landTime) / 1000F;
			
			if(onGroundDuration < LAND_INACCURACY_DURATION)
			{
				base += 100 * gunType.getInaccuracyLand() * (LAND_INACCURACY_DURATION - onGroundDuration);
			}
		}
		
		float maxPlayerSpeed = getMovementSpeed();
		base += speed * gunType.getInaccuracyMove() / maxPlayerSpeed;
		
		return base < MAXIMAL_INACCURACY ? base : MAXIMAL_INACCURACY;
	}
	
	public Vector getRecoilVector(Vector direction, GunType gunType)
	{
	/*	double rawRecoil = getRecoil();
		double magnitude = gunType.getRecoilMagnitude();
		double recoil = magnitude * rawRecoil;
		double xVar = gunType.getRecoilAngleVariance();
		double yVar = gunType.getRecoilMagnitudeVariance();
		double length = direction.length();
		double xDir = direction.getX();
		double yDir = direction.getY();
		double zDir = direction.getZ();
		
		Vector yVec = new Vector(xDir >= 0 ? length - xDir : xDir - length, yDir, zDir >= 0 ? length - zDir : zDir - length);
		
		getPlayer().sendMessage(yVec.toString());
		
		yVec.multiply((Math.random() - 0.5) * 2 * yVar);
		
		Vector vec = yVec.clone().multiply(recoil);
		
		return vec;*/
		
		return new Vector(0, 0, 0);
	}
	
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
	
	public void set(String key, Object value)
	{
		lazyData.set(key, value);
	}
	
	public <T> T get(Class<T> clazz, String key)
	{
		if(key.equals("inventory"))
			updateData();
		
		return lazyData.get(clazz, key);
	}
	
	public <T> T get(Class<T> clazz, String key, T ifNull)
	{
		if(key.equals("inventory"))
			updateData();
		
		return lazyData.get(clazz, key, ifNull);
	}
	
	public RecordData getData()
	{
		updateData();
		
		return lazyData;
	}
	
	private void updateData()
	{
		InventoryContainer invContainer = getInventoryContainer();
		Equipment[] equipment = invContainer.getContents();
		String gsonEquipment = EquipmentManager.toGson(equipment);
		
		lazyData.set("inventory", gsonEquipment);
	}

	public void setData(RecordData data)
	{
		lazyInventoryContainer = null;
		this.lazyData = data;
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
		if(lazyInventoryContainer == null)
		{
			lazyInventoryContainer = new InventoryContainer();
			String string = lazyData.get(String.class, "inventory", "");
			Equipment[] equipment = EquipmentManager.fromGson(string);
			
			for(int i = 0; i < equipment.length; i++)
				lazyInventoryContainer.setItem(i, equipment[i]);
		}
		
		return lazyInventoryContainer;
	}

	public HotbarContainer getHotbarContainer()
	{
		return hotbarContainer;
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
}
