package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitScheduler;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhase;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGameMenu;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame.RoundEndReason;

public class Round extends GamePhase<GameLobby, TeamGameMenu, DefuseGameMap, DefuseEquipmentProvider>
{
	public static final long BOMB_TIME = 60 * 20, SPAWN_TIME = 10 * 20, ROUND_TIME = 20 * 60 * 5, END_TIME = 5 * 20, VOTE_TIME = 10 * 20;

	private final Runnable prepareRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			setPhase(RoundPhase.PREPARING);
			boolean cont = getGame().roundPrepare();

			if(cont)
			{
				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), startRunnable, SPAWN_TIME);
				new PreparationCheckRunnable().start(5L);
			}
		}
	};
	private final Runnable startRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			setPhase(RoundPhase.STARTED);
			boolean cont = getGame().roundStart();

			if(cont)
				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), endRunnable, ROUND_TIME);
		}
	};
	private final Runnable endRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			setPhase(RoundPhase.ENDED);
			getGame().roundEnd(RoundEndReason.TIME_OUT);
		}
	};
	private final Runnable explodeRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			setPhase(RoundPhase.ENDED);
			getGame().roundEnd(RoundEndReason.EXPLODED);
		}
	};
	private final Runnable voteRunnable = new Runnable() {
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			getGame().startMapPoll();
		}
	};
	private final Runnable nextRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			getGame().roundNext();
		}
	};
	
	private final MSSceneListener<DefuseGame> listener;
	private PreparationCheckRunnable checker;
	private RoundPhase phase;
	private Integer taskId;
	private Long ranAt;
	
	public Round(DefuseGame game)
	{
		super(game, GamePhaseType.RUNNING);
		
		listener = new RoundListener(game);
	}
	
	public Round start()
	{
		prepareRunnable.run();
		return this;
	}
	
	public void startNextRunnable()
	{
		if(hasTask())
			cancelTask();
		
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), nextRunnable, END_TIME);
	}
	
	public void startVoteRunnable()
	{
		if(hasTask())
			cancelTask();
		
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), voteRunnable, VOTE_TIME);
	}
	
	public void startExplodeRunnable()
	{
		if(hasTask())
			cancelTask();
		
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), explodeRunnable, BOMB_TIME);
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
	
	public RoundPhase getPhase()
	{
		return phase;
	}

	public void setPhase(RoundPhase phase)
	{
		this.phase = phase;
	}

	public static enum RoundPhase
	{
		PREPARING, STARTED, PLANTED, ENDED;
		
		public RoundPhase getNext()
		{
			int index = ordinal();
			RoundPhase[] phases = values();
			
			return index < phases.length - 1 ? phases[index + 1] : null;
		}
	}
	
	@Override
	public void cancel()
	{
		cancelTask();
	}
	
	@Override
	public DefuseGame getGame()
	{
		return (DefuseGame) super.getGame();
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		listener.redirect(event, msPlayer);
		
		if(checker != null)
			checker.redirect(event, msPlayer);
	}
	
	public Long getRanAt()
	{
		return ranAt;
	}

	public void setRanAt(Long ranAt)
	{
		this.ranAt = ranAt;
	}

	private static class RoundListener extends MSSceneListener<DefuseGame>
	{
		public RoundListener(DefuseGame game)
		{
			super(game);
		}
		
		@EventHandler
		public void onArenaJoin(ArenaJoinEvent event, MSPlayer msPlayer)
		{
			DefuseGame game = getScene();
			GamePhase<GameLobby, TeamGameMenu, DefuseGameMap, DefuseEquipmentProvider> phase = game.getPhase();
			
			if(phase instanceof Round)
			{
				Round round = (Round) phase;
				RoundPhase roundPhase = round.getPhase();
				
				if(roundPhase == RoundPhase.PREPARING)
				{
					Player player = msPlayer.getPlayer();
					
					player.setWalkSpeed(0);
				}
				else if(roundPhase == RoundPhase.STARTED)
				{
					game.setDead(msPlayer, true);
				}
				else if(roundPhase == RoundPhase.ENDED)
				{
					Player player = msPlayer.getPlayer();
					
					player.setWalkSpeed(0);
				}
			}
		}
	}
	
	private class PreparationCheckRunnable extends MSListener implements Runnable
	{
		private final HashMap<MSPlayer, Location> ORIGIN = new HashMap<MSPlayer, Location>();
		public Integer preparationCheckTaskId;
		
		public int start(long frequency)
		{
			checker = this;
			return preparationCheckTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, 0L, frequency);
		}
		
		@Override
		public void run()
		{
			DefuseGame game = getGame();
			
			for(MSPlayer msPlayer : game.getPlayingPlayers())
			{
				Player player = msPlayer.getPlayer();
				Location origin = ORIGIN.get(msPlayer);
				Location loc = player.getLocation();
				
				if(origin == null)
					ORIGIN.put(msPlayer, loc);
				else if(origin.distanceSquared(loc) > 0)
				{
					origin.setYaw(loc.getYaw());
					origin.setPitch(loc.getPitch());
					
					msPlayer.teleport(origin, false);
				}
			}
			
			if(phase != RoundPhase.PREPARING && preparationCheckTaskId != null)
			{
				BukkitScheduler scheduler = Bukkit.getScheduler();
				
				scheduler.cancelTask(preparationCheckTaskId);
				
				preparationCheckTaskId = null;
				checker = null;
			}
		}
		
		@EventHandler
		public void onArenaJoin(ArenaJoinEvent event,final MSPlayer msPlayer)
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new Runnable() {

				@Override
				public void run()
				{
					Player player = msPlayer.getPlayer();
					Location loc = player.getLocation();
					
					ORIGIN.put(msPlayer, loc);
				}
				
			});
		}
		
		@EventHandler
		public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
		{
			ORIGIN.remove(msPlayer);
		}
	}
}