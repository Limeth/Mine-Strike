package cz.minestrike.me.limeth.minestrike.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Container;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentType;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.Grenade;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public class InteractionListener implements Listener
{
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		ItemStack item = event.getItem();
		Material type = item == null ? null : item.getType();
		
		if(type == Material.POTION)
		{
			short durability = item.getDurability();
			GrenadeType grenadeType = null;
			
			for(GrenadeType curType : GrenadeType.values())
				if(curType.getColor() == durability)
				{
					grenadeType = curType;
					break;
				}
			
			if(type == null)
				return;
			
			Action action = event.getAction();
			
			event.setCancelled(true);
			
			if(action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR)
				return;
			
			Player player = event.getPlayer();
			PlayerInventory inv = player.getInventory();
			int slot = inv.getHeldItemSlot();
			MSPlayer msPlayer = MSPlayer.get(event.getPlayer());
			Container hotbarContainer = msPlayer.getHotbarContainer();
			
			Grenade.throwGrenade(grenadeType, player, 1);
			player.setItemInHand(null);
			hotbarContainer.setItem(slot, null);
			return;
		}
		
		Action action = event.getAction();
		
		if(action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)
		{
			Player player = event.getPlayer();
			PlayerInventory inv = player.getInventory();
			int slot = inv.getHeldItemSlot();
			MSPlayer msPlayer = MSPlayer.get(event.getPlayer());
			Container hotbarContainer = msPlayer.getHotbarContainer();
			Equipment<? extends EquipmentType> equipment = hotbarContainer.getItem(slot);
			
			if(equipment != null && equipment instanceof Gun)
			{
				Gun gun = (Gun) equipment;
				
				msPlayer.pressTrigger(gun);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		MSPlayer msPlayer = MSPlayer.get(player);
		
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		event.getDrops().clear();
		msPlayer.respawnDelayed();
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Location loc = msPlayer.spawn(false);
		
		if(loc == null)
		{
			MineStrike.warn("Cannot spawn at null location!");
			return;
		}
		
		event.setRespawnLocation(loc);
	}
}
