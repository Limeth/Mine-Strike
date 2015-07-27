package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhase;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.team.PreparationCheckRunnable;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseRound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class DeathMatchRound extends GamePhase<DeathMatchGame>
{
    public static final long PREPARE_TIME = 10 * 20, ROUND_TIME = 20 * 60 * 3, END_TIME = 5 * 20, VOTE_TIME = 10 * 20;

    private void onPrepare()
    {
        setRanAt(System.currentTimeMillis());
        setPhase(DeathMatchRoundPhase.PREPARING);
        boolean cont = getGame().roundPrepare();

        if(cont)
        {
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onStart, PREPARE_TIME);
            startPreparationCheckRunnable();
        }
    }

    private void onStart()
    {
        setRanAt(System.currentTimeMillis());
        setPhase(DeathMatchRoundPhase.STARTED);
        boolean cont = getGame().roundStart();

        if (cont)
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onEnd, ROUND_TIME);
    }

    private void onEnd()
    {
        setRanAt(System.currentTimeMillis());

        if(!hasEnded())
            getGame().roundEnd();
    }

    private void onVote()
    {
        setRanAt(System.currentTimeMillis());
        getGame().startMapPoll();
    }

    private void onNext()
    {
        setRanAt(System.currentTimeMillis());
        getGame().roundNext();
    }

    private final MSSceneListener<DeathMatchGame> listener;
    private PreparationCheckRunnable checker;
    private DeathMatchRoundPhase phase;
    private Integer taskId;
    private Long ranAt;

    public DeathMatchRound(DeathMatchGame game)
    {
        super(game, GamePhaseType.RUNNING);

        listener = new RoundListener(game);
    }

    @Override
    public DeathMatchRound start()
    {
        onPrepare();
        return this;
    }

    public void startNextRunnable()
    {
        if(hasTask())
            cancelTask();

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onNext, END_TIME);
    }

    public void startVoteRunnable()
    {
        if(hasTask())
            cancelTask();

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onVote, VOTE_TIME);
    }

    private void startPreparationCheckRunnable()
    {
        checker = new PreparationCheckRunnable(getGame(), () -> phase == DeathMatchRoundPhase.PREPARING, () -> checker = null);
        checker.start(5L);
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

    public DeathMatchRoundPhase getPhase()
    {
        return phase;
    }

    public void setPhase(DeathMatchRoundPhase phase)
    {
        this.phase = phase;
    }

    public static enum DeathMatchRoundPhase
    {
        PREPARING, STARTED, ENDED;

        public DeathMatchRoundPhase getNext()
        {
            int index = ordinal();
            DeathMatchRoundPhase[] phases = values();

            return index < phases.length - 1 ? phases[index + 1] : null;
        }
    }

    @Override
    public void cancel()
    {
        cancelTask();
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

    public boolean hasEnded()
    {
        return phase == DeathMatchRoundPhase.ENDED;
    }

    private static class RoundListener extends MSSceneListener<DeathMatchGame>
    {
        public RoundListener(DeathMatchGame game)
        {
            super(game);
        }

        @EventHandler
        public void onArenaJoin(ArenaJoinEvent event, MSPlayer msPlayer)
        {
            DeathMatchGame game = getScene();
            GamePhase<? extends Game> phase = game.getPhase();

            if(phase instanceof DeathMatchRound)
            {
                DeathMatchRound round = (DeathMatchRound) phase;
                DeathMatchRoundPhase roundPhase = round.getPhase();

                if(roundPhase == DeathMatchRoundPhase.PREPARING || roundPhase == DeathMatchRoundPhase.ENDED)
                {
                    Player player = msPlayer.getPlayer();

                    player.setWalkSpeed(0);
                }
            }
        }
    }
}
