package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class SceneJoinEvent extends MSPlayerEvent implements Cancellable, SceneEvent<Scene>
{
    private static final HandlerList handlers = new HandlerList();
    private Scene   scene;
    private boolean cancelled;

    public SceneJoinEvent(Scene scene, MSPlayer msPlayer)
    {
        super(msPlayer);

        Validate.notNull(scene, "The scene cannot be null!");
        Validate.notNull(msPlayer, "The player cannot be null!");

        this.scene = scene;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public HandlerList getHandlers()
    {
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
}
