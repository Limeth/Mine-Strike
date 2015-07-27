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

public class PreparationCheckRunnable extends MSSceneListener<Game> implements Runnable
{
    private static final double MAX_DISTANCE = 0.25;
    private final HashMap<MSPlayer, Location> origin = Maps.newHashMap();
    private BooleanSupplier isBeingPrepared;
    private Runnable onEnd;
    private Integer preparationCheckTaskId;

    public PreparationCheckRunnable(Game game, BooleanSupplier isBeingPrepared, Runnable onEnd)
    {
        super(game);

        Preconditions.checkNotNull(isBeingPrepared, "The isBeingPrepared BooleanSupplier must not be null!");
        Preconditions.checkNotNull(onEnd, "The onEnd Runnable must not be null!");

        this.isBeingPrepared = isBeingPrepared;
        this.onEnd = onEnd;
    }

    public int start(long frequency)
    {
        return preparationCheckTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, 0L, frequency);
    }

    public void setIsBeingPrepared(BooleanSupplier isBeingPrepared)
    {
        Preconditions.checkNotNull(isBeingPrepared, "The isBeingPrepared BooleanSupplier must not be null!");

        this.isBeingPrepared = isBeingPrepared;
    }

    public BooleanSupplier getIsBeingPrepared()
    {
        return isBeingPrepared;
    }

    public void setOnEnd(Runnable onEnd)
    {
        Preconditions.checkNotNull(onEnd, "The onEnd Runnable must not be null!");

        this.onEnd = onEnd;
    }

    public Runnable getOnEnd()
    {
        return onEnd;
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
            else if(origin.distanceSquared(loc) > MAX_DISTANCE * MAX_DISTANCE)
            {
                origin.setYaw(loc.getYaw());
                origin.setPitch(loc.getPitch());

                msPlayer.teleport(origin, false);
            }
        }

        if(!isBeingPrepared.getAsBoolean() && preparationCheckTaskId != null)
        {
            BukkitScheduler scheduler = Bukkit.getScheduler();

            scheduler.cancelTask(preparationCheckTaskId);

            preparationCheckTaskId = null;

            onEnd.run();
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
