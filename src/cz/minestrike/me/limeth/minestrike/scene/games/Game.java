package cz.minestrike.me.limeth.minestrike.scene.games;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.server.v1_7_R4.EntityItem;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManagerInitializationException;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import cz.minestrike.me.limeth.minestrike.events.GameJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class Game extends Scene
{
	private static final String SOUND_JOIN = "projectsurvive:counterstrike.ui.valve_logo_music";
	@Expose private final GameType type;
	@Expose private final String id;
	@Expose private String name;
	private MSPlayer owner;
	private boolean open;
	private HashSet<MSPlayer> players;
	private HashSet<String> invited;
	@Expose private String lobbyId;
	@Expose private String menuId;
	@Expose private FilledArrayList<String> maps;
	private FilledArrayList<GameMap> lazyCorrespondingMaps;
	private Structure<? extends GameLobby> lobbyStructure;
	private Structure<? extends GameMenu> menuStructure;
	private Structure<? extends GameMap> mapStructure;
	private GamePhase<? extends Game> phase;
	private MSInventoryListener inventoryListener;
	private MSShoppingListener shoppingListener;
	private MSInteractionListener interactionListener;
	private EquipmentProvider equipmentProvider;
	private Scoreboard scoreboard;
	private HashMap<Item, Equipment> drops;
	
	public Game(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps)
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
	
	public abstract Predicate<MSPlayer> isPlayerPlaying();
	public abstract int getXPForKill(MSPlayer msVictim, MSPlayer msKiller);
	
	public void firstStart()
	{
		FilledArrayList<GameMap> maps = getMaps();
		
		setMap(maps.get(MSConstant.RANDOM.nextInt(maps.size())));
	}
	
	public void start()
	{
		if(open && mapStructure == null)
			firstStart();
	}
	
	public void joinMenu(MSPlayer msPlayer)
	{
		msPlayer.setPlayerState(PlayerState.MENU_GAME);
		msPlayer.setPlayerStructure(menuStructure);
		msPlayer.spawn(true);
	}
	
	public void joinLobby(MSPlayer msPlayer)
	{
		msPlayer.setPlayerState(PlayerState.LOBBY_GAME);
		msPlayer.setPlayerStructure(lobbyStructure);
		msPlayer.spawn(true);
	}
	
	public boolean quitArena(MSPlayer msPlayer)
	{
		ArenaQuitEvent event = new ArenaQuitEvent(this, msPlayer);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(event);
		
		if(event.isCancelled())
			return false;
		
		if(owner != null && owner.equals(msPlayer))
			joinLobby(msPlayer);
		else
			joinMenu(msPlayer);
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void equip(MSPlayer msPlayer, boolean force)
	{
		GameEquipEvent event = new GameEquipEvent(this, msPlayer, force);
		PluginManager pm = Bukkit.getPluginManager();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		FilledArrayList<EquipmentCategory> categories = equipmentProvider.getEquipmentCategories();
		
		for(int rel = 0; rel < PlayerUtil.INVENTORY_WIDTH * 3; rel++)
		{
			int abs = rel + PlayerUtil.INVENTORY_WIDTH;
			
			inv.setItem(abs, MSConstant.ITEM_BACKGROUND);
		}
		
		PlayerUtil.setItem(inv, 1, 1, MSConstant.QUIT_SERVER_ITEM);
		PlayerUtil.setItem(inv, 2, 1, MSConstant.QUIT_MENU_ITEM);
		
		for(int i = 0; i < categories.size(); i++)
		{
			EquipmentCategory category = categories.get(i);
			ItemStack icon = category.getIcon();
			int x = 6 + i % 2;
			int y = i / 2;
			
			PlayerUtil.setItem(inv, x, y, icon);
		}
		
		pm.callEvent(event);
		equipmentProvider.equip(msPlayer);
		player.setFireTicks(0);
		player.setHealth(((Damageable) player).getMaxHealth());
		player.updateInventory();
	}
	
	public Set<MSPlayer> getPlayingPlayers()
	{
		return getPlayers(isPlayerPlaying());
	}
	
	public Set<MSPlayer> getPlayingPlayers(Predicate<MSPlayer> predicate)
	{
		return getPlayers(p -> { return isPlayerPlaying().test(p) && predicate.test(p); });
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		inventoryListener.redirect(event, msPlayer);
		shoppingListener.redirect(event, msPlayer);
		interactionListener.redirect(event, msPlayer);
		
		if(hasPhase())
			phase.redirect(event, msPlayer);
	}
	
	@SuppressWarnings("unchecked")
	public Game setup()
	{
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
		inventoryListener = new MSInventoryListener(this);
		shoppingListener = new MSShoppingListener(this);
		interactionListener = new MSInteractionListener(this);
		players = new HashSet<MSPlayer>();
		invited = open ? null : new HashSet<String>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		drops = new HashMap<Item, Equipment>();
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private void initEquipmentManager()
	{
		try
		{
			equipmentProvider = (EquipmentProvider) type.newEquipmentManager(this);
		}
		catch(Exception e) { throw new EquipmentManagerInitializationException(e, getClass(), type); }
	}
	
	@SuppressWarnings("unchecked")
	private FilledArrayList<GameMap> initCorrespondingMaps()
	{
		FilledArrayList<GameMap> correspondingMaps = new FilledArrayList<GameMap>();
		
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
			sender.sendMessage(ChatColor.RED + "Unknown game command.");
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
			if(((GameLobby) SchemeManager.getScheme(lobbyId)) == null)
				return false;
			else if(((GameMenu) SchemeManager.getScheme(menuId)) == null)
				return false;
		}
		catch(ClassCastException e) { return false; }
		
		if(getMaps().size() <= 0)
			return false;
		
		return true;
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
		Validate.notNull(msPlayer, "The player cannot be null!");
		Validate.isTrue(!hasJoined(msPlayer), "Player '" + msPlayer.getName() + "' has already joined this game ('" + id + "').");
		
		GameJoinEvent event = new GameJoinEvent(this, msPlayer);
		
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
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
		Validate.isTrue(hasJoined(msPlayer), "Player '" + msPlayer + "' has not joined this game ('" + id + "').");
		
		GameQuitEvent event = new GameQuitEvent(this, msPlayer, reason);
		
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
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
	
	public boolean hasJoined(MSPlayer msPlayer)
	{
		return this.equals(msPlayer.getScene()) && players.contains(msPlayer);
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
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers(condition))
			players.add(player.getPlayer());
		
		return players;
	}
	
	public Set<Player> getBukkitPlayers()
	{
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers())
			players.add(player.getPlayer());
		
		return players;
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
		for(MSPlayer msPlayer : getPlayers(predicate))
			msPlayer.updateTabHeaderAndFooter();
	}
	
	public void playSound(String path, Location loc, float volume, float pitch, Predicate<MSPlayer> predicate)
	{
		if(loc != null)
		{
			PacketPlayOutNamedSoundEffect packet = SoundManager.buildPacket(path, loc, volume, pitch);
			
			for(MSPlayer msPlayer : (predicate != null ? getPlayers(predicate) : getPlayers()))
			{
				Player player = msPlayer.getPlayer();
				
				SoundManager.play(packet, player);
			}
		}
		else
		{
			for(MSPlayer msPlayer : (predicate != null ? getPlayers(predicate) : getPlayers()))
			{
				Player player = msPlayer.getPlayer();
				loc = player.getLocation();
				PacketPlayOutNamedSoundEffect packet = SoundManager.buildPacket(path, loc, volume, pitch);
				
				SoundManager.play(packet, player);
			}
		}
	}
	
	public void playSound(String path, Location loc, float volume, Predicate<MSPlayer> predicate)
	{
		playSound(path, loc, volume, 1, predicate);
	}
	
	public void playSound(String path, Location loc, float volume)
	{
		playSound(path, loc, volume, 1, null);
	}
	
	public void playSound(String path, Location loc, Predicate<MSPlayer> predicate)
	{
		playSound(path, loc, 1, 1, predicate);
	}
	
	public void playSound(String path, Location loc)
	{
		playSound(path, loc, 1, 1, null);
	}
	
	public void playSound(String path, float volume, Predicate<MSPlayer> predicate)
	{
		playSound(path, null, volume, 1, predicate);
	}
	
	public void playSound(String path, float volume)
	{
		playSound(path, null, volume, 1, null);
	}
	
	public void playSound(String path, Predicate<MSPlayer> predicate)
	{
		playSound(path, null, 1, 1, predicate);
	}
	
	public void playSound(String path)
	{
		playSound(path, null, 1, 1, null);
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
		if(phase != null)
			phase.start();
		
		this.phase = phase;
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
	
	@Override
	public String toString()
	{
		return "Game [type=" + type + ", id=" + id + ", name=" + name + "]";
	}
}
