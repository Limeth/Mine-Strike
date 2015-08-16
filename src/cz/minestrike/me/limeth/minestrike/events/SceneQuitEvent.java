package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.scene.Scene;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

//TODO Change to SceneEvent<Scene>
public class SceneQuitEvent extends MSPlayerEvent implements Cancellable, SceneEvent<Scene>
{
	private static final HandlerList handlers = new HandlerList();
	private Scene            scene;
	private SceneQuitReason reason;
	private boolean         cancelled;
	
	public SceneQuitEvent(Scene scene, MSPlayer msPlayer, SceneQuitReason reason)
	{
		super(msPlayer);
		
		Validate.notNull(scene, "The scene cannot be null!");
		Validate.notNull(msPlayer, "The player cannot be null!");
		Validate.notNull(reason, "The reason cannot be null!");
		
		this.scene = scene;
		this.reason = reason;
	}
	
	public HandlerList getHandlers()
	{
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
	public Scene getScene()
	{
		return scene;
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
