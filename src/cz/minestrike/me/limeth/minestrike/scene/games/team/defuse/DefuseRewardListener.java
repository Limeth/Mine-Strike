package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.cases.Case;
import cz.minestrike.me.limeth.minestrike.scene.games.WeightedMSRewardListener;

import java.util.Map;

/**
 * @author Limeth
 */
public class DefuseRewardListener extends WeightedMSRewardListener<DefuseGame>
{
	public static final long REQUIRED_PLAYTIME_MILLIS = (int) (Round.ROUND_TIME * 3D / 4D) * 1000 / 20;
	public static final int REQUIRED_KILLS = 0;//3;
	public static final double WEIGHT_CASE = 1;

	public DefuseRewardListener(DefuseGame scene)
	{
		super(scene, REQUIRED_PLAYTIME_MILLIS, REQUIRED_KILLS);
	}

	@Override
	public Map<Equipment, Double> initEquipmentWeights()
	{
		Map<Equipment, Double> weights = Maps.newHashMap();

		for(Case caze : Case.values())
			weights.put(caze, WEIGHT_CASE);

		return weights;
	}

	@Override
	public double initGlobalChance()
	{
		return 1; //TODO Change to 0.25 or something
	}
}
