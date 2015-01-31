package cz.minestrike.me.limeth.minestrike.equipment.rewards;

/**
 * @author Limeth
 */
public class PartialRewardRecord
{
	private String username;
	private long timestamp;

	public PartialRewardRecord(String username, long timestamp)
	{
		this.username = username;
		this.timestamp = timestamp;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}
