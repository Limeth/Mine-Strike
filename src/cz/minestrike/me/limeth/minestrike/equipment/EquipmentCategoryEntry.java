package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang.Validate;

import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;

public class EquipmentCategoryEntry
{
	static
	{
		register(GunType.P2000, GunType.USP_S);
		register(GunType.M4A4, GunType.M4A1_S);
		
		for(Equipment equipment : EquipmentManager.getEquipment())
			try
			{
				register(equipment);
			}
			catch(Exception e) {}
	}
	
	private static final HashSet<EquipmentCategoryEntry> VALUES = new HashSet<EquipmentCategoryEntry>();
	private final FilledHashSet<Equipment> sourceEquipment;
	private final Equipment defaultEquipment;
	
	private EquipmentCategoryEntry(FilledHashSet<Equipment> sourceEquipment, Equipment defaultEquipment)
	{
		this.sourceEquipment = sourceEquipment;
		this.defaultEquipment = defaultEquipment;
	}
	
	private static void register(Equipment... sourceEquipment)
	{
		Validate.isTrue(sourceEquipment.length > 0, "Source equipment length must be larger than 0!");
		
		FilledHashSet<Equipment> sourceEquipmentSet = new FilledHashSet<Equipment>(Arrays.asList(sourceEquipment));;
		
		for(EquipmentCategoryEntry entry : VALUES)
			for(Equipment curEquipment : entry.sourceEquipment)
				if(sourceEquipmentSet.contains(curEquipment))
					throw new IllegalArgumentException("Equipment already registered.");
		
		VALUES.add(new EquipmentCategoryEntry(sourceEquipmentSet, sourceEquipment[0]));
	}
	
	public static EquipmentCategoryEntry valueOf(Equipment... sourceEquipment)
	{
		for(EquipmentCategoryEntry entry : VALUES)
			if(entry.sourceEquipment.equals(sourceEquipment))
				return entry;
		
		return null;
	}
	
	public static EquipmentCategoryEntry getContaining(Equipment sourceEquipment)
	{
		for(EquipmentCategoryEntry entry : VALUES)
			if(entry.sourceEquipment.contains(sourceEquipment))
				return entry;
		
		return null;
	}

	public FilledHashSet<Equipment> getEquipment()
	{
		return sourceEquipment;
	}

	public Equipment getDefaultEquipment()
	{
		return defaultEquipment;
	}
}
