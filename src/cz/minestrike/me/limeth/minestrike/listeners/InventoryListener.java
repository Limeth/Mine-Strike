package cz.minestrike.me.limeth.minestrike.listeners;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public class InventoryListener implements Listener
{
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		final Item item = event.getItemDrop();
		ItemStack is = item.getItemStack();
		Gun gun = Gun.tryParse(is);
		
		item.remove();
		inv.setItem(slot, is);
		
		if(gun != null)
		{
			if(gun.canBeReloaded())
			{
				MSPlayer msPlayer = MSPlayer.get(player);
				
				msPlayer.reload(gun);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		
		if(msPlayer.hasGunTask())
			msPlayer.getGunTask().cancel();
	}
}
