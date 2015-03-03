package cz.minestrike.me.limeth.minestrike.listeners;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
		
		if(equipment == null)
			return;
		
		final Item item = event.getItemDrop();
		ItemStack is = item.getItemStack();
		
		item.remove();
		inv.setItem(slot, is);
		equipment.dropButtonPress(msPlayer);
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Container hotbarContainer = msPlayer.getHotbarContainer();
		int newIndex = event.getNewSlot();
		int oldIndex = event.getPreviousSlot();
		Equipment newEquipment = hotbarContainer.getItem(newIndex);
		Equipment oldEquipment = hotbarContainer.getItem(oldIndex);
		
		if(oldEquipment != null)
		{
			oldEquipment.onDeselect(msPlayer);
		}

		msPlayer.setHeldItemSlot(newIndex);
		
		if(newEquipment != null)
		{
			Location loc = player.getEyeLocation();
			String sound = newEquipment.getSoundDrawing();
			
			newEquipment.onSelect(msPlayer);
			SoundManager.play(sound, loc, player);
		}
		
		msPlayer.updateMovementSpeed();
	}
}
