package cz.minestrike.me.limeth.minestrike.scene.games;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import cz.minestrike.me.limeth.minestrike.Translation;

public enum Team
{
	TERRORISTS(Translation.TEAM_TERRORISTS, Translation.GAME_JOIN_TEAM_T, ChatColor.GOLD, DyeColor.ORANGE, "anarchist", "terwin"),
	COUNTER_TERRORISTS(Translation.TEAM_COUNTERTERRORISTS, Translation.GAME_JOIN_TEAM_CT, ChatColor.BLUE, DyeColor.BLUE, "seal", "ctwin");
	
	private static final Translation JOIN_MESSAGE_SPECTATORS = Translation.GAME_JOIN_SPECTATORS;
	private final Translation name;
	private final Translation joinMessage;
	private final ChatColor chatColor;
	private final DyeColor dyeColor;
	private final String voiceDirectory;
	private final String winSound;
	
	private Team(Translation name, Translation joinMessage, ChatColor chatColor, DyeColor dyeColor, String voice, String winSound)
	{
		this.name = name;
		this.joinMessage = joinMessage;
		this.chatColor = chatColor;
		this.dyeColor = dyeColor;
		this.voiceDirectory = "projectsurvive:counterstrike.player.vo." + voice + ".";
		this.winSound = "projectsurvive:counterstrike.radio." + winSound;
	}
	
	public static Team getByItemColor(DyeColor color)
	{
		for(Team team : values())
			if(team.getDyeColor() == color)
				return team;
		
		return null;
	}
	
	/**
	 * @return The team's join message or spectator join message if null
	 */
	public static Translation getJoinMessage(Team team)
	{
		return team != null ? team.joinMessage : JOIN_MESSAGE_SPECTATORS;
	}
	
	public Translation getJoinMessage()
	{
		return joinMessage;
	}
	
	public String getColoredName()
	{
		return chatColor + getName();
	}
	
	public Team getOppositeTeam()
	{
		return values()[(ordinal() + 1) % 2];
	}

	public String getName()
	{
		return name.getMessage();
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
	
	public String getVoiceDirectory()
	{
		return voiceDirectory;
	}
}
