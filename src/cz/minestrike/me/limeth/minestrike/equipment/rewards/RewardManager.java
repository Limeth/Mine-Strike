package cz.minestrike.me.limeth.minestrike.equipment.rewards;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.dbi.RewardRecordDAO;

import java.util.List;
import java.util.Map;

/**
 * @author Limeth
 */
public class RewardManager
{
	public static final long REWARD_PERIOD_DEFAULT         = 1000 * 60 * 60 * 24 * 2; //2 days
	public static final int  REWARD_AMOUNT_DEFAULT         = 1;
	public static final int  REWARD_GENEROSITY_MAX_DEFAULT = 3;

	private static RewardManager instance;
	private Map<String, List<PartialRewardRecord>> lazyRecords = Maps.newHashMap();

	private RewardManager()
	{
	}

	public boolean isRewardable(String playerName)
	{
		long rewardPeriod = MSConfig.getRewardPeriod();
		int rewardAmount = MSConfig.getRewardAmount();

		if(rewardAmount <= 0)
			return false;
		else if(rewardPeriod <= 0)
			return true;

		List<PartialRewardRecord> specificRecords = getRecords(playerName);

		if(specificRecords.size() <= 0)
			return true;

		long now = System.currentTimeMillis();
		long minTimestamp = now - rewardPeriod;
		int recordsInPeriod = 0;

		for(PartialRewardRecord record : specificRecords)
			if(record.getTimestamp() >= minTimestamp)
				recordsInPeriod++;

		return recordsInPeriod < rewardAmount;
	}

	public void addRecord(String playerName, long timestamp)
	{
		PartialRewardRecord record = new PartialRewardRecord(playerName, timestamp);
		List<PartialRewardRecord> specificRecords = getRecords(playerName);

		for(int i = 0; i <= specificRecords.size(); i++)
			if(i == specificRecords.size())
			{
				specificRecords.add(record);
				break;
			}
			else if(specificRecords.get(i).getTimestamp() > timestamp)
			{
				specificRecords.add(i, record);
				break;
			}

		RewardRecordDAO.insertRewardRecord(record);
	}

	public void addRecord(String playerName)
	{
		addRecord(playerName, System.currentTimeMillis());
	}

	private List<PartialRewardRecord> getRecords(String playerName)
	{
		checkRecords(playerName);
		return lazyRecords.get(playerName);
	}

	private boolean checkRecords(String playerName)
	{
		long rewardPeriod = MSConfig.getRewardPeriod();
		List<PartialRewardRecord> specificRecords = lazyRecords.get(playerName);

		if(specificRecords == null)
		{
			specificRecords = Lists.newArrayList(RewardRecordDAO.selectRewardRecords(playerName, rewardPeriod));

			lazyRecords.put(playerName, specificRecords);
			MineStrike.info("Caching " + specificRecords.size() + " records for player " + playerName);
			return true;
		}

		return false;
	}

	public static RewardManager getInstance()
	{
		return instance != null ? instance : (instance = new RewardManager());
	}
}
