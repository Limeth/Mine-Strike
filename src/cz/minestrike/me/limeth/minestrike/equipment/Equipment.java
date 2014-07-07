package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class Equipment<T extends EquipmentType>
{
	private T type;
	private final EquipmentCustomization customization;
	
	public Equipment(T type, EquipmentCustomization customization)
	{
		this.type = type;
		this.customization = customization;
	}
	
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = type.newItemStack(msPlayer);
		
		customization.apply(type, is);
		
		return is;
	}

	public T getType()
	{
		return type;
	}

	public EquipmentCustomization getCustomization()
	{
		return customization;
	}

	@Override
	public String toString()
	{
		return "Equipment [type=" + type + ", customization=" + customization + "]";
	}
}
