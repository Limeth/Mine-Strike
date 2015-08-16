package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import com.google.common.base.Optional;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

import java.util.Map;
import java.util.Random;

/**
 * @author Limeth
 */
public abstract class WeightedRewardMSListener<T extends Game> extends RewardMSListener<T>
{
	private static final Random RANDOM = new Random();
	private Map<Equipment, Double> equipmentWeights;

	public WeightedRewardMSListener(T scene, long requiredPlaytimeMillis, int requiredKills, int maxRewardGenerosity)
	{
		super(scene, requiredPlaytimeMillis, requiredKills, maxRewardGenerosity);

		this.equipmentWeights = initEquipmentWeights();
	}

	public abstract Map<Equipment, Double> initEquipmentWeights();
	public abstract double getGlobalChance();

	@Override
	public Optional<Equipment> getReward(MSPlayer msPlayer)
	{
		if(RANDOM.nextDouble() > getGlobalChance())
			return Optional.absent();

		double range = getWeightRange();
		double weightedIndex = RANDOM.nextDouble() * range;
		double countedWeight = 0;

		for(Map.Entry<Equipment, Double> entry : equipmentWeights.entrySet())
		{
			countedWeight += entry.getValue();

			if(countedWeight >= weightedIndex)
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
