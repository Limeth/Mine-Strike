package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import org.bukkit.Bukkit;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;

public class BurstFireExtension extends GunExtension
{
	public static final String SOUND_TOGGLE = "projectsurvive:counterstrike.weapons.auto_semiauto_switch";
	
	public BurstFireExtension(Gun gun)
	{
		super(gun);
	}
	
	@Override
	public void onLeftClick(MSPlayer msPlayer)
	{
		Gun gun = getGun();
		
		gun.setSecondaryState(!gun.isSecondaryState());
		SoundManager.play(SOUND_TOGGLE, msPlayer.getPlayer());
	}
	
	@Override
	public void onRightClick(MSPlayer msPlayer)
	{
		Gun gun = getGun();
		
		if(!gun.isSecondaryState())
		{
			super.onRightClick(msPlayer);
			return;
		}
		
		if(!gun.isShotDelaySatisfied())
			return;
		
		shootBurst(msPlayer);
	}
	
	public void shootBurst(MSPlayer msPlayer)
	{
		Gun gun = getGun();
		int bulletAmount = gun.getShootingBullets();
		
		if(bulletAmount >= 1)
			shootSingle(msPlayer);
		
		for(int i = 0; i < bulletAmount - 1; i++)
			Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new BurstRunnable(msPlayer), i);
	}
	
	private void shootSingle(MSPlayer msPlayer)
	{
		Gun gun = getGun();
		
		if(!gun.isLoaded())
			return;
		
		HotbarContainer container = msPlayer.getHotbarContainer();
		
		gun.decreaseLoadedBullets();
		msPlayer.shoot(gun);
		container.apply(msPlayer, gun);
	}
	
	private class BurstRunnable implements Runnable
	{
		private final MSPlayer msPlayer;
		
		public BurstRunnable(MSPlayer msPlayer)
		{
			this.msPlayer = msPlayer;
		}
		
		@Override
		public void run()
		{
			shootSingle(msPlayer);
		}
	}
}
