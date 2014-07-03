package cz.minestrike.me.limeth.minestrike.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class MSPlayerEvent extends PlayerEvent
{
	private static final HandlerList handlers = new HandlerList();
	private final MSPlayer msPlayer;
	
	public MSPlayerEvent(MSPlayer who)
	{
		super(who.getPlayer());
		
		this.msPlayer = who;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public MSPlayer getMSPlayer()
	{
		return msPlayer;
	}
}
