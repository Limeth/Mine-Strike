package cz.minestrike.me.limeth.minestrike.events;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ScenePreSpawnEvent extends MSPlayerEvent implements Cancellable, SceneEvent<Scene>
{
	private static final HandlerList handlers = new HandlerList();
	private final Scene    scene;
	private       boolean  cancelled;
	private       boolean  teleport;
	private       Location location;

	public ScenePreSpawnEvent(Scene scene, MSPlayer who, boolean teleport)
	{
		super(who);
		
		Validate.notNull(scene, "The scene cannot be null!");
		
		this.scene = scene;
		this.teleport = teleport;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public Scene getScene()
	{
		return scene;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		Preconditions.checkNotNull(location, "The location must be set!");

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

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		Preconditions.checkNotNull(location);

		this.location = location;
	}
}
