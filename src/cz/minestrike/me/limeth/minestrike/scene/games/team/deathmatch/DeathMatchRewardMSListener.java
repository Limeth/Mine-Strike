package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.scene.games.listeners.AbstractRewardMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseRewardMSListener;

/**
 * @author Limeth
 */
public class DeathMatchRewardMSListener extends AbstractRewardMSListener<DeathMatchGame>
{
	public static final long   REQUIRED_PLAYTIME_MILLIS =
			(int) (DeathMatchRound.PHASE_RUNNING.getDuration() * 2D / 3D) * 1000 / 20;
	public static final double GLOBAL_CHANCE            = DefuseRewardMSListener.GLOBAL_CHANCE / 2;

	public DeathMatchRewardMSListener(DeathMatchGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, GLOBAL_CHANCE);
	}
}
