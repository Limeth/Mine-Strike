package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.games.GamePhase;
import cz.minestrike.me.limeth.minestrike.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.games.team.defuse.DefuseGame.RoundEndReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSGameListener;

public class Round extends GamePhase<GameLobby, GameMenu, DefuseGameMap, DefuseEquipmentManager>
{
	public static final long SPAWN_TIME = 10 * 20, ROUND_TIME = 20 * 60 * 5, END_TIME = 5 * 20, VOTE_TIME = 10 * 20;

	private final Runnable prepareRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			boolean cont = getGame().roundPrepare();
			setPhase(RoundPhase.PREPARING);

			if(cont)
				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), startRunnable, SPAWN_TIME);
		}
	};
	private final Runnable startRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			boolean cont = getGame().roundStart();
			setPhase(RoundPhase.STARTED);

			if(cont)
				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), endRunnable, ROUND_TIME);
		}
	};
	private final Runnable endRunnable = new Runnable() {
		@Override
		public void run()
		{
			setRanAt(System.currentTimeMillis());
			getGame().roundEnd(RoundEndReason.TIME_OUT);
			setPhase(RoundPhase.ENDED);
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
	
	private final MSGameListener<DefuseGame> listener;
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
		PREPARING, STARTED, ENDED;
		
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
	}
	
	public Long getRanAt()
	{
		return ranAt;
	}

	public void setRanAt(Long ranAt)
	{
		this.ranAt = ranAt;
	}

	private static class RoundListener extends MSGameListener<DefuseGame>
	{
		public RoundListener(DefuseGame game)
		{
			super(game);
		}
		
		@EventHandler
		public void onArenaJoin(ArenaJoinEvent event, MSPlayer msPlayer)
		{
			DefuseGame game = getGame();
			GamePhase<GameLobby, GameMenu, DefuseGameMap, DefuseEquipmentManager> phase = game.getPhase();
			
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
}
