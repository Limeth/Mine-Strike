package cz.minestrike.me.limeth.minestrike.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public class GameSpawnEvent extends MSPlayerEvent implements Cancellable, GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game<?, ?, ?, ?> game;
	private boolean cancelled;
	private boolean teleport;
	
	public GameSpawnEvent(Game<?, ?, ?, ?> game, MSPlayer who, boolean teleport)
	{
		super(who);
		
		Validate.notNull(game, "The game cannot be null!");
		
		this.game = game;
		this.teleport = teleport;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public Game<?, ?, ?, ?> getGame()
	{
		return game;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	public boolean isTeleport()
	{
		return teleport;
	}

	public void setTeleport(boolean teleport)
	{
		this.teleport = teleport;
	}
}
