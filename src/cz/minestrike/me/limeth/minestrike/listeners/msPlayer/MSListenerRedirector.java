package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface MSListenerRedirector
{
	public void redirect(Event event, MSPlayer msPlayer);
}
