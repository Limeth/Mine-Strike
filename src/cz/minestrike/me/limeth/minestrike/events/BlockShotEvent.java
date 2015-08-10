package cz.minestrike.me.limeth.minestrike.events;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

/**
 * @author Limeth
 */
public class BlockShotEvent extends MSPlayerEvent implements ShotEvent
{
	private static final HandlerList handlers = new HandlerList();
	private final Block block;
	private final Location locationBulletFinal;
	private double damage;
	private double penetration;
	private boolean cancelled;
	private boolean penetrated;

	public BlockShotEvent(MSPlayer msPlayer, Location locationBulletFinal, double damage, Block block)
	{
		super(msPlayer);

		Preconditions.checkNotNull(msPlayer, "The player must not be null!");
		Preconditions.checkNotNull(locationBulletFinal, "The final bullet location must not be null!");
		Preconditions.checkNotNull(block, "The shot block must not be null!");

		this.locationBulletFinal = locationBulletFinal;
		this.block = block;
		this.damage = damage;
		this.penetration = 1;
		this.penetrated = false;
		this.cancelled = false;
	}

	@Override
	public Location getLocationBulletFinal()
	{
		return locationBulletFinal;
	}

	public Block getBlock()
	{
		return block;
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
	public double penetrate(double penetrationModifier)
	{
		if(!penetrated)
			penetrated = true;

		return penetration *= penetrationModifier;
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
