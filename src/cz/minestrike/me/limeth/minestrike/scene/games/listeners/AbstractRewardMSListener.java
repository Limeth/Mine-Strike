package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomizationManager;
import cz.minestrike.me.limeth.minestrike.equipment.cases.AbstractCase;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

import java.util.List;
import java.util.Map;

/**
 * @author Limeth
 */
public abstract class AbstractRewardMSListener<T extends Game> extends WeightedRewardMSListener<T>
{
	public static final int REQUIRED_KILLS = 3;
	public static final double WEIGHT_CASES = 1;
	public static final double WEIGHT_FREE_REWARDS = 1;
	private final double globalChance;

	public AbstractRewardMSListener(T scene, long requiredPlaytimeMillis, double globalChance)
	{
		super(scene, requiredPlaytimeMillis, REQUIRED_KILLS, MSConfig.getMaxRewardGenerosity());

		this.globalChance = globalChance;
	}

	@Override
	public Map<Equipment, Double> initEquipmentWeights()
	{
		Map<Equipment, Double> weights = Maps.newHashMap();
		List<AbstractCase> cases = EquipmentCustomizationManager.getCases();
		List<Equipment> freeRewards = EquipmentCustomizationManager.getFreeEquipment();
		double caseWeight = WEIGHT_CASES * (double) freeRewards.size() / (double) cases.size();
		double freeRewardWeight = WEIGHT_FREE_REWARDS * (double) cases.size() / (double) freeRewards.size();

		for(AbstractCase caze : cases)
			weights.put(caze, caseWeight);

		for(Equipment freeReward : freeRewards)
			weights.put(freeReward, freeRewardWeight);

		return weights;
	}

	@Override
	public double getGlobalChance()
	{
		return globalChance;
	}
}
