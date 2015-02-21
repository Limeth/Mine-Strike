package cz.minestrike.me.limeth.minestrike.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
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
		MSPlayer msPlayer = MSPlayer.get(player, true);
		Scene scene = msPlayer.getScene();
		
		scene.onJoin(msPlayer);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		
		msPlayer.quitScene(SceneQuitReason.LOG_OUT, false, false);
		msPlayer.save(false);
		MSPlayer.remove(player);
	}
}
