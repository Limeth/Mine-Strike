package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.RoundPhase;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RespawnMovementRunnable;
import org.bukkit.Location;

/**
 * Created by limeth on 29.7.15.
 */
public class CheckMovedRunnable extends RespawnMovementRunnable<DeathMatchGame>
{
    public static final double SENSITIVITY = 0.25;
    public static final long FREQUENCY = 5;

    public CheckMovedRunnable(DeathMatchGame game)
    {
        super(game, SENSITIVITY, FREQUENCY);
    }

    @Override
    public void moved(MSPlayer msPlayer, Location origin)
    {
        DeathMatchGame game = getScene();

        //Don't disable shop when moved if players are getting prepared
        GamePhaseType gamePhaseType = game.getPhaseType();

        if(gamePhaseType == GamePhaseType.RUNNING)
        {
            DeathMatchRound round = game.getRound();
            RoundPhase roundPhase = round.getPhase();

            if(roundPhase == DeathMatchRound.PHASE_PREPARATION)
                return;
        }

        game.setMoved(msPlayer, true);
    }
}
