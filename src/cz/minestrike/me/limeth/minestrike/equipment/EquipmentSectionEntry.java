package cz.minestrike.me.limeth.minestrike.equipment;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;
import org.apache.commons.lang.Validate;

import java.util.Arrays;
import java.util.HashSet;

public class EquipmentSectionEntry
{
	static
	{
		VALUES = new HashSet<EquipmentSectionEntry>();
		
		register(GunType.P2000, GunType.USP_S);
		register(GunType.P250, GunType.CZ75);
		register(GunType.M4A4, GunType.M4A1_S);
		
		for(Equipment equipment : EquipmentManager.getEquipment())
			try
			{
				register(equipment);
			}
			catch(Exception e) {}
	}

	private static final HashSet<EquipmentSectionEntry> VALUES;
	private final        FilledHashSet<Equipment>       sourceEquipment;
	private final        Equipment                      defaultEquipment;

	private EquipmentSectionEntry(FilledHashSet<Equipment> sourceEquipment, Equipment defaultEquipment)
	{
		this.sourceEquipment = sourceEquipment;
		this.defaultEquipment = defaultEquipment;
	}

	private static void register(Equipment... sourceEquipment)
	{
		Validate.isTrue(sourceEquipment.length > 0, "Source equipment length must be larger than 0!");

		FilledHashSet<Equipment> sourceEquipmentSet = new FilledHashSet<Equipment>(Arrays.asList(sourceEquipment));
		;

		for(EquipmentSectionEntry entry : VALUES)
			for(Equipment curEquipment : entry.sourceEquipment)
				if(sourceEquipmentSet.contains(curEquipment))
					throw new IllegalArgumentException("Equipment already registered.");

		VALUES.add(new EquipmentSectionEntry(sourceEquipmentSet, sourceEquipment[0]));
	}
	
	public static EquipmentSectionEntry valueOf(Equipment... sourceEquipment)
	{
		FilledHashSet<Equipment> sourceEquipmentSet = new FilledHashSet<Equipment>(Arrays.asList(sourceEquipment));;
		
		for(EquipmentSectionEntry entry : VALUES)
			if(entry.sourceEquipment.equals(sourceEquipmentSet))
				return entry;
		
		throw new NotFoundException("Category entry for " + Arrays.toString(sourceEquipment) + " not found.");
	}
	
	public static EquipmentSectionEntry getContaining(Equipment sourceEquipment)
	{
		for(EquipmentSectionEntry entry : VALUES)
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

	@Override
	public String toString()
	{
		return "EquipmentCategoryEntry [sourceEquipment=" + sourceEquipment + ", defaultEquipment=" + defaultEquipment + "]";
	}
}
