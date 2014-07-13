package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import java.util.ArrayList;

import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.Container;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategoryEntry;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Helmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Knife;
import cz.minestrike.me.limeth.minestrike.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class DefuseEquipmentProvider implements EquipmentProvider
{
	static
	{
		ItemStack defaultKit = new ItemStack(Material.IRON_PICKAXE);
		defaultKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
		ItemMeta defaultKitMeta = defaultKit.getItemMeta();
		
		defaultKitMeta.setDisplayName(Translation.EQUIPMENT_DEFUSEKIT_DEFAULT.getMessage());
		defaultKit.setItemMeta(defaultKitMeta);
		DEFUSE_KIT_DEFAULT = new SimpleEquipment("KIT_DEFAULT", defaultKit, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.movement");
		
		ItemStack boughtKit = new ItemStack(Material.DIAMOND_PICKAXE);
		boughtKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
		ItemMeta boughtKitMeta = boughtKit.getItemMeta();
		
		boughtKitMeta.setDisplayName(Translation.EQUIPMENT_DEFUSEKIT_BOUGHT.getMessage());
		boughtKit.setItemMeta(boughtKitMeta);
		DEFUSE_KIT_BOUGHT = new SimpleEquipment("KIT_BOUGHT", boughtKit, 400, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.movement");
		
		ItemStack bomb = new ItemStack(Material.OBSIDIAN);
		ItemMeta bombIM = bomb.getItemMeta();
		
		bombIM.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "C4");
		bomb.setItemMeta(bombIM);
		
		BOMB = new SimpleEquipment("BOMB", bomb, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.c4.c4_draw");
		
		FilledArrayList<EquipmentCategory> categories = new FilledArrayList<EquipmentCategory>();
		
		categories.add(EquipmentCategory.PISTOLS);
		categories.add(EquipmentCategory.HEAVY);
		categories.add(EquipmentCategory.SMGS);
		categories.add(EquipmentCategory.RIFLES);
		
		EquipmentCategoryEntry[] gearCTOriginal = EquipmentCategory.GEAR.getCounterTerroristsEquipment();
		EquipmentCategoryEntry[] gearTOriginal = EquipmentCategory.GEAR.getTerroristsEquipment();
		EquipmentCategoryEntry[] gearCTNew = new EquipmentCategoryEntry[]
				{ EquipmentCategoryEntry.valueOf(DefuseEquipmentProvider.DEFUSE_KIT_BOUGHT) };
		EquipmentCategoryEntry[] gearCT = ArrayUtils.addAll(gearCTNew, gearCTOriginal);
		EquipmentCategory gear = new EquipmentCategory(EquipmentCategory.GEAR.getId(), EquipmentCategory.GEAR.getTranslation(), EquipmentCategory.GEAR.getIcon(), gearTOriginal, gearCT);
		
		categories.add(gear);
		categories.add(EquipmentCategory.GRENADES);
		
		EQUIPMENT_CATEGORIES = categories;
	}
	
	public static final SimpleEquipment DEFUSE_KIT_DEFAULT, DEFUSE_KIT_BOUGHT, BOMB;
	public static final int INDEX_GUN_PRIMARY = 0, INDEX_GUN_SECONDARY = 1, INDEX_GRENADES = 2, INDEX_EXTRA = 7, INDEX_KNIFE = 8, GRENADE_AMOUNT = 3;
	private static final FilledArrayList<EquipmentCategory> EQUIPMENT_CATEGORIES;
	private final DefuseGame game;
	
	public DefuseEquipmentProvider(DefuseGame game)
	{
		this.game = game;
	}
	
	@Override
	public void equip(MSPlayer msPlayer)
	{
		DefuseGame game = getGame();
		Team team = game.getTeam(msPlayer);
		boolean hasSecondary = refreshGun(msPlayer, false);
		
		refreshGun(msPlayer, true);
		
		if(!hasSecondary)
		{
			Gun pistol = getDefaultPistol(msPlayer);
			
			pistol.setOwnerName(msPlayer.getName());
			setGun(msPlayer, pistol);
		}
		
		equipKnife(msPlayer);
		
		if(team == Team.COUNTER_TERRORISTS)
			equipDefuseKit(msPlayer, false);
	}
	
	public void equipBomb(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		CustomizedEquipment<Equipment> equipment = new CustomizedEquipment<Equipment>(BOMB, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer);
		
		gameContainer.setItem(INDEX_EXTRA, equipment);
		inv.setItem(INDEX_EXTRA, item);
	}
	
	public void equipKnife(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		CustomizedEquipment<Equipment> equipment = new CustomizedEquipment<Equipment>(Knife.KNIFE, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer);
		
		gameContainer.setItem(INDEX_KNIFE, equipment);
		inv.setItem(INDEX_KNIFE, item);
	}
	
	private Gun getDefaultPistol(MSPlayer msPlayer)
	{
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		EquipmentCategoryEntry firstPistolsEntry = EquipmentCategory.PISTOLS.getEntries(msPlayer)[0];
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
	
	private boolean refreshGun(MSPlayer msPlayer, boolean primary)
	{
		Gun gun = getGun(msPlayer, primary);
		
		if(gun == null)
			return false;
		
		gun.refresh();
		setGun(msPlayer, gun);
		return true;
	}
	
	public void setGun(MSPlayer msPlayer, Gun gun)
	{
		GunType gunType = gun.getEquipment();
		boolean primary = gunType.isPrimary();
		int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack item = gun.newItemStack(msPlayer);
		
		gameContainer.setItem(slot, gun);
		inv.setItem(slot, item);
	}
	
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
	
	public boolean canHoldMoreGrenades(MSPlayer msPlayer)
	{
		return getGrenadeAmount(msPlayer) < GRENADE_AMOUNT;
	}
	
	public int getGrenadeAmount(MSPlayer msPlayer)
	{
		ArrayList<GrenadeType> grenades = getGrenades(msPlayer);
		int amount = 0;
		
		for(int i = 0; i < grenades.size(); i++)
			if(grenades.get(i) != null)
				amount++;
		
		return amount;
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
			
			Container gameContainer = msPlayer.getHotbarContainer();
			Player player = msPlayer.getPlayer();
			PlayerInventory inv = player.getInventory();
			int slot = i + INDEX_GRENADES;
			CustomizedEquipment<GrenadeType> equipment = new CustomizedEquipment<GrenadeType>(type, null); //TODO custom
			ItemStack item = equipment.newItemStack(msPlayer);
			
			gameContainer.setItem(slot, equipment);
			inv.setItem(slot, item);
			return true;
		}
		
		return false;
	}
	
	public void checkGrenadeAddition(MSPlayer msPlayer, GrenadeType type) throws Exception
	{
		ArrayList<GrenadeType> grenades = getGrenades(msPlayer);
		boolean full = true;
		int typeAmount = 0;
		
		for(int i = 0; i < grenades.size(); i++)
		{
			GrenadeType curType = grenades.get(i);
			
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
	public ArrayList<GrenadeType> getGrenades(MSPlayer msPlayer)
	{
		ArrayList<GrenadeType> grenades = new ArrayList<GrenadeType>();
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
		EquipmentCategoryEntry entry = EquipmentCategoryEntry.valueOf(Kevlar.KEVLAR);
		Equipment kevlar = invContainer.getEquippedEquipment(entry);
		
		armorContainer.setKevlar(kevlar);
		armorContainer.apply(msPlayer);
	}
	
	@Override
	public float getKevlarDurability(MSPlayer msPlayer)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		
		return armorContainer.getKevlarDurability();
	}
	
	@Override
	public boolean hasKevlar(MSPlayer msPlayer)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		
		return armorContainer.hasKevlar();
	}
	
	@Override
	public void setHelmet(MSPlayer msPlayer, boolean equipped)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		EquipmentCategoryEntry entry = EquipmentCategoryEntry.valueOf(Helmet.HELMET);
		Equipment helmet = invContainer.getEquippedEquipment(entry);
		
		armorContainer.setHelmet(helmet);
		armorContainer.apply(msPlayer);
	}
	
	public void equipKevlarAndHelmet(MSPlayer msPlayer)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		EquipmentCategoryEntry kevlarEntry = EquipmentCategoryEntry.valueOf(Kevlar.KEVLAR);
		Equipment kevlar = invContainer.getEquippedEquipment(kevlarEntry);
		EquipmentCategoryEntry helmetEntry = EquipmentCategoryEntry.valueOf(Helmet.HELMET);
		Equipment helmet = invContainer.getEquippedEquipment(helmetEntry);
		
		armorContainer.setKevlar(kevlar);
		armorContainer.setHelmet(helmet);
		armorContainer.apply(msPlayer);
	}
	
	@Override
	public boolean hasHelmet(MSPlayer msPlayer)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		
		return armorContainer.hasHelmet();
	}
	
	public void equipDefuseKit(MSPlayer msPlayer, boolean bought)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment kit = bought ? DEFUSE_KIT_BOUGHT : DEFUSE_KIT_DEFAULT;
		CustomizedEquipment<Equipment> equipment = new CustomizedEquipment<Equipment>(kit, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer); //TODO customization
		
		gameContainer.setItem(INDEX_EXTRA, equipment);
		inv.setItem(INDEX_EXTRA, item);
	}
	
	public boolean hasBoughtDefuseKit(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Equipment equipment = gameContainer.getItem(INDEX_EXTRA);
		Equipment type = equipment.getSource();
		
		return DEFUSE_KIT_BOUGHT.equals(type);
	}

	@Override
	public Equipment getCurrentlyEquipped(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		Equipment equipment = gameContainer.getItem(slot);
		
		return equipment;
	}

	@Override
	public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException
	{
		int price = equipment.getPrice(msPlayer);
		DefuseGame game = getGame();
		int balance = game.getBalance(msPlayer);
		Equipment source = equipment.getSource();
		
		if(equipment instanceof GrenadeType)
			try
			{
				checkGrenadeAddition(msPlayer, (GrenadeType) equipment);
			}
			catch(Exception e)
			{
				throw new EquipmentPurchaseException(equipment, e.getMessage());
			}
		
		if(price > balance)
			throw new EquipmentPurchaseException(equipment, Translation.GAME_SHOP_ERROR_BALANCE.getMessage(equipment.getDisplayName()));
		
		if(equipment.purchase(msPlayer))
			if(source instanceof GrenadeType)
				addGrenade(msPlayer, (GrenadeType) equipment);
			else if(source instanceof GunType)
			{
				GunType gunType = (GunType) equipment;
				Gun gun = new Gun(gunType);
				
				gun.setOwnerName(msPlayer.getName());
				setGun(msPlayer, gun);
			}
			else if(source instanceof Kevlar)
			{
				setKevlar(msPlayer, true);
			}
			else if(source.equals(DEFUSE_KIT_BOUGHT))
			{
				equipDefuseKit(msPlayer, true);
			}
			else
				throw new EquipmentPurchaseException(equipment, Translation.GAME_SHOP_ERROR_UNKNOWN.getMessage(equipment.getClass().getSimpleName()));
		
		game.addBalance(msPlayer, -price);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FilledArrayList<EquipmentCategory> getEquipmentCategories()
	{
		return (FilledArrayList<EquipmentCategory>) EQUIPMENT_CATEGORIES.clone();
	}

	public DefuseGame getGame()
	{
		return game;
	}
}
