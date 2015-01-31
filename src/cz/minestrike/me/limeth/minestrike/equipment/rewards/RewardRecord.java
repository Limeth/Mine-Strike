package cz.minestrike.me.limeth.minestrike.equipment.rewards;

/**
 * @author Limeth
 */
public class RewardRecord extends PartialRewardRecord
{
	private int id;

	public RewardRecord(int id, String username, long timestamp)
	{
		super(username, timestamp);

		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
