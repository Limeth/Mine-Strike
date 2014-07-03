package cz.minestrike.me.limeth.minestrike.equipment.guns;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;

public class Reloading extends GunTask
{
	private static final long CHECK_DELAY = 4L;
	private int iterationsLeft;
	private Integer loopId;
	
	public Reloading(@Nonnull MSPlayer mPlayer, int slotId, @Nonnull GunType gunType)
	{
		super(mPlayer, slotId, gunType);
		iterationsLeft = (int) (gunType.getReloadTime() / CHECK_DELAY);
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
		{
			Gun gun = parseGunIfSelected();
			
			if(gun != null)
				reload(gun);
		}
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
	
	private ItemStack startReload()
	{
		Gun gun = parseGunIfSelected();
		
		if(gun == null)
			cancel();
		
		gun.setReloading(true);
		
		ItemStack is = gun.createItemStack();
		
		msPlayer.getPlayer().setItemInHand(is);
		
		return is;
	}
	
	private void stopReload()
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack is = inv.getItem(slotId);
		Gun gun = Gun.parse(is);
		
		if(gun == null || !gun.isReloading())
			return;
		
		GunType gunType = gun.getType();
		
		if(gunType != this.gunType)
			return;
		
		gun.setReloading(false);
		
		is = gun.createItemStack();
		
		inv.setItem(slotId, is);
	}
	
	private ItemStack reload(Gun gun)
	{
		int clipSize = gunType.getClipSize();
		int unusedBullets = gun.getUnusedBullets();
		int loadedBullets = gun.getLoadedBullets();
		int freeSpace = clipSize - loadedBullets;
		int reloadedBullets = clipSize;
		
		if(reloadedBullets > unusedBullets)
			reloadedBullets = unusedBullets;
		
		if(reloadedBullets > freeSpace)
			reloadedBullets = freeSpace;
		
		Player player = msPlayer.getPlayer();
		
		gun.setUnusedBullets(unusedBullets - reloadedBullets);
		gun.setLoadedBullets(loadedBullets + reloadedBullets);
		gun.setReloading(false);
		
		ItemStack is = gun.createItemStack();
		
		player.setItemInHand(is);
	//	gunType.onReloadComplete(msPlayer);
		
		return is;
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
