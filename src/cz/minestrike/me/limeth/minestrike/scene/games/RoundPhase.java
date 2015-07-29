package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.base.Preconditions;

/**
 * Created by limeth on 28.7.15.
 */
public class RoundPhase
{
    private final String id;
    private final long duration;

    /**
     * @param id The ID displayed in #toString
     * @param duration The duration in ticks
     */
    public RoundPhase(String id, long duration)
    {
        Preconditions.checkNotNull(id, "The ID must not be null!");

        this.id = id;
        this.duration = duration;
    }

    public long getDuration()
    {
        return duration;
    }

    @Override
    public String toString()
    {
        return "RoundPhase[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoundPhase that = (RoundPhase) o;

        if (duration != that.duration) return false;
        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }
}
