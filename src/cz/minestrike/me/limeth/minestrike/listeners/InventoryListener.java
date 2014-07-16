package cz.minestrike.me.limeth.minestrike.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;

public class InventoryListener implements Listener
{
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Container hotbarContainer = msPlayer.getHotbarContainer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		Equipment equipment = hotbarContainer.getItem(slot);
		
		if(equipment == null || !(equipment instanceof Gun))
			return;
		
		Gun gun = (Gun) equipment;
		final Item item = event.getItemDrop();
		ItemStack is = item.getItemStack();
		
		item.remove();
		inv.setItem(slot, is);
		
		if(gun != null && gun.canBeReloaded())
			msPlayer.reload(gun);
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Container hotbarContainer = msPlayer.getHotbarContainer();
		int newIndex = event.getNewSlot();
		Equipment newEquipment = hotbarContainer.getItem(newIndex);
		
		if(msPlayer.hasGunTask())
			msPlayer.getGunTask().cancel();
		
		if(newEquipment != null)
		{
			Location loc = player.getEyeLocation();
			String sound = newEquipment.getSoundDraw();
			
			SoundManager.play(sound, loc, player);
		}
		
		msPlayer.updateMovementSpeed();
	}
}
