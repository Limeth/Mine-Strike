package cz.minestrike.me.limeth.minestrike.equipment.guns.tasks;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class Firing extends GunTask
{
	private static final long MOUSE_CLICK_DELAY = 250;
	private long lastTimeFired;
	private Integer loopId;
	private float remainder;
	
	public Firing(@Nonnull MSPlayer msPlayer, @Nonnull Gun gun, long lastTimeFired)
	{
		super(msPlayer, gun);
		this.lastTimeFired = lastTimeFired;
	}
	
	public Firing(@Nonnull MSPlayer msPlayer, @Nonnull Gun gun)
	{
		this(msPlayer, gun, System.currentTimeMillis());
	}
	
	@Override
	public Firing startLoop()
	{
		nextLoop();
		return this;
	}
	
	private int nextLoop()
	{
		MSPlayer msPlayer = getMSPlayer();
		Gun gun = getGun();
		GunType gunType = gun.getEquipment();
		float cycleTime = gunType.getCycleTime(msPlayer);
		int rounded = (int) (cycleTime + remainder);
		
		//Bukkit.broadcastMessage(cycleTime + " = " + rounded + " + " + remainder);
		
		remainder += cycleTime - rounded;
		final Firing firing = this;
		
		loopId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), () -> {
            if(firing.getLoopId() != null)
                firing.nextLoop();
        }, rounded);

		run();
		
		return loopId;
	}
	
	@Override
	protected boolean execute()
	{
		if(!hasExpired())
		{
			Gun gun = getGun();
			MSPlayer msPlayer = getMSPlayer();
			
			if(decreaseBullets())
			{
				GunType gunType = gun.getEquipment();

				gunType.shoot(msPlayer);
				gun.reloadIfNecessary(msPlayer);

				return true;
			}
		}
		
		return false;
	}

	private boolean decreaseBullets()
	{
		Gun gun = getGun();
		
		if(!gun.isLoaded())
			return false;
		
		gun.decreaseLoadedBullets();
		
		MSPlayer msPlayer = getMSPlayer();
		HotbarContainer container = msPlayer.getHotbarContainer();
		
		container.apply(msPlayer, gun);
		
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
