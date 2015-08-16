package cz.minestrike.me.limeth.minestrike.events;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Optional;

public class ScenePostSpawnEvent extends MSPlayerEvent implements SceneEvent<Scene>
{
	private static final HandlerList handlers = new HandlerList();
	private final Scene   scene;
	private final boolean teleport;
	private final Location location;

	public ScenePostSpawnEvent(Scene scene, MSPlayer who, boolean teleport, Location location)
	{
		super(who);
		
		Preconditions.checkNotNull(scene, "The scene cannot be null!");

		this.scene = scene;
		this.teleport = teleport;
		this.location = location;
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

	public Optional<Location> getLocation()
	{
		return Optional.ofNullable(location);
	}

	public boolean isTeleport()
	{
		return teleport;
	}
}
