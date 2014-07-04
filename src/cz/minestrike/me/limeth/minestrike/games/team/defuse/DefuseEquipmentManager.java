package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.Knife;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.games.Team;

public class DefuseEquipmentManager implements EquipmentManager
{
	static
	{
		ItemStack defaultKit = new ItemStack(Material.IRON_PICKAXE);
		defaultKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
		DEFUSE_KIT_DEFAULT = new SimpleEquipment(defaultKit, 0, MSConstant.MOVEMENT_SPEED_DEFAULT);
		
		ItemStack boughtKit = new ItemStack(Material.DIAMOND_PICKAXE);
		boughtKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
		DEFUSE_KIT_BOUGHT = new SimpleEquipment(boughtKit, 400, MSConstant.MOVEMENT_SPEED_DEFAULT);
	}
	
	public static final Equipment DEFUSE_KIT_DEFAULT, DEFUSE_KIT_BOUGHT;
	public static final int INDEX_GUN_PRIMARY = 0, INDEX_GUN_SECONDARY = 1, INDEX_GRENADES = 2, INDEX_DEFUSE_KIT = 7, INDEX_KNIFE = 8, GRENADE_AMOUNT = 3;
	private final DefuseGame game;
	
	public DefuseEquipmentManager(DefuseGame game)
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
			GunType pistolType = getDefaultPistol(msPlayer);
			Gun pistol = new Gun(msPlayer, pistolType);
			
			setGun(msPlayer, pistol);
		}
		
		equipKnife(msPlayer);
		
		if(team == Team.COUNTER_TERRORISTS)
			equipDefuseKit(msPlayer, false);
	}
	
	public void equipKnife(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack item = Knife.KNIFE.newItemStack(msPlayer, null); //TODO equipment customization
		
		inv.setItem(INDEX_KNIFE, item);
	}
	
	private GunType getDefaultPistol(MSPlayer msPlayer)
	{
		return (GunType) EquipmentCategory.PISTOLS.getEquipment(msPlayer)[0]; //TODO support for more pistols
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
		GunType gunType = gun.getType();
		boolean primary = gunType.isPrimary();
		int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack item = gun.createItemStack();
		
		inv.setItem(slot, item);
	}
	
	public Gun getGun(MSPlayer msPlayer, boolean primary)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
		ItemStack item = inv.getItem(slot);
		
		if(item == null)
			return null;
		
		return Gun.tryParse(item);
	}

	@Override
	public void removeGun(MSPlayer msPlayer, boolean primary)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = primary ? INDEX_GUN_PRIMARY : INDEX_GUN_SECONDARY;
		
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
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		for(int i = 0; i < grenades.size(); i++)
		{
			GrenadeType current = grenades.get(i);
			
			if(current != null)
				continue;
			
			int slot = i + INDEX_GRENADES;
			ItemStack item = type.newItemStack(msPlayer, null); //TODO customization
			
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
				full = false;
			else if(type == curType)
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
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		for(int i = 0; i < GRENADE_AMOUNT; i++)
		{
			int slot = i + INDEX_GRENADES;
			ItemStack item = inv.getItem(slot);
			GrenadeType type = GrenadeType.valueOf(item);
			
			grenades.add(type);
		}
		
		return grenades;
	}
	
	public void equipDefuseKit(MSPlayer msPlayer, boolean bought)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment kit = bought ? DEFUSE_KIT_BOUGHT : DEFUSE_KIT_DEFAULT;
		ItemStack item = kit.newItemStack(msPlayer, null); //TODO customization
		
		inv.setItem(INDEX_DEFUSE_KIT, item);
	}
	
	public boolean hasBoughtDefuseKit(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack kit = inv.getItem(INDEX_DEFUSE_KIT);
		
		return DEFUSE_KIT_BOUGHT.equals(kit);
	}

	@Override
	public Equipment getCurrentlyEquipped(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		
		if(slot == INDEX_GUN_PRIMARY)
		{
			Gun gun = getGun(msPlayer, true);
			
			if(gun == null)
				return null;
			
			return gun.getType();
		}
		else if(slot == INDEX_GUN_SECONDARY)
		{
			Gun gun = getGun(msPlayer, false);
			
			if(gun == null)
				return null;
			
			return gun.getType();
		}
		else if(slot >= INDEX_GRENADES && slot < INDEX_GRENADES + GRENADE_AMOUNT)
		{
			ItemStack is = inv.getItemInHand();
			
			return GrenadeType.valueOf(is);
		}
		else if(slot == INDEX_DEFUSE_KIT)
			return hasBoughtDefuseKit(msPlayer) ? DEFUSE_KIT_BOUGHT : DEFUSE_KIT_DEFAULT;
		else if(slot == INDEX_KNIFE)
			return Knife.KNIFE;
		else
			return null;
	}

	@Override
	public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException
	{
		int price = equipment.getPrice(msPlayer);
		DefuseGame game = getGame();
		int balance = game.getBalance(msPlayer);
		
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
		
		if(equipment instanceof GrenadeType)
			addGrenade(msPlayer, (GrenadeType) equipment);
		else if(equipment instanceof GunType)
		{
			GunType gunType = (GunType) equipment;
			Gun gun = new Gun(msPlayer, gunType);
			
			setGun(msPlayer, gun);
		}
		else
			throw new EquipmentPurchaseException(equipment, Translation.GAME_SHOP_ERROR_UNKNOWN.getMessage(equipment.getClass().getSimpleName()));
		
		game.addBalance(msPlayer, -price);
	}

	public DefuseGame getGame()
	{
		return game;
	}
}
