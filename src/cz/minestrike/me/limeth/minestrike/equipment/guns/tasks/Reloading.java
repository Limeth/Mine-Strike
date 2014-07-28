package cz.minestrike.me.limeth.minestrike.equipment.guns.tasks;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;

public class Reloading extends GunTask
{
	private static final long CHECK_DELAY = 4L;
	private int iterationsLeft;
	private Integer loopId;
	
	public Reloading(@Nonnull MSPlayer mPlayer, @Nonnull Gun gun)
	{
		super(mPlayer, gun);
		iterationsLeft = (int) (gun.getEquipment().getReloadTime() / CHECK_DELAY);
	}
	
	@Override
	public Reloading startLoop()
	{
		loopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, CHECK_DELAY, CHECK_DELAY);
		
		startReload();
		//gunType.onReloadStart(getMSPlayer());
		
		return this;
	}
	
	@Override
	protected boolean execute()
	{
		if(decreaseIterationsLeft())
			reload();
		else
			return true;
		
		return false;
	}
	
	public boolean hasFinished()
	{
		return iterationsLeft <= 0;
	}
	
	private boolean decreaseIterationsLeft()
	{
		iterationsLeft--;
		
		return hasFinished();
	}
	
	private void startReload()
	{
		Gun gun = getGun();
		MSPlayer msPlayer = getMSPlayer();
		HotbarContainer container = msPlayer.getHotbarContainer();
		
		gun.setReloading(true);
		container.apply(msPlayer, gun);
	}
	
	private void stopReload()
	{
		MSPlayer msPlayer = getMSPlayer();
		Container hotbarContainer = msPlayer.getHotbarContainer();
		Gun gun = getGun();
		
		gun.setReloading(false);
		hotbarContainer.apply(msPlayer, gun);
	}
	
	private void reload()
	{
		Gun gun = getGun();
		GunType gunType = gun.getEquipment();
		int clipSize = gunType.getClipSize();
		int unusedBullets = gun.getUnusedBullets();
		int loadedBullets = gun.getLoadedBullets();
		int freeSpace = clipSize - loadedBullets;
		int reloadedBullets = clipSize;
		
		if(reloadedBullets > unusedBullets)
			reloadedBullets = unusedBullets;
		
		if(reloadedBullets > freeSpace)
			reloadedBullets = freeSpace;
		
		MSPlayer msPlayer = getMSPlayer();
		HotbarContainer container = msPlayer.getHotbarContainer();
		
		gun.setUnusedBullets(unusedBullets - reloadedBullets);
		gun.setLoadedBullets(loadedBullets + reloadedBullets);
		gun.setReloading(false);
		container.apply(msPlayer);
	}
	
	@Override
	public void cancel()
	{
		stopReload();
		super.cancel();
	}
	
	@Override
	public Integer getLoopId()
	{
		return loopId;
	}
}
