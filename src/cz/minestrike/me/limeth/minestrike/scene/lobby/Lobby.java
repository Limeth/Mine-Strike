package cz.minestrike.me.limeth.minestrike.scene.lobby;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.Scene;

public class Lobby extends Scene
{
	private static final Lobby INSTANCE = new Lobby();
	
	private Lobby() {}
	
	public static Lobby getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
	}
	
	@Override
	public Location spawn(MSPlayer msPlayer, boolean teleport)
	{
		Location loc = MSConfig.getWorld().getSpawnLocation();
		
		if(teleport)
			msPlayer.teleport(loc);
		
		return loc;
	}
	
	@Override
	public void broadcast(String message)
	{
		for(MSPlayer msPlayer : getPlayers())
			msPlayer.sendMessage(message);
	}
	
	@Override
	public Set<MSPlayer> getPlayers()
	{
		return MSPlayer.getOnlinePlayers(p -> { return p.getScene() instanceof Lobby; });
	}
	
	@Override
	public Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> predicate)
	{
		return MSPlayer.getOnlinePlayers(p -> { return p.getScene() instanceof Lobby && predicate.test(p); });
	}
	
	@Override
	public Set<Player> getBukkitPlayers()
	{
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers())
			players.add(player.getPlayer());
		
		return players;
	}
	
	@Override
	public Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> predicate)
	{
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers(predicate))
			players.add(player.getPlayer());
		
		return players;
	}
}
