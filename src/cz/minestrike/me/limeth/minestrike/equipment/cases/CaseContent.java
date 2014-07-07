package cz.minestrike.me.limeth.minestrike.equipment.cases;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

public class CaseContent
{
	private final Equipment equipment;
	private final CaseContentRarity rarity;
	
	public CaseContent(Equipment equipment, CaseContentRarity rarity)
	{
		this.equipment = equipment;
		this.rarity = rarity;
	}

	public Equipment getEquipment()
	{
		return equipment;
	}

	public CaseContentRarity getRarity()
	{
		return rarity;
	}
}
