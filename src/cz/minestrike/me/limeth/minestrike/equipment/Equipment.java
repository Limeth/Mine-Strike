package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface Equipment
{
	public String getId();
	public Class<? extends Equipment> getEquipmentClass();
	public Equipment getSource();
	public ItemStack newItemStack(MSPlayer msPlayer);
	public String getDisplayName();
	public Integer getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
}
