package cz.minestrike.me.limeth.minestrike.scene.games;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.renderers.MapPollRenderer;
import cz.minestrike.me.limeth.minestrike.util.MapAllocator;
import cz.minestrike.me.limeth.minestrike.util.RendererUtil;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class MapPoll<Lo extends GameLobby, Me extends GameMenu, Ma extends GameMap, EM extends EquipmentProvider> extends GamePhase<Lo, Me, Ma, EM> implements Runnable
{
	public static final String OBJECTIVE_ID = "mapPoll";
	public static final int SELECTED_MAX = 5, VOTING_SECONDS = 50 /* incl. changing */, CHANGING_SECONDS = 10; // + 10
	private final PollListener listener;
	private final FilledHashMap<String, Ma> votes;
	private int secondsLeft;
	private Integer taskId;
	private FilledHashMap<Short, Ma> selectedMaps;
	private Objective objective;
	private Ma votedMap;
	
	public MapPoll(Game<Lo, Me, Ma, EM> game)
	{
		super(game, GamePhaseType.FINISHED);
		
		listener = new PollListener(game);
		votes = new FilledHashMap<String, Ma>();
		secondsLeft = VOTING_SECONDS;
	}

	@Override
	public GamePhase<Lo, Me, Ma, EM> start()
	{
		selectMaps();
		setRenderers();
		initObjective();
		equipPlayers();
		startCountdown();
		return this;
	}
	
	public void endVoting()
	{
		Game<Lo, Me, Ma, EM> game = getGame();
		Integer mostVotes = null;
		
		for(Ma map : selectedMaps.values())
		{
			int votes = 0;
			
			for(Ma currentMap : this.votes.values())
				if(map == currentMap)
					votes++;
			
			if(mostVotes == null || votes > mostVotes)
			{
				votedMap = map;
				mostVotes = votes;
			}
		}
		
		String votedMapName = votedMap.getName();
		
		game.broadcast(Translation.GAME_POLL_CHANGING.getMessage(votedMapName));
	}
	
	public void end()
	{
		cancelTask();
		objective.unregister();
		changeMap();
	}
	
	private void changeMap()
	{
		Game<Lo, Me, Ma, EM> game = getGame();
		
		game.setMap(votedMap);
		
		Structure<Ma> structure = game.getMapStructure();
		
		for(MSPlayer msPlayer : game.getPlayers(p -> { return p.getPlayerState() == PlayerState.JOINED_GAME; }))
			msPlayer.setPlayerStructure(structure);
		
		game.start();
	}
	
	//Executed every second
	@Override
	public void run()
	{
		secondsLeft--;
		
		if(secondsLeft > 0)
		{
			if(secondsLeft == CHANGING_SECONDS)
				endVoting();
			
			updateObjectiveHeader();
		}
		else
			end();
	}
	
	private void cancelTask()
	{
		if(!hasTask())
			throw new RuntimeException("No task is running.");
		
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	private boolean hasTask()
	{
		return taskId != null;
	}
	
	@Override
	public void cancel()
	{
		if(isVoteable())
			endVoting();
		
		end();
	}
	
	private void selectMaps()
	{
		Game<Lo, Me, Ma, EM> game = getGame();
		FilledArrayList<Ma> availableMaps = game.getMaps();
		int amount = availableMaps.size();
		
		if(amount > SELECTED_MAX)
			amount = SELECTED_MAX;
		
		FilledArrayList<Short> availableIds = MapAllocator.allocate(amount);
		selectedMaps = new FilledHashMap<Short, Ma>();
		int i = 0;
		
		for(short mapId : availableIds)
		{
			Ma map = availableMaps.get(i);
			
			selectedMaps.put(mapId, map);
			i++;
		}
	}
	
	private void setRenderers()
	{
		World world = MSConfig.getWorld();
		
		for(Entry<Short, Ma> entry : selectedMaps.entrySet())
		{
			Short viewId = entry.getKey();
			Ma map = entry.getValue();
			
			try
			{
				MapPollRenderer renderer = MapPollRenderer.forGameMap(map);
				RendererUtil.setRenderer(world, viewId, renderer);
			}
			catch(Exception e)
			{
				MineStrike.warn("Error when setting the renderer for map " + map + " (MapView #" + viewId + ")");
				e.printStackTrace();
			}
		}
	}
	
	private void initObjective()
	{
		Game<?, ?, ?, ?> game = getGame();
		Scoreboard sb = game.getScoreboard();
		objective = sb.getObjective(OBJECTIVE_ID);
		
		if(objective == null)
			objective = sb.registerNewObjective(OBJECTIVE_ID, "dummy");
		
		updateObjective();
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	private void startCountdown()
	{
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, 20L, 20L);
	}
	
	private void updateObjective()
	{
		updateObjectiveHeader();
		updateObjectiveVotes();
	}
	
	private void updateObjectiveHeader()
	{
		int secondsLeft;
		Translation translation;
		
		if(this.secondsLeft > CHANGING_SECONDS)
		{
			secondsLeft = this.secondsLeft - CHANGING_SECONDS;
			translation = Translation.GAME_POLL_HEADER_VOTING;
		}
		else
		{
			secondsLeft = this.secondsLeft;
			translation = Translation.GAME_POLL_HEADER_CHANGING;
		}
		
		String time = Integer.valueOf(secondsLeft).toString();
		String header = translation.getMessage(time);
		
		if(header.length() > 32)
			header = header.substring(0, 32);
		
		objective.setDisplayName(header);
	}
	
	private void updateObjectiveVotes()
	{
		for(Ma map : selectedMaps.values())
		{
			OfflinePlayer entry = getPollEntry(map);
			Score score = objective.getScore(entry);
			int votes = 0;
			
			for(Ma votedMap : this.votes.values())
				if(map.equals(votedMap))
					votes++;
			
			score.setScore(votes);
		}
	}
	
	private void equipPlayers()
	{
		Game<Lo, Me, Ma, EM> game = getGame();
		
		for(MSPlayer msPlayer : game.getPlayingPlayers())
			equipPlayer(msPlayer);
	}
	
	private void equipPlayer(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int i = 0;
		
		msPlayer.clearHotbar();
		
		for(Entry<Short, Ma> entry : selectedMaps.entrySet())
		{
			short mapId = entry.getKey();
			Ma map = entry.getValue();
			String name = map.getName();
			ItemStack is = new ItemStack(Material.MAP, 1, mapId);
			ItemMeta im = is.getItemMeta();
			
			im.setDisplayName(ChatColor.YELLOW + name);
			is.setItemMeta(im);
			inv.setItem(i++, is);
		}
	}
	
	private static OfflinePlayer getPollEntry(GameMap map)
	{
		String name = ChatColor.YELLOW + map.getName();
		
		if(name.length() > 16)
			name = name.substring(0, 16);
		
		return Bukkit.getOfflinePlayer(name);
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		listener.redirect(event, msPlayer);
	}
	
	public FilledHashMap<String, Ma> getVotes()
	{
		return votes;
	}

	public boolean isVoteable()
	{
		return votedMap == null;
	}
	
	public Ma getVotedMap()
	{
		return votedMap;
	}

	private class PollListener extends MSSceneListener<Game<Lo, Me, Ma, EM>>
	{
		public PollListener(Game<Lo, Me, Ma, EM> game)
		{
			super(game);
		}
		
		@EventHandler
		public void onGameEquip(GameEquipEvent event, MSPlayer msPlayer)
		{
			Game<Lo, Me, Ma, EM> game = getScene();
			
			if(game.isPlayerPlaying().test(msPlayer))
				equipPlayer(msPlayer);
		}
		
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event, MSPlayer msPlayer)
		{
			Action action = event.getAction();
			
			if(action == Action.PHYSICAL)
				return;
			
			if(!isVoteable())
				return;
			
			Player player = msPlayer.getPlayer();
			ItemStack item = player.getItemInHand();
			
			if(item == null || item.getType() != Material.MAP)
				return;
			
			short durability = item.getDurability();
			Ma map = selectedMaps.get(durability);
			
			if(map == null)
				return;
			
			String playerName = player.getName();
			Ma previousMap = votes.get(playerName);
			
			if(previousMap != null)
			{
				String previousMapName = previousMap.getName();
				
				player.sendMessage(Translation.GAME_POLL_VOTE_REPEATED.getMessage(previousMapName));
				return;
			}
			
			String mapName = map.getName();
			
			votes.put(playerName, map);
			updateObjectiveVotes();
			player.sendMessage(Translation.GAME_POLL_VOTE_SUCCESS.getMessage(mapName));
		}
	}
}
