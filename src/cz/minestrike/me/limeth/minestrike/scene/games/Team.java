package cz.minestrike.me.limeth.minestrike.scene.games;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Team
{
	TERRORISTS("Terrorists", ChatColor.GOLD, DyeColor.ORANGE, "anarchist", "terwin"),
	COUNTER_TERRORISTS("Counter-Terrorists", ChatColor.BLUE, DyeColor.BLUE, "seal", "ctwin");
	
	private final String name;
	private final ChatColor chatColor;
	private final DyeColor dyeColor;
	private final String voiceDirectory;
	private final String winSound;
	
	private Team(String name, ChatColor chatColor, DyeColor dyeColor, String voice, String winSound)
	{
		this.name = name;
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
	
	public String getVoiceSound(VoiceSound sound)
	{
		return getVoiceSound(sound.toString());
	}
	
	public String getVoiceSound(String soundName)
	{
		return voiceDirectory + soundName;
	}

	public String getVoiceDirectory()
	{
		return voiceDirectory;
	}
}
