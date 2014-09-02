package cz.minestrike.me.limeth.minestrike;

import org.bukkit.ChatColor;

public enum Rank
{
	/*
	 * Characters:
	 * - *:
	 *   - Small: ★
	 *   - 3 points:
	 *   	- outline: 』
	 *   	- opaque: 」
	 *   - 5 points:
	 *   	- outline: 『
	 *   	- opaque: 「
	 * - >
	 *   - One: 〈
	 *   - Two: 〉
	 *   - Three: 《
	 *   - Four: 》
	 * - (
	 *   - One: 〇
	 *   - Two: 〆
	 *   - Three: 々
	 *   - Four: 〄
	 */
	
	SILVER_I(ChatColor.GRAY + "Silver I", "〈"),
	SILVER_II(ChatColor.GRAY + "Silver II", "〇〈"),
	SILVER_III(ChatColor.GRAY + "Silver III", "〉"),
	SILVER_IV(ChatColor.GRAY + "Silver IV", "〇〉"),
	SILVER_V(ChatColor.GRAY + "Silver V", "〆〉"),
	SILVER_VI(ChatColor.GRAY + "Silver VI", "《"),
	SILVER_VII(ChatColor.GRAY + "Silver VII", "〇《"),
	SILVER_VIII(ChatColor.GRAY + "Silver VIII", "〆《"),
	SILVER_IX(ChatColor.GRAY + "Silver IX", "々《"),
	SILVER_X(ChatColor.GRAY + "Silver X", "々★《"),
	GOLD_I(ChatColor.GOLD + "Gold I", "』"),
	GOLD_II(ChatColor.GOLD + "Gold II", "」"),
	GOLD_III(ChatColor.GOLD + "Gold III", "』』"),
	GOLD_IV(ChatColor.GOLD + "Gold IV", "」』"),
	GOLD_V(ChatColor.GOLD + "Gold V", "」」"),
	GOLD_VI(ChatColor.GOLD + "Gold VI", "』』』"),
	GOLD_VII(ChatColor.GOLD + "Gold VII", "」』』"),
	GOLD_VIII(ChatColor.GOLD + "Gold VIII", "」」』"),
	GOLD_IX(ChatColor.GOLD + "Gold IX", "」」」"),
	PLATINUM_I(ChatColor.AQUA + "Platinum I", "『"),
	PLATINUM_II(ChatColor.AQUA + "Platinum II", "「"),
	PLATINUM_III(ChatColor.AQUA + "Platinum III", "『『"),
	PLATINUM_IV(ChatColor.AQUA + "Platinum IV", "「『"),
	PLATINUM_V(ChatColor.AQUA + "Platinum V", "「「"),
	PLATINUM_VI(ChatColor.AQUA + "Platinum VI", "『『『"),
	PLATINUM_VII(ChatColor.AQUA + "Platinum VII", "「『『"),
	PLATINUM_VIII(ChatColor.AQUA + "Platinum VIII", "「「『"),
	PLATINUM_IX(ChatColor.AQUA + "Platinum IX", "「「「"),
	SUPREME_MASTER(ChatColor.LIGHT_PURPLE + "Supreme Master", ChatColor.LIGHT_PURPLE + "TODO"), //TODO
	GLOBAL_ELITE(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Global Elite", ChatColor.LIGHT_PURPLE + "TODO"); //TODO

	private static final double DELAY = 1000, BASE = 2.5, RANK_MODIFIER = 0.125, MODIFIER = 105000;
	private final String name, tag;
	
	private Rank(String name, String tag)
	{
		this.name = name;
		this.tag = tag;
	}
	
	public static Rank getForXP(long xp)
	{
		int level = xpToLevel(xp);
		
		if(level < 1)
			return null;
		
		Rank[] ranks = values();
		
		return ranks[level - 1];
	}
	
	private static int xpToLevel(long xp)
	{
		double level = 1 + (log(BASE, ((xp - DELAY) / MODIFIER) + 1) / RANK_MODIFIER);
		Rank[] ranks = values();
		int length = ranks.length;
		
		if(level < 1)
			return 0;
		else if(level > length)
			return length;
		else
			return (int) level;
	}
	
	private static double log(double base, double n)
	{
		return Math.log(n) / Math.log(base);
	}
	
	public long getRequiredXP()
	{
		return (long) (((Math.pow(BASE, ordinal() * RANK_MODIFIER) - 1) * MODIFIER + DELAY));
	}
	
	public int getLevel()
	{
		return ordinal() + 1;
	}

	public String getName()
	{
		return name;
	}

	public String getTag()
	{
		return tag;
	}
	
	public static Rank getNext(Rank rank)
	{
		if(rank == null)
			return Rank.SILVER_I;
		else
			return rank.getNext();
	}
	
	public Rank getNext()
	{
		int ordinal = ordinal();
		Rank[] values = values();
		
		if(ordinal >= values.length - 1)
			return null;
		
		return values[ordinal + 1];
	}
	
	public Rank getPrevious()
	{
		int ordinal = ordinal();
		Rank[] values = values();
		
		if(ordinal <= 0)
			return null;
		
		return values[ordinal - 1];
	}
	
	@Override
	public String toString()
	{
		return name() + " (Name: " + getName() + "; Tag: " + getTag() + "; Level: " + getLevel() + "; Required XP: " + getRequiredXP() + ")";
	}
}
