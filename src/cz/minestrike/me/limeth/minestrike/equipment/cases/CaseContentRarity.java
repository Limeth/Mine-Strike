package cz.minestrike.me.limeth.minestrike.equipment.cases;

import java.util.Random;

import org.bukkit.ChatColor;

import cz.minestrike.me.limeth.minestrike.Translation;

public enum CaseContentRarity
{
	LEGENDARY(ChatColor.LIGHT_PURPLE, Translation.EQUIPMENT_RARITY_LEGENDARY),
	UNIQUE(ChatColor.RED, Translation.EQUIPMENT_RARITY_UNIQUE),
	RARE(ChatColor.GOLD, Translation.EQUIPMENT_RARITY_RARE),
	VALUABLE(ChatColor.YELLOW, Translation.EQUIPMENT_RARITY_VALUABLE),
	COMMON(ChatColor.GREEN, Translation.EQUIPMENT_RARITY_COMMON);
	
	private static Integer $rarities;
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
	
	public String getColoredName()
	{
		return color + translation.getMessage();
	}
	
	public static CaseContentRarity getRandom(Random random)
	{
		int rarities = getRarities();
		int selectedRarity = 1 + random.nextInt(rarities);
		int currentRarity = 0;
		CaseContentRarity[] values = values();
		
		for(int i = 0; i < values.length; i++)
		{
			currentRarity += (int) Math.pow(2, i);
			
			if(currentRarity >= selectedRarity)
				return values[i];
		}
		
		throw new RuntimeException("Whoops.");
	}
	
	private static int getRarities()
	{
		if($rarities == null)
		{
			$rarities = 0;
			
			for(int i = 0; i < values().length; i++)
				$rarities += (int) Math.pow(2, i);
		}
		
		return $rarities;
	}
}
