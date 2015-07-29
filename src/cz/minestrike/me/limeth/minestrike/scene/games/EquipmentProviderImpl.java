package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSection;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSectionEntry;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.*;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public abstract class EquipmentProviderImpl<T extends Game> implements EquipmentProvider<T>
{
    private final T game;

    public EquipmentProviderImpl(T game)
    {
        Preconditions.checkNotNull(game, "The game must not be null!");

        this.game = game;
    }

    @Override
    public abstract void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException;
    @Override
    public abstract FilledArrayList<EquipmentSection> getEquipmentCategories();

    @Override
    public T getGame()
    {
        return game;
    }

    /**
     * @return Thrown equipment
     */
    @Override
    public Equipment add(MSPlayer msPlayer, Equipment equipment)
    {
        Equipment sourceEquipment = equipment.getSource();

        if(sourceEquipment instanceof GrenadeType)
            addGrenade(msPlayer, (GrenadeType) equipment);
        else if(sourceEquipment instanceof GunType)
        {
            GunType gunType = (GunType) sourceEquipment;
            boolean primary = gunType.isPrimary(msPlayer);
            Gun previousGun = getGun(msPlayer, primary);
            Gun gun;

            if(equipment instanceof GunType)
                gun = new Gun(gunType);
            else
                gun = ((Gun) equipment).clone();

            gun.setOwnerName(msPlayer.getName());
            setGun(msPlayer, gun);

            return previousGun;
        }
        else if(sourceEquipment instanceof Kevlar)
            setKevlar(msPlayer, true);
        else if(sourceEquipment instanceof Helmet)
            setHelmet(msPlayer, true);
        else if(sourceEquipment instanceof KevlarAndHelmet)
        {
            setKevlar(msPlayer, true);
            setHelmet(msPlayer, true);
        }

        return null;
    }

    protected Gun getDefaultPistol(MSPlayer msPlayer)
    {
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        EquipmentSectionEntry firstPistolsEntry = EquipmentSection.PISTOLS.getEntries(msPlayer)[0];
        Equipment equipment = invContainer.getEquippedEquipment(firstPistolsEntry);
        Gun pistol;

        if(equipment instanceof Gun)
        {
            pistol = (Gun) equipment;

            pistol.refresh();
        }
        else if(equipment instanceof GunType)
            pistol = new Gun((GunType) equipment);
        else
            throw new RuntimeException(equipment + " is not a gun.");

        return pistol;
    }

    protected boolean refreshGun(MSPlayer msPlayer, boolean primary)
    {
        Gun gun = getGun(msPlayer, primary);

        if(gun == null)
            return false;

        gun.refresh();
        setGun(msPlayer, gun);
        return true;
    }

    public void equipKnife(MSPlayer msPlayer)
    {
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        Container gameContainer = msPlayer.getHotbarContainer();
        Player player = msPlayer.getPlayer();
        PlayerInventory inv = player.getInventory();
        Equipment equipment = invContainer.getEquippedCustomizedEquipment(Knife.KNIFE);

        if(equipment == null)
            equipment = Knife.KNIFE;

        ItemStack item = equipment.newItemStack(msPlayer);

        gameContainer.setItem(INDEX_KNIFE, equipment);
        inv.setItem(INDEX_KNIFE, item);
    }

    public void equipRadar(MSPlayer msPlayer)
    {
        HotbarContainer gameContainer = msPlayer.getHotbarContainer();
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        Player player = msPlayer.getPlayer();
        PlayerInventory inv = player.getInventory();
        Equipment equipment = invContainer.getEquippedCustomizedEquipment(Radar.RADAR);

        if(equipment == null)
            equipment = Radar.RADAR;

        ItemStack item = equipment.newItemStack(msPlayer);

        gameContainer.setItem(INDEX_RADAR, equipment);
        inv.setItem(INDEX_RADAR, item);
    }

    @Override
    public void equip(MSPlayer msPlayer)
    {
        boolean hasSecondary = refreshGun(msPlayer, false);

        refreshGun(msPlayer, true);

        if(!hasSecondary)
        {
            Gun pistol = getDefaultPistol(msPlayer);

            pistol.setOwnerName(msPlayer.getName());
            setGun(msPlayer, pistol);
        }

        equipKnife(msPlayer);
        equipRadar(msPlayer);
    }

    @Override
    public boolean pickup(MSPlayer msPlayer, Equipment equipment)
    {
        if(getAdditionError(msPlayer, equipment, false) == null)
        {
            add(msPlayer, equipment);
            return true;
        }

        return false;
    }

    @Override
    public String getAdditionError(MSPlayer msPlayer, Equipment equipment, boolean purchase)
    {
        Equipment sourceEquipment = equipment.getSource();

        if(sourceEquipment instanceof GrenadeType)
            try
            {
                checkGrenadeAddition(msPlayer, (GrenadeType) equipment);
            }
            catch(Exception e)
            {
                return e.getLocalizedMessage();
            }
        else if(sourceEquipment instanceof GunType)
        {
            GunType gunType = (GunType) sourceEquipment;
            boolean primary = gunType.isPrimary(msPlayer);

            if(!purchase && getGun(msPlayer, primary) != null)
                return Translation.GAME_SHOP_ERROR_SLOTTAKEN.getMessage();
        }
        else if(sourceEquipment instanceof Kevlar && getKevlarDurability(msPlayer) >= 1)
            return Translation.GAME_SHOP_ERROR_KEVLARNEW.getMessage();
        else if(sourceEquipment instanceof Helmet && hasHelmet(msPlayer))
            return Translation.GAME_SHOP_ERROR_HELMETPRESENT.getMessage();
        else if(sourceEquipment instanceof KevlarAndHelmet && getKevlarDurability(msPlayer) >= 1 && hasHelmet(msPlayer))
            return Translation.GAME_SHOP_ERROR_SETPRESENT.getMessage();

        return null;
    }

    @Override
    public void checkGrenadeAddition(MSPlayer msPlayer, GrenadeType type) throws Exception
    {
        ArrayList<GrenadeType> grenades = getGrenades(msPlayer);
        boolean full = true;
        int typeAmount = 0;

        for(GrenadeType curType : grenades)
        {
            if(curType == null)
            {
                full = false;
                continue;
            }

            if(type == curType)
                typeAmount++;
        }

        if(full)
            throw new Exception(Translation.GAME_SHOP_ERROR_GRENADE_FULL_GENERAL.getMessage());
        else if(typeAmount >= type.getMaxAmount())
            throw new Exception(Translation.GAME_SHOP_ERROR_GRENADE_FULL_SPECIFIC.getMessage(type.getName()));
    }

    @Override
    public void setGun(MSPlayer msPlayer, Gun gun)
    {
        GunType gunType = gun.getEquipment();
        boolean primary = gunType.isPrimary(msPlayer);
        int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
        Container gameContainer = msPlayer.getHotbarContainer();
        Player player = msPlayer.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack item = gun.newItemStack(msPlayer);

        gameContainer.setItem(slot, gun);
        inv.setItem(slot, item);
    }

    @Override
	public Gun getGun(MSPlayer msPlayer, boolean primary)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
		Equipment equipment = gameContainer.getItem(slot);

		if(!(equipment instanceof Gun))
			return null;

		return (Gun) equipment;
	}

    @Override
	public void removeGun(MSPlayer msPlayer, boolean primary)
    {
        Container gameContainer = msPlayer.getHotbarContainer();
        Player player = msPlayer.getPlayer();
        PlayerInventory inv = player.getInventory();
        int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;

        gameContainer.setItem(slot, null);
        inv.setItem(slot, null);
    }

    @Override
	public boolean addGrenade(MSPlayer msPlayer, GrenadeType type)
    {
        try
        {
            checkGrenadeAddition(msPlayer, type);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }

        ArrayList<GrenadeType> grenades = getGrenades(msPlayer);

        for(int i = 0; i < grenades.size(); i++)
        {
            GrenadeType current = grenades.get(i);

            if(current != null)
                continue;

            HotbarContainer gameContainer = msPlayer.getHotbarContainer();
            InventoryContainer invContainer = msPlayer.getInventoryContainer();
            Player player = msPlayer.getPlayer();
            PlayerInventory inv = player.getInventory();
            int slot = i + INDEX_GRENADES;
            Equipment equipment = invContainer.getEquippedCustomizedEquipment(type);

            if(equipment == null)
                equipment = type;

            ItemStack item = equipment.newItemStack(msPlayer);

            gameContainer.setItem(slot, equipment);
            inv.setItem(slot, item);
            return true;
        }

        return false;
    }

    @Override
    public ArrayList<GrenadeType> getGrenades(MSPlayer msPlayer)
    {
        ArrayList<GrenadeType> grenades = Lists.newArrayList();
        Container gameContainer = msPlayer.getHotbarContainer();

        for(int i = 0; i < GRENADE_AMOUNT; i++)
        {
            int slot = i + INDEX_GRENADES;
            Equipment equipment = gameContainer.getItem(slot);

            if(equipment == null || !(equipment.getSource() instanceof GrenadeType))
            {
                grenades.add(null);
                continue;
            }

            GrenadeType grenadeEquipment = (GrenadeType) equipment.getSource();

            grenades.add(grenadeEquipment);
        }

        return grenades;
    }

    @Override
	public void setKevlar(MSPlayer msPlayer, boolean equipped)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        EquipmentSectionEntry entry = EquipmentSectionEntry.valueOf(Kevlar.KEVLAR);
        Equipment kevlar = invContainer.getEquippedEquipment(entry);

        armorContainer.setKevlar(kevlar);
        armorContainer.apply(msPlayer);
    }

    @Override
	public boolean hasKevlar(MSPlayer msPlayer)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();

        return armorContainer.hasKevlar();
    }

    @Override
    public float getKevlarDurability(MSPlayer msPlayer)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();

        return armorContainer.getKevlarDurability();
    }

    @Override
	public void setHelmet(MSPlayer msPlayer, boolean equipped)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        EquipmentSectionEntry entry = EquipmentSectionEntry.valueOf(Helmet.HELMET);
        Equipment helmet = invContainer.getEquippedEquipment(entry);

        armorContainer.setHelmet(helmet);
        armorContainer.apply(msPlayer);
    }

    @Override
	public boolean hasHelmet(MSPlayer msPlayer)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();

        return armorContainer.hasHelmet();
    }

    @Override
    public void equipKevlarAndHelmet(MSPlayer msPlayer)
    {
        ArmorContainer armorContainer = msPlayer.getArmorContainer();
        InventoryContainer invContainer = msPlayer.getInventoryContainer();
        EquipmentSectionEntry kevlarEntry = EquipmentSectionEntry.valueOf(Kevlar.KEVLAR);
        Equipment kevlar = invContainer.getEquippedEquipment(kevlarEntry);
        EquipmentSectionEntry helmetEntry = EquipmentSectionEntry.valueOf(Helmet.HELMET);
        Equipment helmet = invContainer.getEquippedEquipment(helmetEntry);

        armorContainer.setKevlar(kevlar);
        armorContainer.setHelmet(helmet);
        armorContainer.apply(msPlayer);
    }
}
