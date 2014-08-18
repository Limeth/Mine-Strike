package cz.minestrike.me.limeth.minestrike.scene.games;

import java.util.ArrayList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public interface EquipmentProvider
{
	/* Must have a constructor with the current game. */
	
	public void equip(MSPlayer msPlayer);
	public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException;
	public void setGun(MSPlayer msPlayer, Gun gun);
	public Gun getGun(MSPlayer msPlayer, boolean primary);
	public void removeGun(MSPlayer msPlayer, boolean primary);
	public boolean addGrenade(MSPlayer msPlayer, GrenadeType type);
	public ArrayList<GrenadeType> getGrenades(MSPlayer msPlayer);
	public void setKevlar(MSPlayer msPlayer, boolean equipped);
	public boolean hasKevlar(MSPlayer msPlayer);
	public float getKevlarDurability(MSPlayer msPlayer);
	public void setHelmet(MSPlayer msPlayer, boolean equipped);
	public boolean hasHelmet(MSPlayer msPlayer);
	public void equipKevlarAndHelmet(MSPlayer msPlayer);
	public FilledArrayList<EquipmentCategory> getEquipmentCategories();
}
