package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.*;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManagerInitializationException;
import cz.minestrike.me.limeth.minestrike.events.*;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.listeners.GameShotMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.listeners.InteractionMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.listeners.InventoryMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.listeners.ShoppingMSListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import net.minecraft.server.v1_7_R4.EntityItem;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Game extends Scene
{
	private static final String SOUND_JOIN = "projectsurvive:counterstrike.ui.valve_logo_music", CUSTOM_DATA_DEAD
			= "MineStrike.game.dead";
	@Expose
	private final GameType                       type;
	@Expose
	private final String                         id;
	@Expose
	private       String                         name;
	private       MSPlayer                       owner;
	private       boolean                        open;
	private       HashSet<MSPlayer>              players;
	private       HashSet<String>                invited;
	@Expose
	private       String                         lobbyId;
	@Expose
	private       String                         menuId;
	@Expose
	private       FilledArrayList<String>        maps;
	private       FilledArrayList<GameMap>       lazyCorrespondingMaps;
	private       Structure<? extends GameLobby> lobbyStructure;
	private       Structure<? extends GameMenu>  menuStructure;
	private       Structure<? extends GameMap>   mapStructure;
	private       GamePhase<? extends Game>      phase;
	private       InventoryMSListener            inventoryListener;
	private       ShoppingMSListener             shoppingListener;
	private       InteractionMSListener          interactionListener;
	private       GameShotMSListener             gameShotListener;
	private       EquipmentProvider              equipmentProvider;
	private       Scoreboard                     scoreboard;
	private       Map<Item, Equipment>           drops;
	private       Map<Block, Double>             blockDamageMap;

	public Game(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId,
				FilledArrayList<String> maps)
	{
		Validate.notNull(gameType, "The type of the game cannot be null!");
		Validate.notNull(id, "The ID must not be null!");
		Validate.notNull(name, "The name must not be null!");
		Validate.notEmpty(name, "The name must not be empty!");
		Validate.notNull(maps, "The map list must not be null!");

		this.type = gameType;
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.open = open;
		this.maps = maps;
		this.lobbyId = lobbyId;
		this.menuId = menuId;

		Collections.shuffle(this.maps);
	}

	public abstract boolean isPlayerPlaying(MSPlayer msPlayer);

	public abstract int getXPForKill(MSPlayer msVictim, MSPlayer msKiller);

	public abstract int getXPForAssist(MSPlayer msVictim, MSPlayer msAssistant);

	public void firstStart()
	{
		FilledArrayList<GameMap> maps = getMaps();

		setMap(maps.get(MSConstant.RANDOM.nextInt(maps.size())));
	}

	public void start()
	{
		if(open && mapStructure == null)
			firstStart();

		Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
	}

	public void joinMenu(MSPlayer msPlayer, boolean spawn)
	{
		msPlayer.setPlayerState(PlayerState.MENU_GAME);
		msPlayer.setPlayerStructure(menuStructure);

		if(spawn)
			msPlayer.spawn(true);
	}

	public void joinMenu(MSPlayer msPlayer)
	{
		joinMenu(msPlayer, true);
	}

	public void joinLobby(MSPlayer msPlayer, boolean spawn)
	{
		msPlayer.setPlayerState(PlayerState.LOBBY_GAME);
		msPlayer.setPlayerStructure(lobbyStructure);

		if(spawn)
			msPlayer.spawn(true);
	}

	public void joinLobby(MSPlayer msPlayer)
	{
		joinLobby(msPlayer, true);
	}

	public boolean quitArena(MSPlayer msPlayer, boolean spawn)
	{
		ArenaQuitEvent event = new ArenaQuitEvent(this, msPlayer);
		PluginManager pm = Bukkit.getPluginManager();

		pm.callEvent(event);

		if(event.isCancelled())
			return false;

		if(owner != null && owner.equals(msPlayer))
			joinLobby(msPlayer, spawn);
		else
			joinMenu(msPlayer, spawn);

		return true;
	}

	public boolean quitArena(MSPlayer msPlayer)
	{
		return quitArena(msPlayer, true);
	}

	@SuppressWarnings("deprecation")
	public void equip(MSPlayer msPlayer, boolean force)
	{
		super.equip(msPlayer, force);

		SceneEquipEvent event = new SceneEquipEvent(this, msPlayer, force);
		PluginManager pm = Bukkit.getPluginManager();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();

		pm.callEvent(event);
		PlayerUtil.setItem(inv, 1, 1, MSConstant.QUIT_SERVER_ITEM);
		PlayerUtil.setItem(inv, 2, 1, MSConstant.QUIT_MENU_ITEM);

		if(isWeaponEquippable(msPlayer))
			equipmentProvider.equip(msPlayer);

		player.setFireTicks(0);
		player.setHealth(((Damageable) player).getMaxHealth());
		player.updateInventory();
		msPlayer.updateMovementSpeed();
	}

	public boolean isWeaponEquippable(MSPlayer msPlayer)
	{
		return isPlayerPlaying(msPlayer) && !isDead(msPlayer) && getPhaseType() != GamePhaseType.FINISHED && getPhaseType() != GamePhaseType.LOBBY;
	}
	
	public Set<MSPlayer> getPlayingPlayers()
	{
		return getPlayers(this::isPlayerPlaying);
	}
	
	public Set<MSPlayer> getPlayingPlayers(Predicate<MSPlayer> predicate)
	{
		return getPlayers(p -> isPlayerPlaying(p) && predicate.test(p));
	}

	public boolean isDead(MSPlayer msPlayer)
	{
		Boolean dead = msPlayer.getCustomData(CUSTOM_DATA_DEAD);

		return dead != null ? dead : false;
	}

	public final boolean isAlive(MSPlayer msPlayer)
	{
		return !isDead(msPlayer);
	}

	public void setDead(MSPlayer msPlayer, boolean value)
	{
		msPlayer.setCustomData(CUSTOM_DATA_DEAD, value);
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		super.redirect(event, msPlayer);

		inventoryListener.redirect(event, msPlayer);
		shoppingListener.redirect(event, msPlayer);
		interactionListener.redirect(event, msPlayer);
		gameShotListener.redirect(event, msPlayer);
		
		if(hasPhase())
			phase.redirect(event, msPlayer);
	}
	
	@SuppressWarnings("unchecked")
	public Game setup()
	{
		super.setup();

		Scheme lobbyScheme = SchemeManager.getScheme(lobbyId);
		
		if(lobbyScheme == null)
			throw new RuntimeException("Lobby scheme of id '" + lobbyId + "' not found");
		
		GameLobby lobby;
		
		try
		{
			lobby = (GameLobby) lobbyScheme;
		}
		catch(Exception e) { throw new RuntimeException("Lobby scheme of id '" + lobbyId + "' is an incorrect instance"); }
		
		Scheme menuScheme = SchemeManager.getScheme(menuId);
		
		if(menuScheme == null)
			throw new RuntimeException("Menu scheme of id '" + menuId + "' not found");
		
		GameMenu menu;
		
		try
		{
			menu = (GameMenu) menuScheme;
		}
		catch(Exception e) { throw new RuntimeException("Menu scheme of id '" + menuId + "' is an incorrect instance"); }
		
		FilledArrayList<GameMap> maps = getMaps();
		
		if(maps.size() <= 0)
			throw new RuntimeException("Map list is empty");
		
		if(equipmentProvider == null)
			initEquipmentManager();
		
		lobbyStructure = PlotManager.registerStructure(lobby);
		menuStructure = PlotManager.registerStructure(menu);
		inventoryListener = new InventoryMSListener(this);
		shoppingListener = new ShoppingMSListener(this);
		interactionListener = new InteractionMSListener(this);
		gameShotListener = new GameShotMSListener(this);
		players = new HashSet<>();
		invited = open ? null : new HashSet<>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		drops = new HashMap<>();
		blockDamageMap = Maps.newHashMap();
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private void initEquipmentManager()
	{
		try
		{
			equipmentProvider = type.newEquipmentManager(this);
		}
		catch(Exception e) { throw new EquipmentManagerInitializationException(e, getClass(), type); }
	}
	
	@SuppressWarnings("unchecked")
	private FilledArrayList<GameMap> initCorrespondingMaps()
	{
		FilledArrayList<GameMap> correspondingMaps = new FilledArrayList<>();
		
		for(String mapId : maps)
		{
			Scheme scheme = SchemeManager.getScheme(mapId);
			GameMap map;
			
			try
			{
				map = (GameMap) scheme;
			}
			catch(Exception e)
			{
				MineStrike.warn("Couldn't add map id '" + mapId + "' to game id '" + id + "' - invalid instance.");
				continue;
			}
			
			correspondingMaps.add(map);
		}
		
		return lazyCorrespondingMaps = correspondingMaps;
	}
	
	public void startMapPoll()
	{
		MapPoll poll = new MapPoll(this);
		
		setPhase(poll);
	}
	
	public FilledArrayList<GameMap> getMaps()
	{
		return lazyCorrespondingMaps != null ? lazyCorrespondingMaps : initCorrespondingMaps();
	}
	
	public void listCommands(CommandSender sender)
	{
		sender.sendMessage(getCommandPath("maps ..."));
	}
	
	public void handleCommand(CommandSender sender, String command, String[] args)
	{
		if(command.equalsIgnoreCase("maps"))
		{
			if(args.length <= 0)
			{
				sender.sendMessage(getCommandPath("maps list"));
				sender.sendMessage(getCommandPath("maps add"));
				sender.sendMessage(getCommandPath("maps remove"));
			}
			else if(args[0].equalsIgnoreCase("list"))
			{
				String maps = "";
				
				for(String map : this.maps)
					maps += map + ", ";
				
				sender.sendMessage(maps);
			}
			else if(args[0].equalsIgnoreCase("add") && args.length > 1)
			{
				String id = args[1];
				
				if(maps.contains(id))
				{
					sender.sendMessage(ChatColor.RED + "Map list already contains map id '" + id + "'.");
					return;
				}
				
				maps.add(id);
				sender.sendMessage(ChatColor.GREEN + "Map '" + id + "' added.");
			}
			else if(args[0].equalsIgnoreCase("remove") && args.length > 1)
			{
				String id = args[1];
				boolean removed = maps.remove(id);
				
				if(removed)
					sender.sendMessage(ChatColor.GREEN + "Map id '" + id + "' removed.");
				else
					sender.sendMessage(ChatColor.RED + "Map id '" + id + "' not found.");
			}
		}
		else
		{
			sender.sendMessage(getCommandPath("maps ..."));
		}
	}
	
	private String getCommandPath(String args)
	{
		return "/ms game select " + id + " " + args;
	}

	@SuppressWarnings("unchecked")
	public boolean isReadyForSetup()
	{
		if(lobbyId == null || menuId == null)
			return false;
		
		try
		{
			if((SchemeManager.getScheme(lobbyId)) == null)
				return false;
			else if(SchemeManager.getScheme(menuId) == null)
				return false;
		}
		catch(ClassCastException e) { return false; }

		return getMaps().size() > 0;
	}
	
	public Game register()
	{
		GameManager.register(this);
		
		return this;
	}
	
	public boolean unregister()
	{
		return GameManager.unregister(this);
	}
	
	public boolean onJoin(MSPlayer msPlayer)
	{
		if(!super.onJoin(msPlayer))
			return false;
		
		Player player = msPlayer.getPlayer();
		
		msPlayer.setPlayerStructure(menuStructure);
		msPlayer.setPlayerState(PlayerState.MENU_GAME);
		players.add(msPlayer);
		msPlayer.setScene(this);
		player.setScoreboard(scoreboard);
		msPlayer.spawn(true);
		SoundManager.play(SOUND_JOIN, player);
		
		return true;
	}
	
	public boolean onQuit(MSPlayer msPlayer, SceneQuitReason reason, boolean teleport)
	{
		if(!super.onQuit(msPlayer, reason, teleport))
			return false;

		if(!quitArena(msPlayer, false))
			return false;
		
		players.remove(msPlayer);
		msPlayer.setScene(null);
		msPlayer.setPlayerStructure(null);
		msPlayer.setPlayerState(PlayerState.LOBBY_SERVER);
		msPlayer.updateMovementSpeed();
		
		if(teleport)
			msPlayer.spawn(true);
		
		return true;
	}
	
	public void broadcast(String message)
	{
		for(MSPlayer msPlayer : players)
			msPlayer.sendMessage(message);
	}
	
/*	public void setupPlot()
	{
		Validate.notNull(scheme, "The structure cannot be null while setting up the plot!");
		
		plot = PlotManager.getFreePlot(scheme);
		
		plot.setGame(this);
		plot.build();
	}
	
	public void giveUpPlot()
	{
		plot.setGame(null);
		this.plot = null;
	}*/
	
	public Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> condition)
	{
		return getPlayers(condition).stream().map(MSPlayer::getPlayer).collect(Collectors.toSet());
	}
	
	public Set<Player> getBukkitPlayers()
	{
		return getPlayers().stream().map(MSPlayer::getPlayer).collect(Collectors.toSet());
	}
	
	public Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> condition)
	{
		return players.stream().filter(condition).collect(Collectors.toSet());
	}
	
	public HashSet<MSPlayer> getPlayers()
	{
		return players;
	}
	
	public void updateTabHeadersAndFooters()
	{
		for(MSPlayer msPlayer : getPlayers())
			msPlayer.updateTabHeaderAndFooter();
	}
	
	public void updateTabHeadersAndFooters(Predicate<MSPlayer> predicate)
	{
		getPlayers(predicate).forEach(cz.minestrike.me.limeth.minestrike.MSPlayer::updateTabHeaderAndFooter);
	}
	
	public boolean canJoin(String playerName)
	{
		return open || isInvited(playerName) || playerName.equals(owner.getName());
	}
	
	public boolean isInvited(String playerName)
	{
		for(String invited : this.invited)
			if(invited.equalsIgnoreCase(playerName))
				return true;
		
		return false;
	}
	
	public String getId()
	{
		return id;
	}

	public boolean isOpen()
	{
		return open;
	}
	
	public void setOpen(boolean open)
	{
		this.open = open;
	}

	public HashSet<String> getInvited()
	{
		return invited;
	}
	
	public boolean hasOwner()
	{
		return owner != null;
	}

	public MSPlayer getOwner()
	{
		return owner;
	}

	public FilledArrayList<String> getMapIds()
	{
		return maps;
	}
	
	public Structure<? extends GameMap> setMap(GameMap map)
	{
		Validate.notNull(map, "The map cannot be null!");
		
		if(mapStructure != null)
		{
			GameMap currentMap = mapStructure.getScheme();
			
			if(currentMap.equals(map))
				return mapStructure;
			
			PlotManager.unregisterStructure(mapStructure);
		}
		
		return mapStructure = PlotManager.registerStructure(map);
	}
	
	public boolean hasPhase()
	{
		return phase != null;
	}

	public GamePhase<? extends Game> getPhase()
	{
		return phase;
	}

	public void setPhase(GamePhase<? extends Game> phase)
	{
		this.phase = phase;

		if(phase != null)
			phase.start();
	}
	
	public GamePhaseType getPhaseType()
	{
		return phase != null ? phase.getType() : null;
	}

	public String getLobbyId()
	{
		return lobbyId;
	}

	public void setLobbyId(String lobbyId)
	{
		Validate.notNull(lobbyId, "The lobby id must not be null!");
		
		this.lobbyId = lobbyId;
	}

	public GameType getGameType()
	{
		return type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		Validate.notNull(name, "The name must not be null!");
		Validate.notEmpty(name, "The name must not be empty!");
		
		this.name = name;
	}
	
	public Structure<? extends GameLobby> getLobbyStructure()
	{
		return lobbyStructure;
	}

	public void setLobbyStructure(Structure<? extends GameLobby> lobbyStructure)
	{
		this.lobbyStructure = lobbyStructure;
	}

	public Structure<? extends GameMenu> getMenuStructure()
	{
		return menuStructure;
	}

	public void setMenuStructure(Structure<? extends GameMenu> menuStructure)
	{
		this.menuStructure = menuStructure;
	}

	public Structure<? extends GameMap> getMapStructure()
	{
		return mapStructure;
	}

	public void setMapStructure(Structure<GameMap> mapStructure)
	{
		this.mapStructure = mapStructure;
	}

	public String getMenuId()
	{
		return menuId;
	}

	public void setMenuId(String menuId)
	{
		Validate.notNull(menuId, "The menu id cannot be null!");
		
		this.menuId = menuId;
	}
	
	public EquipmentProvider getEquipmentProvider()
	{
		return equipmentProvider;
	}
	
	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}
	
	public Item drop(Location loc, Vector velocity, Equipment equipment, MSPlayer msPlayer)
	{
		World world = loc.getWorld();
		WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		ItemStack itemStack = equipment.newItemStack(msPlayer);
		net.minecraft.server.v1_7_R4.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
		EntityItem nmsItem = new EntityItem(nmsWorld, loc.getX(), loc.getY(), loc.getZ(), nmsItemStack);
		
		nmsItem.motX = velocity.getX();
		nmsItem.motY = velocity.getY();
		nmsItem.motZ = velocity.getZ();
		nmsItem.pickupDelay = 10;
		
		nmsWorld.addEntity(nmsItem, SpawnReason.CUSTOM);
		
		Item item = (Item) nmsItem.getBukkitEntity();
		
		drops.put(item, equipment);
		
		return item;
	}
	
	public Item drop(Equipment equipment, MSPlayer msPlayer, boolean viewAxis)
	{
		Player player = msPlayer.getPlayer();
		Location location = player.getEyeLocation();
		Vector velocity;
		
		if(viewAxis)
			velocity = location.getDirection().multiply(0.25);
		else
			velocity = new Vector((Math.random() - 0.5) / 2, Math.random() / 4, (Math.random() - 0.5) / 2);
		
		return drop(location, velocity, equipment, msPlayer);
	}

	public Map<Item, Equipment> getDrops()
	{
		return drops;
	}

	public Equipment getDrop(Item item)
	{
		return drops.get(item);
	}
	
	public Equipment removeDrop(Item item)
	{
		item.remove();
		
		return drops.remove(item);
	}
	
	public void clearDrops()
	{
		for(Item drop : drops.keySet())
			drop.remove();
		
		drops.clear();
	}

	public Map<Block, Double> getBlockDamageMap()
	{
		return blockDamageMap;
	}

	public void repairDamagedBlocks()
	{
		for(Block block : blockDamageMap.keySet())
		{
			Material type = block.getType();

			if(type != Material.AIR)
				continue;

			Structure<? extends GameMap> structure = getMapStructure();
			GameMap scheme = structure.getScheme();
			Point base = structure.getBase();
			Point relativeSource = Point.valueOf(block).subtract(base);
			World world = block.getWorld();

			scheme.build(relativeSource, base, world);
		}

		blockDamageMap.clear();
	}

	@Override
	public String toString()
	{
		return "Game [type=" + type + ", id=" + id + ", name=" + name + "]";
	}
}
