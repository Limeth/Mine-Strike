package cz.minestrike.me.limeth.minestrike.events;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
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
	private double damage;
	private double penetration;
	private boolean cancelled;

	public PlayerShotEvent(MSPlayer msPlayer, Location locationBulletFinal, double damage, MSPlayer msVictim)
	{
		super(msPlayer);

		Preconditions.checkNotNull(msPlayer, "The player must not be null!");
		Preconditions.checkNotNull(locationBulletFinal, "The final bullet location must not be null!");
		Preconditions.checkNotNull(msVictim, "The shot block must not be null!");

		this.locationBulletFinal = locationBulletFinal;
		this.msVictim = msVictim;
		this.damage = damage;
		this.penetration = 1;
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
	public double getPenetration()
	{
		return penetration;
	}

	@Override
	public void setPenetration(double penetration)
	{
		this.penetration = penetration;
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
