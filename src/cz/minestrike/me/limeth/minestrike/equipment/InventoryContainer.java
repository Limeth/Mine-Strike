package cz.minestrike.me.limeth.minestrike.equipment;

import org.apache.commons.lang.Validate;

public class InventoryContainer extends ScalableContainer
{
	private static final long serialVersionUID = -8525702276639421368L;
	
	public CustomizedEquipment<? extends Equipment> getEquippedCustomizedEquipment(EquipmentCategoryEntry categoryEntry)
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
		
		return null;
	}
	
	public Equipment getEquippedEquipment(EquipmentCategoryEntry categoryEntry)
	{
		CustomizedEquipment<? extends Equipment> customized = getEquippedCustomizedEquipment(categoryEntry);
		
		return customized != null ? customized : categoryEntry.getDefaultEquipment();
	}
}
