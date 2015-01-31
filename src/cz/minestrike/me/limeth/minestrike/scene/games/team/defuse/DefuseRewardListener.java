package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.FreeRewardEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.cases.Case;
import cz.minestrike.me.limeth.minestrike.scene.games.WeightedMSRewardListener;

import java.util.Map;

/**
 * @author Limeth
 */
public class DefuseRewardListener extends WeightedMSRewardListener<DefuseGame>
{
	public static final long REQUIRED_PLAYTIME_MILLIS = (int) (Round.ROUND_TIME * 3D / 4D) * 1000 / 20;
	public static final int REQUIRED_KILLS = 3;
	public static final double WEIGHT_CASES = 1;
	public static final double WEIGHT_FREE_REWARDS = 1;

	public DefuseRewardListener(DefuseGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, REQUIRED_KILLS, MSConfig.getMaxRewardGenerosity());
	}

	@Override
	public Map<Equipment, Double> initEquipmentWeights()
	{
		Map<Equipment, Double> weights = Maps.newHashMap();
		Case[] cases = Case.values();
		Equipment[] freeRewards = FreeRewardEquipment.VALUES;
		double caseWeight = WEIGHT_CASES * (double) freeRewards.length / (double) cases.length;
		double freeRewardWeight = WEIGHT_FREE_REWARDS * (double) cases.length / (double) freeRewards.length;

		for(Case caze : Case.values())
			weights.put(caze, caseWeight);

		for(Equipment freeReward : freeRewards)
			weights.put(freeReward, freeRewardWeight);

		return weights;
	}

	@Override
	public double initGlobalChance()
	{
		return 0.25;
	}
}
