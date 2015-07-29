package cz.minestrike.me.limeth.minestrike.scene.games.team;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

public abstract class RespawnMovementRunnable<T extends Game> extends MSSceneListener<T> implements Runnable
{
    private static final double MAX_DISTANCE = 0.25;
    private final HashMap<MSPlayer, Location> origin = Maps.newHashMap();
    private Integer preparationCheckTaskId;
    private double sensitivity;
    private long frequencyTicks;

    public RespawnMovementRunnable(T game, double sensitivity, long frequencyTicks)
    {
        super(game);

        Preconditions.checkArgument(frequencyTicks > 0, "The frequency must be larger than 0!");

        this.sensitivity = sensitivity;
        this.frequencyTicks = frequencyTicks;
    }

    public abstract void moved(MSPlayer msPlayer, Location origin);

    public void reset()
    {
        origin.clear();
    }

    public void reset(MSPlayer msPlayer)
    {
        origin.remove(msPlayer);
    }

    public int start()
    {
        if(isRunning())
            throw new IllegalStateException("Already running!");

        return preparationCheckTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, 0L, frequencyTicks);
    }

    public void stop()
    {
        if(!isRunning())
            throw new IllegalStateException("Not running!");

        BukkitScheduler scheduler = Bukkit.getScheduler();

        scheduler.cancelTask(preparationCheckTaskId);

        preparationCheckTaskId = null;
    }

    public boolean isRunning()
    {
        return preparationCheckTaskId != null;
    }

    public double getSensitivity()
    {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity)
    {
        this.sensitivity = sensitivity;
    }

    public long getFrequencyTicks()
    {
        return frequencyTicks;
    }

    public void setFrequencyTicks(long frequencyTicks)
    {
        this.frequencyTicks = frequencyTicks;
    }

    @Override
    public void run()
    {
        Game game = getScene();

        for(MSPlayer msPlayer : game.getPlayingPlayers(game::isAlive))
        {
            Player player = msPlayer.getPlayer();
            Location origin = this.origin.get(msPlayer);
            Location loc = player.getLocation();

            if(origin == null)
                this.origin.put(msPlayer, loc);
            else if(origin.distanceSquared(loc) > sensitivity * sensitivity)
                moved(msPlayer, loc);
        }
    }

    @EventHandler
    public void onArenaJoin(ArenaJoinEvent event, final MSPlayer msPlayer)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), () -> {
            Player player = msPlayer.getPlayer();
            Location loc = player.getLocation();

            origin.put(msPlayer, loc);
        });
    }

    @EventHandler
    public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
    {
        origin.remove(msPlayer);
    }
}
