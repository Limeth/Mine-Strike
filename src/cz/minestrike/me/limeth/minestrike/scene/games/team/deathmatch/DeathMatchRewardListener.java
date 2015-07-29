package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.scene.games.listeners.AbstractRewardListener;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseRewardListener;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseRound;

/**
 * @author Limeth
 */
public class DeathMatchRewardListener extends AbstractRewardListener<DeathMatchGame>
{
	public static final long REQUIRED_PLAYTIME_MILLIS = (int) (DeathMatchRound.PHASE_RUNNING.getDuration() * 2D / 3D) * 1000 / 20;
	public static final double GLOBAL_CHANCE = DefuseRewardListener.GLOBAL_CHANCE / 2;

	public DeathMatchRewardListener(DeathMatchGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, GLOBAL_CHANCE);
	}
}
