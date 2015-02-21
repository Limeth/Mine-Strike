package cz.minestrike.me.limeth.minestrike.dbi.binding;

import com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.base.Preconditions;
import org.bukkit.ChatColor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public final class MSPlayerData implements Serializable, ComparisonGenerating<MSPlayerData>
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

	/*
	* 	private String username;
	private int xp;
	private int kills;
	private int assists;
	private int deaths;
	private long playtime;
	* */

	@Override
	public List<String> generateComparison(MSPlayerData other)
	{
		List<String> result = Lists.newArrayList();

		if(!username.equals(other.username))
			result.add("Username: " + other.username + " > " + username);

		generateNumberComparison(result, "xp", "XP", this, other);
		generateNumberComparison(result, "kills", "kills", this, other);
		generateNumberComparison(result, "assists", "assists", this, other);
		generateNumberComparison(result, "deaths", "deaths", this, other);
		generateNumberComparison(result, "playtime", "playtime millis", this, other);

		return result;
	}

	private static void generateNumberComparison(List<String> result, String fieldName, String unit, MSPlayerData thiz, MSPlayerData that)
	{
		try
		{
			Field field = MSPlayerData.class.getDeclaredField(fieldName);
			Number valueThis = (Number) field.get(thiz);
			Number valueThat = (Number) field.get(that);
			long delta = valueThis.longValue() - valueThat.longValue();

			if(delta == 0)
				return;

			String prefix = delta < 0 ? ChatColor.RED + ChatColor.BOLD.toString() + delta
			                          : ChatColor.GREEN + ChatColor.BOLD.toString() + "+" + delta;

			result.add(prefix + " " + unit);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}

	}
}
