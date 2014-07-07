package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface EquipmentType
{
	public String getId();
	@SuppressWarnings("rawtypes")
	public Class<? extends Equipment> getEquipmentClass();
	public ItemStack newItemStack(MSPlayer msPlayer);
	public String getDisplayName();
	public int getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
}
