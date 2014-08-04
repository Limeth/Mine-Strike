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
	
	public void onSelect(MSPlayer msPlayer) {}
	public void onDeselect(MSPlayer msPlayer) {}
	public boolean onLeftClick(MSPlayer msPlayer) { return false; }
	
	public boolean onRightClick(MSPlayer msPlayer)
	{
		msPlayer.pressTrigger(gun);
		return true;
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
