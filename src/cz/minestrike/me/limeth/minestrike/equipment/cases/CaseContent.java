package cz.minestrike.me.limeth.minestrike.equipment.cases;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

import java.util.ArrayList;

public class CaseContent
{
	private final Equipment equipment;
	private final CaseContentRarity rarity;
	
	public CaseContent(Equipment equipment, CaseContentRarity rarity)
	{
		Preconditions.checkNotNull(equipment, "The equipment must not be null!");
		Preconditions.checkNotNull(rarity, "The rarity must not be null!");

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

	public static class ArrayBuilder
	{
		private final ArrayList<CaseContent> list = new ArrayList<>();

		public ArrayBuilder add(Equipment equipment, CaseContentRarity rarity)
		{
			list.add(new CaseContent(equipment, rarity));
			return this;
		}

		public ArrayBuilder common(Equipment equipment)
		{
			return add(equipment, CaseContentRarity.COMMON);
		}

		public ArrayBuilder valuable(Equipment equipment)
		{
			return add(equipment, CaseContentRarity.VALUABLE);
		}

		public ArrayBuilder rare(Equipment equipment)
		{
			return add(equipment, CaseContentRarity.RARE);
		}

		public ArrayBuilder unique(Equipment equipment)
		{
			return add(equipment, CaseContentRarity.UNIQUE);
		}

		public ArrayBuilder legendary(Equipment equipment)
		{
			return add(equipment, CaseContentRarity.LEGENDARY);
		}

		public CaseContent[] build()
		{
			return list.toArray(new CaseContent[list.size()]);
		}
	}
}
