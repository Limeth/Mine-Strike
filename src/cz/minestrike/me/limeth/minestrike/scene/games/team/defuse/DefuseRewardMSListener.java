package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.scene.games.listeners.AbstractRewardMSListener;

/**
 * @author Limeth
 */
public class DefuseRewardMSListener extends AbstractRewardMSListener<DefuseGame>
{
	public static final long REQUIRED_PLAYTIME_MILLIS = (int) (DefuseRound.PHASE_RUNNING.getDuration() * 3D / 4D) * 1000 / 20;
	public static final double GLOBAL_CHANCE = 0.25;

	public DefuseRewardMSListener(DefuseGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, GLOBAL_CHANCE);
	}
}
