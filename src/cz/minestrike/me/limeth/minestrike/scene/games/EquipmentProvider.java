package cz.minestrike.me.limeth.minestrike.scene.games;

import java.util.ArrayList;

import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSection;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSectionEntry;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Helmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.equipment.simple.KevlarAndHelmet;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public interface EquipmentProvider<T extends Game>
{
	int INDEX_GUN_PRIMARY = 0, INDEX_GUN_SECONDARY = 1, INDEX_GRENADES = 2, INDEX_EXTRA = 6, INDEX_KNIFE = 7,
			INDEX_RADAR = 8, GRENADE_AMOUNT = 4;
	/* Must have a constructor with the current game. */

    T getGame();
	void equip(MSPlayer msPlayer);
	void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException;
    FilledArrayList<EquipmentSection> getEquipmentCategories();

    /**
     * @return Thrown equipment
     */
    Equipment add(MSPlayer msPlayer, Equipment equipment);
    boolean pickup(MSPlayer msPlayer, Equipment equipment);
    String getAdditionError(MSPlayer msPlayer, Equipment equipment, boolean purchase);
    void checkGrenadeAddition(MSPlayer msPlayer, GrenadeType type) throws Exception;
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
}
