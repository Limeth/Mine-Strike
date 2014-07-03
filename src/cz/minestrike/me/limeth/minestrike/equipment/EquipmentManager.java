package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.ArrayList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public interface EquipmentManager
{
	/* Must have a constructor with the current game. */
	
	public void equip(MSPlayer msPlayer);
	public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException;
	public void setGun(MSPlayer msPlayer, Gun gun);
	public Gun getGun(MSPlayer msPlayer, boolean primary);
	public void removeGun(MSPlayer msPlayer, boolean primary);
	public boolean addGrenade(MSPlayer msPlayer, GrenadeType type);
	public ArrayList<GrenadeType> getGrenades(MSPlayer msPlayer);
	public Equipment getCurrentlyEquipped(MSPlayer msPlayer);
}
