package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.scene.games.listeners.AbstractRewardListener;

/**
 * @author Limeth
 */
public class DefuseRewardListener extends AbstractRewardListener<DefuseGame>
{
	public static final long REQUIRED_PLAYTIME_MILLIS = (int) (Round.ROUND_TIME * 3D / 4D) * 1000 / 20;
	public static final double GLOBAL_CHANCE = 0.25;

	public DefuseRewardListener(DefuseGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, GLOBAL_CHANCE);
	}
}
