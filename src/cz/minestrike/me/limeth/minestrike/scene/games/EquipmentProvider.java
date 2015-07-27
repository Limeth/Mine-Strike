package cz.minestrike.me.limeth.minestrike.scene.games;

import java.util.ArrayList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSection;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public interface EquipmentProvider
{
	/* Must have a constructor with the current game. */
	
	void equip(MSPlayer msPlayer);
	boolean pickup(MSPlayer msPlayer, Equipment equipment);
	void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException;
	void setGun(MSPlayer msPlayer, Gun gun);
	Gun getGun(MSPlayer msPlayer, boolean primary);
	void removeGun(MSPlayer msPlayer, boolean primary);
	boolean addGrenade(MSPlayer msPlayer, GrenadeType type);
	ArrayList<GrenadeType> getGrenades(MSPlayer msPlayer);
	void setKevlar(MSPlayer msPlayer, boolean equipped);
	boolean hasKevlar(MSPlayer msPlayer);
	float getKevlarDurability(MSPlayer msPlayer);
	void setHelmet(MSPlayer msPlayer, boolean equipped);
	boolean hasHelmet(MSPlayer msPlayer);
	void equipKevlarAndHelmet(MSPlayer msPlayer);
	FilledArrayList<EquipmentSection> getEquipmentCategories();
}
