package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.SilencerToggle;

public class SilencableExtension extends GunExtension
{
	private boolean silenced = true;
	
	public SilencableExtension(Gun gun)
	{
		super(gun);
	}
	
	@Override
	public String getSoundShooting(MSPlayer msPlayer)
	{
		String sound = super.getSoundShooting(msPlayer);
		
		if(!silenced)
			sound += "_unsil";
		
		return sound;
	}
	
	@Override
	public void onLeftClick(MSPlayer msPlayer)
	{
		if(msPlayer.hasGunTask())
			return;
		
		Gun gun = getGun();
		String sound = super.getSoundShooting(msPlayer) + "_silencer_" + (silenced ? "off" : "on");
		
		msPlayer.setGunTask(new SilencerToggle(msPlayer, gun, sound).startLoop());
	}
	
	public boolean isSilenced()
	{
		return silenced;
	}
	
	public void setSilenced(boolean silenced)
	{
		this.silenced = silenced;
	}
	
	public boolean toggleSilencer()
	{
		return silenced = !silenced;
	}
}
