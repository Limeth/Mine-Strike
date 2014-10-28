package cz.minestrike.me.limeth.minestrike.equipment.cases;

import java.util.Random;

import org.bukkit.ChatColor;

import cz.minestrike.me.limeth.minestrike.Translation;

public enum CaseContentRarity
{
	LEGENDARY(ChatColor.LIGHT_PURPLE, Translation.EQUIPMENT_RARITY_LEGENDARY, "5_legendary"),
	UNIQUE(ChatColor.RED, Translation.EQUIPMENT_RARITY_UNIQUE, "4_mythical"),
	RARE(ChatColor.GOLD, Translation.EQUIPMENT_RARITY_RARE, "3_rare"),
	VALUABLE(ChatColor.YELLOW, Translation.EQUIPMENT_RARITY_VALUABLE, "2_uncommon"),
	COMMON(ChatColor.GREEN, Translation.EQUIPMENT_RARITY_COMMON, "1_common");
	
	private static Integer $rarities;
	private final ChatColor color;
	private final Translation translation;
	private final String soundName;
	
	private CaseContentRarity(ChatColor color, Translation translation, String soundName)
	{
		this.color = color;
		this.translation = translation;
		this.soundName = "projectsurvive:counterstrike.ui.item_drop" + soundName;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public Translation getTranslation()
	{
		return translation;
	}
	
	public String getSoundName()
	{
		return soundName;
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
