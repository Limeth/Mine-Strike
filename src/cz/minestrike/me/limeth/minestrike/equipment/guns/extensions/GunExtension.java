package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public class GunExtension
{
	private final Gun gun;
	
	public GunExtension(Gun gun)
	{
		this.gun = gun;
	}
	
	public void onLeftClick(MSPlayer msPlayer)
	{
		
	}
	
	public void onRightClick(MSPlayer msPlayer)
	{
		msPlayer.pressTrigger(gun);
	}
	
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return gun.getEquipment().getMovementSpeed(msPlayer);
	}
	
	public String getSoundShooting(MSPlayer msPlayer)
	{
		return gun.getEquipment().getSoundShooting();
	}
	
	public Gun getGun()
	{
		return gun;
	}
}
