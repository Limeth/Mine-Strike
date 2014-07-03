package cz.minestrike.me.limeth.minestrike.listeners;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.games.Game;

public class ConnectionListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		MSPlayer.get(playerName, true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		
		if(msPlayer.hasGame())
		{
			Game<?, ?, ?, ?> game = msPlayer.getGame();
			
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
