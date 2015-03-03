package cz.minestrike.me.limeth.minestrike.equipment;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import com.google.common.collect.Sets;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.CZ75;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.P2000;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.P250;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.USPS;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.M4A1S;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.M4A4;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;
import org.apache.commons.lang.Validate;

import java.util.Arrays;
import java.util.HashSet;

public class EquipmentSectionEntry
{
	static
	{
		VALUES = Sets.newHashSet();
		
		register(P2000.getInstance(), USPS.getInstance());
		register(P250.getInstance(), CZ75.getInstance());
		register(M4A4.getInstance(), M4A1S.getInstance());
		
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

		FilledHashSet<Equipment> sourceEquipmentSet = new FilledHashSet<>(Arrays.asList(sourceEquipment));

		for(EquipmentSectionEntry entry : VALUES)
			for(Equipment curEquipment : entry.sourceEquipment)
				if(sourceEquipmentSet.contains(curEquipment))
					throw new IllegalArgumentException("Equipment already registered.");

		VALUES.add(new EquipmentSectionEntry(sourceEquipmentSet, sourceEquipment[0]));
	}
	
	public static EquipmentSectionEntry valueOf(Equipment... sourceEquipment)
	{
		FilledHashSet<Equipment> sourceEquipmentSet = new FilledHashSet<>(Arrays.asList(sourceEquipment));;
		
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
		return "EquipmentSectionEntry [sourceEquipment=" + sourceEquipment + ", defaultEquipment=" + defaultEquipment + "]";
	}
}
