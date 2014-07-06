package cz.minestrike.me.limeth.minestrike.equipment.guns;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;

public class Firing extends GunTask
{
	private static final long MOUSE_CLICK_DELAY = 250;
	private long lastTimeFired;
	private Integer loopId;
	private float remainder;
	
	public Firing(@Nonnull MSPlayer msPlayer, int slotId, @Nonnull GunType gunType, long lastTimeFired)
	{
		super(msPlayer, slotId, gunType);
		this.lastTimeFired = lastTimeFired;
	}
	
	public Firing(@Nonnull MSPlayer msPlayer, int slotId, @Nonnull GunType gunType)
	{
		this(msPlayer, slotId, gunType, System.currentTimeMillis());
	}
	
	@Override
	public Firing startLoop()
	{
		nextLoop();
		return this;
	}
	
	private int nextLoop()
	{
		float cycleTime = getGunType().getCycleTime();
		int rounded = (int) (cycleTime + remainder);
		
		//Bukkit.broadcastMessage(cycleTime + " = " + rounded + " + " + remainder);
		
		remainder += cycleTime - rounded;
		final Firing firing = this;
		
		loopId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				if(firing.getLoopId() != null)
					firing.nextLoop();
			}
		}, rounded);

		run();
		
		return loopId;
	}
	
	@Override
	protected boolean execute()
	{
		if(!hasExpired())
		{
			Gun gun = parseGunIfSelected();
			
			if(gun != null)
				if(decreaseBullets(gun))
				{
					getMSPlayer().shoot(getGunType());
					return true;
				}
		}
		
		return false;
	}
	
	private boolean decreaseBullets(Gun gun)
	{
		Player player = getMSPlayer().getPlayer();
		
		if(!gun.isLoaded())
			return false;
		
		gun.decreaseLoadedBullets();
		
		ItemStack itemStack = gun.newItemStack(msPlayer);//TODO try editing instead of creating a new one
		
		player.setItemInHand(itemStack);
		
		return true;
	}
	
	public boolean hasExpired()
	{
		return lastTimeFired < System.currentTimeMillis() - MOUSE_CLICK_DELAY;
	}

	public long getLastTimeFired()
	{
		return lastTimeFired;
	}

	public void setLastTimeFired(long lastTimeFired)
	{
		this.lastTimeFired = lastTimeFired;
	}

	@Override
	public Integer getLoopId()
	{
		return loopId;
	}
}
