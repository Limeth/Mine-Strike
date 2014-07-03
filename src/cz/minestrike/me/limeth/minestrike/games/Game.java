package cz.minestrike.me.limeth.minestrike.games;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Scoreboard;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManagerInitializationException;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import cz.minestrike.me.limeth.minestrike.events.GameJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSInventoryListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class Game<Lo extends GameLobby, Me extends GameMenu, Ma extends GameMap, EM extends EquipmentManager> implements MSListenerRedirector
{
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
	private FilledArrayList<Ma> lazyCorrespondingMaps;
	private Structure<Lo> lobbyStructure;
	private Structure<Me> menuStructure;
	private Structure<Ma> mapStructure;
	private GamePhase<Lo, Me, Ma, EM> phase;
	private MSInventoryListener<Game<Lo, Me, Ma, EM>> inventoryListener;
	private EM equipmentManager;
	private Scoreboard scoreboard;
	
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
	
	public abstract boolean joinArena(MSPlayer msPlayer, Team team);
	public abstract void start();
	public abstract Predicate<MSPlayer> isPlayerPlaying();
	
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
	
	public void equip(MSPlayer msPlayer, boolean force)
	{
		GameEquipEvent event = new GameEquipEvent(this, msPlayer, force);
		PluginManager pm = Bukkit.getPluginManager();
		
		Player player = msPlayer.getPlayer();
		
		pm.callEvent(event);
		equipmentManager.equip(msPlayer);
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
		
		if(hasPhase())
			phase.redirect(event, msPlayer);
	}
	
	public abstract Location spawn(MSPlayer msPlayer, boolean teleport);
	
	@SuppressWarnings("unchecked")
	public Game<Lo, Me, Ma, EM> setup()
	{
		Scheme lobbyScheme = SchemeManager.getScheme(lobbyId);
		
		if(lobbyScheme == null)
			throw new RuntimeException("Lobby scheme of id '" + lobbyId + "' not found");
		
		Lo lobby;
		
		try
		{
			lobby = (Lo) lobbyScheme;
		}
		catch(Exception e) { throw new RuntimeException("Lobby scheme of id '" + lobbyId + "' is an incorrect instance"); }
		
		Scheme menuScheme = SchemeManager.getScheme(menuId);
		
		if(menuScheme == null)
			throw new RuntimeException("Menu scheme of id '" + menuId + "' not found");
		
		Me menu;
		
		try
		{
			menu = (Me) menuScheme;
		}
		catch(Exception e) { throw new RuntimeException("Menu scheme of id '" + menuId + "' is an incorrect instance"); }
		
		FilledArrayList<Ma> maps = getMaps();
		
		if(maps.size() <= 0)
			throw new RuntimeException("Map list is empty");
		
		if(equipmentManager == null)
			initEquipmentManager();
		
		lobbyStructure = PlotManager.registerStructure(lobby);
		menuStructure = PlotManager.registerStructure(menu);
		
		if(open)
			setMap(maps.get(maps.size() - 1)); //TODO move to start
		
		inventoryListener = new MSInventoryListener<Game<Lo, Me, Ma, EM>>(this);
		players = new HashSet<MSPlayer>();
		invited = open ? null : new HashSet<String>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private void initEquipmentManager()
	{
		try
		{
			equipmentManager = (EM) type.newEquipmentManager(this);
		}
		catch(Exception e) { throw new EquipmentManagerInitializationException(e, getClass(), type); }
	}
	
	@SuppressWarnings("unchecked")
	private FilledArrayList<Ma> initCorrespondingMaps()
	{
		FilledArrayList<Ma> correspondingMaps = new FilledArrayList<Ma>();
		
		for(String mapId : maps)
		{
			Scheme scheme = SchemeManager.getScheme(mapId);
			Ma map;
			
			try
			{
				map = (Ma) scheme;
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
		MapPoll<Lo, Me, Ma, EM> poll = new MapPoll<Lo, Me, Ma, EM>(this);
		
		setPhase(poll);
	}
	
	public FilledArrayList<Ma> getMaps()
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
	public boolean isSetUp()
	{
		if(lobbyId == null || menuId == null)
			return false;
		
		try
		{
			if(((Lo) SchemeManager.getScheme(lobbyId)) == null)
				return false;
			else if(((Me) SchemeManager.getScheme(menuId)) == null)
				return false;
		}
		catch(ClassCastException e) { return false; }
		
		if(getMaps().size() <= 0)
			return false;
		
		return true;
	}
	
	public Game<Lo, Me, Ma, EM> register()
	{
		GameManager.register(this);
		
		return this;
	}
	
	public boolean unregister()
	{
		return GameManager.unregister(this);
	}
	
	public boolean join(MSPlayer msPlayer)
	{
		Validate.notNull(msPlayer, "The player cannot be null!");
		Validate.isTrue(!hasJoined(msPlayer), "Player '" + msPlayer.getName() + "' has already joined this game ('" + id + "').");
		Validate.isTrue(!msPlayer.hasGame(), "Player '" + msPlayer.getName() + "' has already joined another game ('" + msPlayer.getGame() + "')!");
		
		GameJoinEvent event = new GameJoinEvent(this, msPlayer);
		
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
			return false;
		
		Player player = msPlayer.getPlayer();
		
		msPlayer.setPlayerStructure(menuStructure);
		msPlayer.setPlayerState(PlayerState.MENU_GAME);
		players.add(msPlayer);
		msPlayer.setGame(this);
		player.setScoreboard(scoreboard);
		msPlayer.spawn(true);
		
		return true;
	}
	
	public boolean quit(MSPlayer msPlayer, GameQuitReason reason, boolean teleport)
	{
		Validate.isTrue(hasJoined(msPlayer), "Player '" + msPlayer + "' has not joined this game ('" + id + "').");
		
		GameQuitEvent event = new GameQuitEvent(this, msPlayer, reason);
		
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled())
			return false;
		
		players.remove(msPlayer);
		msPlayer.setGame(null);
		msPlayer.setPlayerStructure(null);
		msPlayer.setPlayerState(PlayerState.LOBBY_SERVER);
		
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
		return this.equals(msPlayer.getGame()) && players.contains(msPlayer);
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
	
	public Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> condition)
	{
		return players.stream().filter(condition).collect(Collectors.toSet());
	}
	
	public HashSet<MSPlayer> getPlayers()
	{
		return players;
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
	
	public void setMap(Ma map)
	{
		Validate.notNull(map, "The map cannot be null!");
		
		if(mapStructure != null)
		{
			GameMap currentMap = mapStructure.getScheme();
			
			if(currentMap.equals(map))
				return;
			
			PlotManager.unregisterStructure(mapStructure);
		}
		
		mapStructure = PlotManager.registerStructure(map);
	}
	
	public boolean hasPhase()
	{
		return phase != null;
	}

	public GamePhase<Lo, Me, Ma, EM> getPhase()
	{
		return phase;
	}

	public void setPhase(GamePhase<Lo, Me, Ma, EM> phase)
	{
		if(phase != null)
			phase.start();
		
		this.phase = phase;
	}
	
	public GamePhaseType getPhaseType()
	{
		return phase.getType();
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
	
	public Structure<Lo> getLobbyStructure()
	{
		return lobbyStructure;
	}

	public void setLobbyStructure(Structure<Lo> lobbyStructure)
	{
		this.lobbyStructure = lobbyStructure;
	}

	public Structure<Me> getMenuStructure()
	{
		return menuStructure;
	}

	public void setMenuStructure(Structure<Me> menuStructure)
	{
		this.menuStructure = menuStructure;
	}

	public Structure<Ma> getMapStructure()
	{
		return mapStructure;
	}

	public void setMapStructure(Structure<Ma> mapStructure)
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
	
	public EM getEquipmentManager()
	{
		return equipmentManager;
	}

	@Override
	public String toString()
	{
		return "Game [type=" + type + ", id=" + id + ", name=" + name + "]";
	}

	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}
}
