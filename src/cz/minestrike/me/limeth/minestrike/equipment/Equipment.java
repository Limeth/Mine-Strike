package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface Equipment
{
	public ItemStack newItemStack(MSPlayer msPlayer, EquipmentCustomization customization);
	public String getDisplayName();
	public int getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
}
