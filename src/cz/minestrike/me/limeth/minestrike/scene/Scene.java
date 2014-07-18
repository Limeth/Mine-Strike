package cz.minestrike.me.limeth.minestrike.scene;

import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;

public abstract class Scene implements MSListenerRedirector
{
	public abstract Scene setup();
	public abstract Location spawn(MSPlayer msPlayer, boolean teleport);
	public abstract void equip(MSPlayer msPlayer, boolean force);
	public abstract void broadcast(String message);
	public abstract Set<MSPlayer> getPlayers();
	public abstract Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> condition);
	public abstract Set<Player> getBukkitPlayers();
	public abstract Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> condition);
	public abstract boolean onJoin(MSPlayer msPlayer);
	public abstract boolean onQuit(MSPlayer msPlayer, SceneQuitReason reason, boolean teleport);
	
	public String getPrefix(MSPlayer msPlayer)
	{
		return null;
	}
	
	public String getSuffix(MSPlayer msPlayer)
	{
		return null;
	}
}
