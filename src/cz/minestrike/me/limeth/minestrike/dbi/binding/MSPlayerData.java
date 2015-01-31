package cz.minestrike.me.limeth.minestrike.dbi.binding;

import net.minecraft.util.com.google.common.base.Preconditions;

import java.io.Serializable;

public final class MSPlayerData implements Serializable
{
	private static final long serialVersionUID = -1098907718410140897L;
	
	private String username;
	private int xp;
	private int kills;
	private int assists;
	private int deaths;
	private long playtime;

	MSPlayerData(String username, int xp, int kills, int assists, int deaths, long playtime)
	{
		setUsername(username);
		setXp(xp);
		setKills(kills);
		setAssists(assists);
		setDeaths(deaths);
		setPlaytime(playtime);
	}
	
	public MSPlayerData(String username)
	{
		this(username, 0, 0, 0, 0, 0);
	}

	public MSPlayerData()
	{
		this(null, 0, 0, 0, 0, 0);
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		Preconditions.checkNotNull(username);
		
		this.username = username;
	}

	public int getXp()
	{
		return xp;
	}

	public void setXp(int xp)
	{
		Preconditions.checkArgument(xp >= 0);
		
		this.xp = xp;
	}

	public int getKills()
	{
		return kills;
	}

	public void setKills(int kills)
	{
		Preconditions.checkArgument(kills >= 0);
		
		this.kills = kills;
	}

	public int getAssists()
	{
		return assists;
	}

	public void setAssists(int assists)
	{
		Preconditions.checkArgument(assists >= 0);
		
		this.assists = assists;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public void setDeaths(int deaths)
	{
		Preconditions.checkArgument(deaths >= 0);
		
		this.deaths = deaths;
	}

	public long getPlaytime()
	{
		return playtime;
	}

	public void setPlaytime(long playtime)
	{
		Preconditions.checkArgument(playtime >= 0);
		
		this.playtime = playtime;
	}
}
