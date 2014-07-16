package cz.minestrike.me.limeth.minestrike.listeners;

import java.util.List;

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
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
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
			MSPlayer msPlayer = MSPlayer.get(player);
			Container hotbarContainer = msPlayer.getHotbarContainer();
			
			Grenade.throwGrenade(grenadeType, msPlayer, 1);
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
			Equipment equipment = hotbarContainer.getItem(slot);
			
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
		Container hotbarContainer = msPlayer.getHotbarContainer();
		List<ItemStack> drops = event.getDrops();
		
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		drops.clear();
		msPlayer.respawnDelayed();
		hotbarContainer.clear();
		event.setDeathMessage(null);
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
