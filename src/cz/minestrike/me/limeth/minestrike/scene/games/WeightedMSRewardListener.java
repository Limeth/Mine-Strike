package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.base.Optional;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

import java.util.Map;
import java.util.Random;

/**
 * @author Limeth
 */
public abstract class WeightedMSRewardListener<T extends Game> extends MSRewardListener<T>
{
	private static final Random RANDOM = new Random();
	private double                 globalChance;
	private Map<Equipment, Double> equipmentWeights;

	public WeightedMSRewardListener(T scene, long requiredPlaytimeMillis, int requiredKills, int maxRewardGenerosity)
	{
		super(scene, requiredPlaytimeMillis, requiredKills, maxRewardGenerosity);

		this.equipmentWeights = initEquipmentWeights();
		this.globalChance = initGlobalChance();
	}

	public abstract Map<Equipment, Double> initEquipmentWeights();
	public abstract double initGlobalChance();

	@Override
	public Optional<Equipment> getReward(MSPlayer msPlayer)
	{
		if(RANDOM.nextDouble() > globalChance)
			return Optional.absent();

		double range = getWeightRange();
		double weightedIndex = RANDOM.nextDouble() * range;
		double countedWeight = 0;

		for(Map.Entry<Equipment, Double> entry : equipmentWeights.entrySet())
		{
			countedWeight += entry.getValue();

			if(countedWeight > weightedIndex)
				return Optional.of(entry.getKey());
		}

		throw new RuntimeException("This shouldn't have happened.");
	}

	private double getWeightRange()
	{
		double range = 0;

		for(double weight : equipmentWeights.values())
			range += weight;

		return range;
	}
}
