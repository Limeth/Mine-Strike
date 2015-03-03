package cz.minestrike.me.limeth.minestrike.equipment.guns.tasks;

import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.util.SoundSequence;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class Reloading extends GunTask
{
	private static final long CHECK_DELAY = 4L;
	private int iterationsLeft;
	private Integer loopId;
	
	public Reloading(@Nonnull MSPlayer msPlayer, @Nonnull Gun gun)
	{
		super(msPlayer, gun);
		iterationsLeft = (int) (gun.getEquipment().getReloadTime(msPlayer) / CHECK_DELAY);
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
		List<Player> player = Lists.newArrayList(msPlayer.getPlayer());
		HotbarContainer container = msPlayer.getHotbarContainer();
		GunType gunType = gun.getEquipment();
		SoundSequence sequence = gunType.getReloadingSoundSequence();

		if(sequence != null)
			sequence.play(() -> player);

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
