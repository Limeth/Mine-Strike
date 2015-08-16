package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhase;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.RoundPhase;
import cz.minestrike.me.limeth.minestrike.scene.games.team.PreparationCheckRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.util.List;

public class DeathMatchRound extends GamePhase<DeathMatchGame>
{
    public static final RoundPhase PHASE_PREPARATION = new RoundPhase("PREPARATION", 10 * 20),
                                    PHASE_RUNNING = new RoundPhase("RUNNING", 20 * 60 * 10),
                                    PHASE_END = new RoundPhase("END", 5 * 20),
                                    PHASE_POLL = new RoundPhase("POLL", 10 * 20);
    public static final List<RoundPhase> PHASES = ImmutableList.of(PHASE_PREPARATION, PHASE_RUNNING, PHASE_END, PHASE_POLL);

    private void onPrepare()
    {
        setRanAt(System.currentTimeMillis());
        setPhase(PHASE_PREPARATION);
        boolean cont = getGame().roundPrepare();

        if(cont)
        {
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onStart, PHASE_PREPARATION.getDuration());
            startPreparationCheckRunnable();
        }
    }

    private void onStart()
    {
        setRanAt(System.currentTimeMillis());
        setPhase(PHASE_RUNNING);
        boolean cont = getGame().roundStart();

        if (cont)
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::onEnd, PHASE_RUNNING.getDuration());
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

    private final SceneMSListener<DeathMatchGame> listener;
    private       PreparationCheckRunnable        checker;
    private       RoundPhase                      phase;
    private       Integer                         taskId;
    private       Long                            ranAt;

    public DeathMatchRound(DeathMatchGame game)
    {
        super(game, GamePhaseType.RUNNING);

        listener = new RoundMSListener(game);
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

        taskId = Bukkit.getScheduler()
                       .scheduleSyncDelayedTask(MineStrike.getInstance(), this::onNext, PHASE_END.getDuration());
    }

    public void startVoteRunnable()
    {
        if(hasTask())
            cancelTask();

        taskId = Bukkit.getScheduler()
                       .scheduleSyncDelayedTask(MineStrike.getInstance(), this::onVote, PHASE_POLL.getDuration());
    }

    private void startPreparationCheckRunnable()
    {
        checker = new PreparationCheckRunnable(getGame(), () -> phase == PHASE_PREPARATION, () -> checker = null);
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

    public RoundPhase getPhase()
    {
        return phase;
    }

    public void setPhase(RoundPhase phase)
    {
        Preconditions.checkArgument(PHASES.contains(phase), "Invalid phase: " + phase);

        this.phase = phase;
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
        return phase == PHASE_END;
    }

    private static class RoundMSListener extends SceneMSListener<DeathMatchGame>
    {
        public RoundMSListener(DeathMatchGame game)
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
                RoundPhase roundPhase = round.getPhase();

                if(roundPhase == PHASE_PREPARATION || roundPhase == PHASE_END)
                {
                    Player player = msPlayer.getPlayer();

                    player.setWalkSpeed(0);
                }
            }
        }
    }
}
