package cz.minestrike.me.limeth.minestrike.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class PlayerUtil
{
	public static final int INVENTORY_WIDTH = 9;
	
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
