package cz.minestrike.me.limeth.minestrike.listeners;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;

public class ConnectionListener implements Listener
{
	private static final ConnectionListener instance = new ConnectionListener();
	
	private ConnectionListener() {}
	
	public static ConnectionListener getInstance()
	{
		return instance;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();
		MSPlayer msPlayer = MSPlayer.get(playerName, true);
		Scene scene = msPlayer.getScene();
		
		msPlayer.updateNameTag();
		scene.onJoin(msPlayer);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		
		msPlayer.quitScene(SceneQuitReason.LOG_OUT, false, false);
		
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
