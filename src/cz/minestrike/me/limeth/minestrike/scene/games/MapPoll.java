package cz.minestrike.me.limeth.minestrike.scene.games;

import cz.minestrike.me.limeth.minestrike.*;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.events.EquipmentPickupEvent;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.renderers.MapPollRenderer;
import cz.minestrike.me.limeth.minestrike.util.RendererUtil;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import cz.projectsurvive.me.limeth.psmaps.MapAllocator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Map.Entry;

public class MapPoll extends GamePhase<Game> implements Runnable
{
	public static final String OBJECTIVE_ID = "mapPoll";
	public static final int SELECTED_MAX = 5, VOTING_SECONDS = 50 /* incl. changing */, CHANGING_SECONDS = 10; // + 10
	private final PollListener listener;
	private final FilledHashMap<GameMap, Integer> votedMaps;
	private final FilledHashMap<String, GameMap> votedPlayers;
	private int secondsLeft;
	private Integer taskId;
	private FilledHashMap<Short, GameMap> selectedMaps;
	private Objective objective;
	private GameMap votedMap;
	
	public MapPoll(Game game)
	{
		super(game, GamePhaseType.FINISHED);
		
		listener = new PollListener(game);
		votedMaps = new FilledHashMap<GameMap, Integer>();
		votedPlayers = new FilledHashMap<String, GameMap>();
		secondsLeft = VOTING_SECONDS;
	}

	@Override
	public GamePhase<Game> start()
	{
		selectMaps();
		setRenderers();
		initObjective();
		equipPlayers(true);
		startCountdown();
		return this;
	}
	
	public void endVoting()
	{
		Game game = getGame();
		Integer mostVotes = null;
		
		for(GameMap map : selectedMaps.values())
		{
			int votes = this.votedMaps.get(map);
			
			if(mostVotes == null || votes > mostVotes)
			{
				votedMap = map;
				mostVotes = votes;
			}
		}

		FilledArrayList<GameMap> availableMaps = game.getMaps();
		String votedMapName = votedMap.getName();

		//Move the map to the end of the queue
		availableMaps.remove(votedMap);
		availableMaps.add(votedMap);
		
		game.broadcast(Translation.GAME_POLL_CHANGING.getMessage(votedMapName));
		
		for(MSPlayer msPlayer : game.getPlayers())
			game.equip(msPlayer, false);
	}
	
	public void end()
	{
		cancelTask();
		objective.unregister();
		changeMap();
	}
	
	private void changeMap()
	{
		Game game = getGame();
		
		Structure<? extends GameMap> structure = game.setMap(votedMap);
		
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
		Game game = getGame();
		FilledArrayList<GameMap> availableMaps = game.getMaps();
		int amount = availableMaps.size();
		
		if(amount > SELECTED_MAX)
			amount = SELECTED_MAX;
		
		ArrayList<Short> availableIds = MapAllocator.allocate(amount);
		selectedMaps = new FilledHashMap<Short, GameMap>();
		int i = 0;
		
		for(short mapId : availableIds)
		{
			GameMap map = availableMaps.get(i);
			
			selectedMaps.put(mapId, map);
			votedMaps.put(map, 0);
			i++;
		}
	}
	
	private void setRenderers()
	{
		World world = MSConfig.getWorld();
		
		for(Entry<Short, GameMap> entry : selectedMaps.entrySet())
		{
			Short viewId = entry.getKey();
			GameMap map = entry.getValue();
			
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
		Game game = getGame();
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
		for(GameMap map : selectedMaps.values())
		{
			String entry = getPollEntry(map);
			Score score = objective.getScore(entry);
			int votes = this.votedMaps.get(map);
			
			score.setScore(votes);
		}
	}
	
	private void equipPlayers(boolean equipScene)
	{
		Game game = getGame();
		
		for(MSPlayer msPlayer : game.getPlayingPlayers())
			equipPlayer(msPlayer, equipScene);
	}
	
	private void equipPlayer(MSPlayer msPlayer, boolean equipScene)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int i = 0;

		msPlayer.clearHotbar();
		msPlayer.getHotbarContainer().clear();

		if(equipScene)
		{
			player.closeInventory();
			getGame().equip(msPlayer, false);
		}

		for(Entry<Short, GameMap> entry : selectedMaps.entrySet())
		{
			short mapId = entry.getKey();
			GameMap map = entry.getValue();
			String name = map.getName();
			ItemStack is = new ItemStack(Material.MAP, 1, mapId);
			ItemMeta im = is.getItemMeta();
			
			im.setDisplayName(ChatColor.YELLOW + name);
			is.setItemMeta(im);
			inv.setItem(i++, is);
		}

		if(equipScene)
			msPlayer.updateInventory();
	}
	
	private static String getPollEntry(GameMap map)
	{
		String name = ChatColor.YELLOW + map.getName();
		
		if(name.length() > 16)
			name = name.substring(0, 16);
		
		return name;
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		listener.redirect(event, msPlayer);
	}
	
	public FilledHashMap<GameMap, Integer> getVotes()
	{
		return votedMaps;
	}

	public boolean isVoteable()
	{
		return votedMap == null && secondsLeft > 0;
	}
	
	public GameMap getVotedMap()
	{
		return votedMap;
	}

	private class PollListener extends MSSceneListener<Game>
	{
		public PollListener(Game game)
		{
			super(game);
		}
		
		@EventHandler
		public void onGameEquip(GameEquipEvent event, MSPlayer msPlayer)
		{
			Game game = getScene();
			
			if(isVoteable() && game.isPlayerPlaying().test(msPlayer))
				equipPlayer(msPlayer, false);
		}
		
		@EventHandler
		public void onPlayerDropItem(PlayerDropItemEvent event, MSPlayer msPlayer)
		{
			Game game = getScene();
			
			if(game.isPlayerPlaying().test(msPlayer))
				event.setCancelled(true);
		}
		
		@EventHandler
		public void onEquipmentPickup(EquipmentPickupEvent event, MSPlayer msPlayer)
		{
			event.setCancelled(true);
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
			GameMap map = selectedMaps.get(durability);
			
			if(map == null)
				return;
			
			String playerName = player.getName();
			GameMap previousMap = votedPlayers.get(playerName);
			
			if(previousMap != null)
			{
				String previousMapName = previousMap.getName();
				
				player.sendMessage(Translation.GAME_POLL_VOTE_REPEATED.getMessage(previousMapName));
				return;
			}
			
			String mapName = map.getName();
			int points = votedMaps.get(map);
			int strength = player.hasPermission(MSConstant.PERMISSION_VOTE_STRENGTH) ? 2 : 1;
			
			msPlayer.clearHotbar();
			votedPlayers.put(playerName, map);
			votedMaps.put(map, points + strength);
			updateObjectiveVotes();
			player.sendMessage(Translation.GAME_POLL_VOTE_SUCCESS.getMessage(mapName));
		}
	}
}
