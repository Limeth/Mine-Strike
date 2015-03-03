package cz.minestrike.me.limeth.minestrike.listeners;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class InteractionListener implements Listener
{
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		
		if(action != Action.PHYSICAL)
		{
			Player player = event.getPlayer();
			PlayerInventory inv = player.getInventory();
			int slot = inv.getHeldItemSlot();
			MSPlayer msPlayer = MSPlayer.get(event.getPlayer());
			Container hotbarContainer = msPlayer.getHotbarContainer();
			Equipment equipment = hotbarContainer.getItem(slot);
			Block clickedBlock = event.getClickedBlock();
			boolean cancelEvent = false;
			
			if(equipment != null)
				if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
					cancelEvent = equipment.rightClick(msPlayer, clickedBlock);
				else
					cancelEvent = equipment.leftClick(msPlayer, clickedBlock);
			
			event.setCancelled(cancelEvent);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		MSPlayer msPlayer = MSPlayer.get(player);
		List<ItemStack> drops = event.getDrops();
		
		event.setKeepLevel(true);
		event.setDroppedExp(0);
		drops.clear();
		msPlayer.respawnDelayed();
		event.setDeathMessage(null);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		final Player player = event.getPlayer();
		final MSPlayer msPlayer = MSPlayer.get(player);
		Location loc = msPlayer.spawn(false);
		
		if(loc == null)
		{
			MineStrike.warn("Cannot spawn at null location!");
			return;
		}

		event.setRespawnLocation(loc);
		BukkitScheduler scheduler = Bukkit.getScheduler();
		Runnable runnable = () -> {
			player.setFoodLevel(5);
			msPlayer.updateMovementSpeed();
			msPlayer.clearPotionEffects();
		};

		scheduler.scheduleSyncDelayedTask(MineStrike.getInstance(), runnable);
	}
}
