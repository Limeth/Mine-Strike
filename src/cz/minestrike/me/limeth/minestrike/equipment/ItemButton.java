package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface ItemButton
{
	public ItemStack newItemStack();
	public void onClick(Inventory inv, MSPlayer msPlayer);
}
