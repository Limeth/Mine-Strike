package cz.minestrike.me.limeth.minestrike.util;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public abstract class BukkitRepeater
{
    private Integer taskId;
    private double tickRemainder;
    private long totalTicksScheduled;
    private long tickIndex;

    public abstract void start();
    public abstract void onInterrupt();
    public abstract Optional<Double> onTick();

    protected final void launch(Double ticks)
    {
        if(isRunning())
            throw new IllegalStateException("BukkitRepeater already running.");

        totalTicksScheduled = 0;
        tickIndex = 0;

        if(ticks != null)
            schedule(ticks);
        else
            tick();
    }

    protected final void launch()
    {
        launch(null);
    }

    public final void interrupt()
    {
        if(taskId == null)
            throw new IllegalStateException("BukkitRepeater not running.");

        try
        {
            onInterrupt();
        }
        finally
        {
            Bukkit.getScheduler().cancelTask(taskId);

            taskId = null;
        }
    }

    private void tick()
    {
        Optional<Double> delay = onTick();
        tickIndex++;

        if(delay.isPresent())
            schedule(delay.get());
        else
            taskId = null;
    }

    protected int schedule(double ticks)
    {
        ticks += tickRemainder;
        int roundedTicks = (int) ticks;
        tickRemainder = ticks - roundedTicks;
        totalTicksScheduled += roundedTicks;

        return taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::tick, roundedTicks);
    }

    public final boolean isRunning()
    {
        return taskId != null;
    }

    public final long getTotalTicksScheduled()
    {
        return totalTicksScheduled;
    }

    public final long getTickIndex()
    {
        return tickIndex;
    }
}
