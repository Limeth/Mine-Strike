package cz.minestrike.me.limeth.minestrike.equipment.cases;

import org.bukkit.ChatColor;

import cz.minestrike.me.limeth.minestrike.Translation;

public enum CaseContentRarity
{
	COMMON(ChatColor.GREEN, Translation.EQUIPMENT_RARITY_COMMON),
	VALUABLE(ChatColor.YELLOW, Translation.EQUIPMENT_RARITY_VALUABLE),
	RARE(ChatColor.GOLD, Translation.EQUIPMENT_RARITY_RARE),
	UNIQUE(ChatColor.RED, Translation.EQUIPMENT_RARITY_UNIQUE),
	LEGENDARY(ChatColor.LIGHT_PURPLE, Translation.EQUIPMENT_RARITY_LEGENDARY);
	
	private final ChatColor color;
	private final Translation translation;
	
	private CaseContentRarity(ChatColor color, Translation translation)
	{
		this.color = color;
		this.translation = translation;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public Translation getTranslation()
	{
		return translation;
	}
}
