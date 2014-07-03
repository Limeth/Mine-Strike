package cz.minestrike.me.limeth.minestrike.games;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Team
{
	TERRORISTS("Terrorists", ChatColor.GOLD, DyeColor.ORANGE), COUNTER_TERRORISTS("Counter-Terrorists", ChatColor.BLUE, DyeColor.BLUE);
	
	private final String name;
	private final ChatColor chatColor;
	private final DyeColor dyeColor;
	
	private Team(String name, ChatColor chatColor, DyeColor dyeColor)
	{
		this.name = name;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
	}
	
	public static Team getByItemColor(DyeColor color)
	{
		for(Team team : values())
			if(team.getDyeColor() == color)
				return team;
		
		return null;
	}
	
	public String getColoredName()
	{
		return chatColor + name;
	}
	
	public Team getOppositeTeam()
	{
		return values()[(ordinal() + 1) % 2];
	}

	public String getName()
	{
		return name;
	}

	public ChatColor getChatColor()
	{
		return chatColor;
	}

	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
}
