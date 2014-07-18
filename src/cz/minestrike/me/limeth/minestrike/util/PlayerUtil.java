package cz.minestrike.me.limeth.minestrike.util;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class PlayerUtil
{
	public static final int INVENTORY_WIDTH = 9;
	
	public static int getBottomInventoryIndex(InventoryView view, int rawSlot)
	{
		Inventory topInv = view.getTopInventory();
		
		if(topInv instanceof CraftInventoryCrafting)
			rawSlot -= INVENTORY_WIDTH;
		else
			rawSlot -= topInv.getSize();
		
		return rawSlot;
	}
	
	public static int getInventoryX(int index)
	{
		return index % INVENTORY_WIDTH;
	}
	
	public static int getInventoryY(int index)
	{
		return index / INVENTORY_WIDTH;
	}
	
	public static void setItem(Inventory inv, int x, int y, ItemStack item)
	{
		inv.setItem(x + y * INVENTORY_WIDTH, item);
	}
	
	public static void setItem(PlayerInventory inv, int x, int y, ItemStack item)
	{
		int index = toPlayerInventoryIndex(x, y);
		
		inv.setItem(index, item);
	}
	
	public static ItemStack getItem(PlayerInventory inv, int x, int y)
	{
		int index = toPlayerInventoryIndex(x, y);
		
		return inv.getItem(index);
	}
	
	public static int toPlayerInventoryIndex(int x, int y)
	{
		return INVENTORY_WIDTH + x + y * INVENTORY_WIDTH;
	}
	
	public static void delayedInventoryUpdate(final Player player, Plugin plugin)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				player.updateInventory();
			}
		});
	}
}
