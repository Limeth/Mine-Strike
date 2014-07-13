package cz.minestrike.me.limeth.minestrike.equipment;

import org.apache.commons.lang.Validate;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;

public class InventoryContainer extends ScalableContainer
{
	private static final long serialVersionUID = -8525702276639421368L;
	
	public CustomizedEquipment<? extends Equipment> getEquippedCustomizedEquipment(EquipmentCategoryEntry categoryEntry) throws NotFoundException
	{
		Validate.notNull(categoryEntry, "The category entry must not be null!");
		
		for(Equipment equipment : this)
		{
			if(!(equipment instanceof CustomizedEquipment))
				continue;
			
			if(!categoryEntry.getEquipment().contains(equipment.getSource()))
				continue;
			
			@SuppressWarnings("unchecked")
			CustomizedEquipment<? extends Equipment> customEquipment = (CustomizedEquipment<? extends Equipment>) equipment;
			
			if(!customEquipment.isEquipped())
				continue;
			
			return customEquipment;
		}
		
		throw new NotFoundException("CustomizedEquipment for category entry " + categoryEntry + " not found.");
	}
	
	public Equipment getEquippedEquipment(EquipmentCategoryEntry categoryEntry)
	{
		CustomizedEquipment<? extends Equipment> customized;
		
		try
		{
			customized = getEquippedCustomizedEquipment(categoryEntry);
		}
		catch(Exception e)
		{
			return categoryEntry.getDefaultEquipment();
		}
		
		return customized;
	}
}
