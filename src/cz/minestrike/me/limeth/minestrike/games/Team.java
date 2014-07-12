package cz.minestrike.me.limeth.minestrike.games;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Team
{
	TERRORISTS("Terrorists", ChatColor.GOLD, DyeColor.ORANGE, "terwin"),
	COUNTER_TERRORISTS("Counter-Terrorists", ChatColor.BLUE, DyeColor.BLUE, "ctwin");
	
	private final String name;
	private final ChatColor chatColor;
	private final DyeColor dyeColor;
	private final String winSound;
	
	private Team(String name, ChatColor chatColor, DyeColor dyeColor, String winSound)
	{
		this.name = name;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
		this.winSound = "projectsurvive:counterstrike.radio." + winSound;
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

	public String getWinSound()
	{
		return winSound;
	}
}
