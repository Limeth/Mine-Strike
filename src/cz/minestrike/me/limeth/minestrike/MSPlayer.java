package cz.minestrike.me.limeth.minestrike;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.server.v1_7_R1.EnumClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Firing;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunManager;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Reloading;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.PlayerState;
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
																.build();
	private static Integer MOVEMENT_LOOP_ID;
	
	public static HashSet<MSPlayer> getOnlinePlayers()
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
	
	private final String playerName;
	private RecordData data;
	private Player player;
	private final HashMap<String, Object> customData = new HashMap<String, Object>();
	private Location lastLocation;
	private GunTask gunTask;
	private PlayerState playerState;
	private Structure<? extends Scheme> playerStructure;
	private float recoil;
	private long recoilSetTime, jumpTime, landTime;
	private double speed;
	private boolean inAir;
	
	//Game stuff
	private Game<?, ?, ?, ?> game;
	
	public MSPlayer(String playerName, RecordData data)
	{
		data.set("username", playerName);
		
		this.playerName = playerName;
		this.data = data;
		this.playerState = PlayerState.LOBBY_SERVER;
	}
	
	public void redirectEvent(Event event)
	{
		if(hasGame())
			game.redirect(event, this);
		
		if(hasPlayerStructure())
			playerStructure.redirect(event, this);
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
		if(hasGame())
		{
			EquipmentManager em = game.getEquipmentManager();
			Equipment equipment = em.getCurrentlyEquipped(this);
			
			if(equipment != null)
				return equipment.getMovementSpeed(this);
		}
		
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	public Player getPlayer()
	{
		if(player == null)
			player = Bukkit.getPlayerExact(playerName);
		
		return player;
	}
	
	public Location spawn(boolean teleport)
	{
		if(hasGame())
			return getGame().spawn(this, teleport);
		else
		{
			Location loc = MSConfig.getWorld().getSpawnLocation();
			Player player = getPlayer();
			
			player.teleport(loc);
			
			return loc;
		}
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
		
		GunType gunType = gun.getType();
		
		if(gunType.isLoadingContinuously() && gunTask instanceof Reloading)
		{
			gunTask.remove();
		}
		
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
			shoot(gunType);
			gun.apply(player.getItemInHand());
		}
	}
	
	public void reload(Gun gun)
	{
		GunType gunType = gun.getType();
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		
		gun.setReloading(true);
		
		ItemStack is = gun.createItemStack();
		
		inv.setItem(slot, is);
		setGunTask(new Reloading(this, slot, gunType).startLoop());
	}
	
	public void shoot(GunType type)
	{
		Player player = getPlayer();
		
		if(player == null)
			return;
		
		Location location = player.getEyeLocation();
		
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
		
		service.save(data, MSConfig.getMySQLTablePlayers());
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
	
	public boolean hasGame()
	{
		return game != null;
	}

	public Game<?, ?, ?, ?> getGame()
	{
		return game;
	}

	public void setGame(Game<?, ?, ?, ?> game)
	{
		this.game = game;
	}

	public RecordData getData()
	{
		return data;
	}

	public void setData(RecordData data)
	{
		this.data = data;
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
}
