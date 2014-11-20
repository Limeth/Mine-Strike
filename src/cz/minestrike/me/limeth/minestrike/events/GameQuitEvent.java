package cz.minestrike.me.limeth.minestrike.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public class GameQuitEvent extends MSPlayerEvent implements Cancellable, GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private SceneQuitReason reason;
	private boolean cancelled;
	
	public GameQuitEvent(Game game, MSPlayer msPlayer, SceneQuitReason reason)
	{
		super(msPlayer);
		
		Validate.notNull(game, "The game cannot be null!");
		Validate.notNull(msPlayer, "The player cannot be null!");
		Validate.notNull(reason, "The reason cannot be null!");
		
		this.game = game;
		this.reason = reason;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
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

	@Override
	public Game getGame()
	{
		return game;
	}
	
	public SceneQuitReason getReason()
	{
		return reason;
	}

	public void setReason(SceneQuitReason reason)
	{
		this.reason = reason;
	}

	public static enum SceneQuitReason
	{
		LEAVE, KICK, LOG_OUT, ERROR_INVALID_PLAYER_STATE;
	}
}
