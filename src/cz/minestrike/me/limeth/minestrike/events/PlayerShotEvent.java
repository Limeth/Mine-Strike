package cz.minestrike.me.limeth.minestrike.events;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.BodyPart;
import cz.minestrike.me.limeth.minestrike.DamageRecord;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @author Limeth
 */
public class PlayerShotEvent extends MSPlayerEvent implements ShotEvent
{
	private static final HandlerList handlers = new HandlerList();
	private final MSPlayer msVictim;
	private final Location locationBulletFinal;
	private final double absolutePenetration;
	private double damage;
	private double relativePenetration;
	private boolean penetrated;
	private boolean cancelled;

	public PlayerShotEvent(MSPlayer msPlayer, Location locationBulletFinal, double damage, double absolutePenetration, boolean penetrated, MSPlayer msVictim)
	{
		super(msPlayer);

		Preconditions.checkNotNull(msPlayer, "The player must not be null!");
		Preconditions.checkNotNull(locationBulletFinal, "The final bullet location must not be null!");
		Preconditions.checkNotNull(msVictim, "The shot block must not be null!");
        Preconditions.checkNotNull(absolutePenetration >= 0, "The absolute penetration must not be negative!");

		this.locationBulletFinal = locationBulletFinal;
		this.msVictim = msVictim;
		this.damage = damage;
		this.relativePenetration = 1;
		this.absolutePenetration = absolutePenetration;
		this.penetrated = penetrated;
		this.cancelled = false;
	}

    public DamageRecord getDamageRecord()
    {
        MSPlayer msDamager = getMSPlayer();
        Location hitLoc = getLocationBulletFinal();
        MSPlayer msVictim = getMSVictim();
        Player victim = msVictim.getPlayer();
        Location victimLoc = victim.getLocation();
        double hitY = hitLoc.getY();
        double victimY = victimLoc.getY();
        double relHitY = hitY - victimY;
        BodyPart bodyPart = BodyPart.getByY(relHitY);
        double damage = getDamage();
        Gun gun = getGun();

        return new DamageRecord(msDamager, gun, bodyPart, penetrated, damage);
    }

	@Override
	public Location getLocationBulletFinal()
	{
		return locationBulletFinal;
	}

	public MSPlayer getMSVictim()
	{
		return msVictim;
	}

	public Player getVictim()
	{
		return msVictim.getPlayer();
	}

	@Override
	public double getDamage()
	{
		return damage;
	}

	@Override
	public void setDamage(double damage)
	{
		this.damage = damage;
	}

	@Override
	public double getRelativePenetration()
	{
		return relativePenetration;
	}

	@Override
	public double getAbsolutePenetration()
	{
		return relativePenetration * absolutePenetration;
	}

	@Override
	public double penetrate(double penetrationModifier)
	{
        if(!penetrated)
            penetrated = true;

		return this.relativePenetration *= penetrationModifier;
	}

    @Override
    public boolean isPenetrated()
    {
        return penetrated;
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
}
