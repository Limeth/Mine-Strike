package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager.EquipmentDeserializer;

public interface EquipmentType
{
	public String getId();
	public EquipmentDeserializer getDeserializer();
	public ItemStack newItemStack(MSPlayer msPlayer);
	public String getDisplayName();
	public int getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
}
