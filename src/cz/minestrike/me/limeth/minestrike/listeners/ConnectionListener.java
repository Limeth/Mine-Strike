package cz.minestrike.me.limeth.minestrike.listeners;

import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public class ConnectionListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();
		Location spawn = MSConfig.getSpawnLocation();
		MSPlayer msPlayer = MSPlayer.get(playerName, true);
		msPlayer.teleport(spawn);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Scene scene = msPlayer.getScene();
		
		if(scene instanceof Game)
		{
			Game<?, ?, ?, ?> game = (Game<?, ?, ?, ?>) scene;
			
			game.quit(msPlayer, GameQuitReason.LOG_OUT, false);
		}
		
		try
		{
			msPlayer.save();
		}
		catch(SQLException e)
		{
			MineStrike.warn("An error occured while saving player data for player '" + player.getName() + "'.");
			e.printStackTrace();
		}
		
		MSPlayer.remove(msPlayer);
	}
}
