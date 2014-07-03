package cz.minestrike.me.limeth.minestrike.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PermissionListener implements Listener
{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		
		if(!(player.hasPermission("MineStrike.build") && player.getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		
		if(!(player.hasPermission("MineStrike.build") && player.getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		
		player.setFoodLevel(5);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		player.setFoodLevel(5);
	}
}
