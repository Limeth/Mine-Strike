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
	
	SILVER_I(ChatColor.GRAY + "Silver I", ChatColor.GRAY + "〈"),
	SILVER_II(ChatColor.GRAY + "Silver II", ChatColor.GRAY + "〇〈"),
	SILVER_III(ChatColor.GRAY + "Silver III", ChatColor.GRAY + "〉"),
	SILVER_IV(ChatColor.GRAY + "Silver IV", ChatColor.GRAY + "〇〉"),
	SILVER_V(ChatColor.GRAY + "Silver V", ChatColor.GRAY + "〆〉"),
	SILVER_VI(ChatColor.GRAY + "Silver VI", ChatColor.GRAY + "《"),
	SILVER_VII(ChatColor.GRAY + "Silver VII", ChatColor.GRAY + "〇《"),
	SILVER_VIII(ChatColor.GRAY + "Silver VIII", ChatColor.GRAY + "〆《"),
	SILVER_IX(ChatColor.GRAY + "Silver IX", ChatColor.GRAY + "々《"),
	SILVER_X(ChatColor.GRAY + "Silver X", ChatColor.GRAY + "々★《"),
	GOLD_I(ChatColor.GOLD + "Gold I", ChatColor.GOLD + "』"),
	GOLD_II(ChatColor.GOLD + "Gold II", ChatColor.GOLD + "」"),
	GOLD_III(ChatColor.GOLD + "Gold III", ChatColor.GOLD + "』』"),
	GOLD_IV(ChatColor.GOLD + "Gold IV", ChatColor.GOLD + "」』"),
	GOLD_V(ChatColor.GOLD + "Gold V", ChatColor.GOLD + "」」"),
	GOLD_VI(ChatColor.GOLD + "Gold VI", ChatColor.GOLD + "』』』"),
	GOLD_VII(ChatColor.GOLD + "Gold VII", ChatColor.GOLD + "」』』"),
	GOLD_VIII(ChatColor.GOLD + "Gold VIII", ChatColor.GOLD + "」」』"),
	GOLD_IX(ChatColor.GOLD + "Gold IX", ChatColor.GOLD + "」」」"),
	PLATINUM_I(ChatColor.AQUA + "Platinum I", ChatColor.AQUA + "『"),
	PLATINUM_II(ChatColor.AQUA + "Platinum II", ChatColor.AQUA + "「"),
	PLATINUM_III(ChatColor.AQUA + "Platinum III", ChatColor.AQUA + "『『"),
	PLATINUM_IV(ChatColor.AQUA + "Platinum IV", ChatColor.AQUA + "「『"),
	PLATINUM_V(ChatColor.AQUA + "Platinum V", ChatColor.AQUA + "「「"),
	PLATINUM_VI(ChatColor.AQUA + "Platinum VI", ChatColor.AQUA + "『『『"),
	PLATINUM_VII(ChatColor.AQUA + "Platinum VII", ChatColor.AQUA + "「『『"),
	PLATINUM_VIII(ChatColor.AQUA + "Platinum VIII", ChatColor.AQUA + "「「『"),
	PLATINUM_IX(ChatColor.AQUA + "Platinum IX", ChatColor.AQUA + "「「「"),
	SUPREME_MASTER(ChatColor.LIGHT_PURPLE + "Supreme Master", ChatColor.LIGHT_PURPLE + "TODO"), //TODO
	GLOBAL_ELITE(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Global Elite", ChatColor.LIGHT_PURPLE + "TODO"); //TODO
	
	private static final double DELAY = 1000, BASE = 2, RANK_MODIFIER = 0.125, MODIFIER = 160000;
	private final String name, tag;
	
	private Rank(String name, String tag)
	{
		this.name = name;
		this.tag = tag;
	}
	
	public static Rank getForXP(int xp)
	{
		int level = xpToLevel(xp);
		
		if(level < 1)
			return null;
		
		Rank[] ranks = values();
		
		return ranks[level - 1];
	}
	
	private static int xpToLevel(int xp)
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
	
	@Override
	public String toString()
	{
		return name() + " (Name: " + getName() + "; Tag: " + getTag() + "; Level: " + getLevel() + "; Required XP: " + getRequiredXP() + ")";
	}
}
