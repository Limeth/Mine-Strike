package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import java.util.ArrayList;

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
import cz.minestrike.me.limeth.minestrike.equipment.Container;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentType;
import cz.minestrike.me.limeth.minestrike.equipment.Knife;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipmentType;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.games.Team;

public class DefuseEquipmentManager implements EquipmentProvider
{
	static
	{
		ItemStack defaultKit = new ItemStack(Material.IRON_PICKAXE);
		defaultKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
		DEFUSE_KIT_DEFAULT = new SimpleEquipmentType("KIT_DEFAULT", defaultKit, 0, MSConstant.MOVEMENT_SPEED_DEFAULT);
		
		ItemStack boughtKit = new ItemStack(Material.DIAMOND_PICKAXE);
		boughtKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
		DEFUSE_KIT_BOUGHT = new SimpleEquipmentType("KIT_BOUGHT", boughtKit, 400, MSConstant.MOVEMENT_SPEED_DEFAULT);
		
		ItemStack bomb = new ItemStack(Material.OBSIDIAN);
		ItemMeta bombIM = bomb.getItemMeta();
		
		bombIM.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "C4");
		bomb.setItemMeta(bombIM);
		
		BOMB = new SimpleEquipmentType("BOMB", bomb, 0, MSConstant.MOVEMENT_SPEED_DEFAULT);
	}
	
	public static final SimpleEquipmentType DEFUSE_KIT_DEFAULT, DEFUSE_KIT_BOUGHT, BOMB;
	public static final int INDEX_GUN_PRIMARY = 0, INDEX_GUN_SECONDARY = 1, INDEX_GRENADES = 2, INDEX_EXTRA = 7, INDEX_KNIFE = 8, GRENADE_AMOUNT = 3;
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
	
	public void equipBomb(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment<EquipmentType> equipment = new Equipment<EquipmentType>(BOMB, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer);
		
		gameContainer.setItem(INDEX_EXTRA, equipment);
		inv.setItem(INDEX_EXTRA, item);
	}
	
	public void equipKnife(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment<EquipmentType> equipment = new Equipment<EquipmentType>(Knife.KNIFE, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer);
		
		gameContainer.setItem(INDEX_KNIFE, equipment);
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
		Equipment<? extends EquipmentType> equipment = gameContainer.getItem(slot);
		
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
		ArrayList<Equipment<GrenadeType>> grenades = getGrenades(msPlayer);
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
		
		ArrayList<Equipment<GrenadeType>> grenades = getGrenades(msPlayer);
		
		for(int i = 0; i < grenades.size(); i++)
		{
			Equipment<GrenadeType> current = grenades.get(i);
			
			if(current != null)
				continue;
			
			Container gameContainer = msPlayer.getHotbarContainer();
			Player player = msPlayer.getPlayer();
			PlayerInventory inv = player.getInventory();
			int slot = i + INDEX_GRENADES;
			Equipment<GrenadeType> equipment = new Equipment<GrenadeType>(type, null); //TODO custom
			ItemStack item = equipment.newItemStack(msPlayer);
			
			gameContainer.setItem(slot, equipment);
			inv.setItem(slot, item);
			return true;
		}
		
		return false;
	}
	
	public void checkGrenadeAddition(MSPlayer msPlayer, GrenadeType type) throws Exception
	{
		ArrayList<Equipment<GrenadeType>> grenades = getGrenades(msPlayer);
		boolean full = true;
		int typeAmount = 0;
		
		for(int i = 0; i < grenades.size(); i++)
		{
			Equipment<GrenadeType> equipment = grenades.get(i);
			GrenadeType curType = equipment.getType();
			
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
	public ArrayList<Equipment<GrenadeType>> getGrenades(MSPlayer msPlayer)
	{
		ArrayList<Equipment<GrenadeType>> grenades = new ArrayList<Equipment<GrenadeType>>();
		Container gameContainer = msPlayer.getHotbarContainer();
		
		for(int i = 0; i < GRENADE_AMOUNT; i++)
		{
			int slot = i + INDEX_GRENADES;
			Equipment<? extends EquipmentType> equipment = gameContainer.getItem(slot);
			EquipmentType type = equipment.getType();
			
			if(!(type instanceof GrenadeType))
				continue;
			
			@SuppressWarnings("unchecked")
			Equipment<GrenadeType> grenadeEquipment = (Equipment<GrenadeType>) equipment;
			
			grenades.add(grenadeEquipment);
		}
		
		return grenades;
	}
	
	public void equipDefuseKit(MSPlayer msPlayer, boolean bought)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		EquipmentType kit = bought ? DEFUSE_KIT_BOUGHT : DEFUSE_KIT_DEFAULT;
		Equipment<EquipmentType> equipment = new Equipment<EquipmentType>(kit, null); //TODO custom
		ItemStack item = equipment.newItemStack(msPlayer); //TODO customization
		
		gameContainer.setItem(INDEX_EXTRA, equipment);
		inv.setItem(INDEX_EXTRA, item);
	}
	
	public boolean hasBoughtDefuseKit(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Equipment<? extends EquipmentType> equipment = gameContainer.getItem(INDEX_EXTRA);
		EquipmentType type = equipment.getType();
		
		return DEFUSE_KIT_BOUGHT.equals(type);
	}

	@Override
	public Equipment<? extends EquipmentType> getCurrentlyEquipped(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		Equipment<? extends EquipmentType> equipment = gameContainer.getItem(slot);
		
		return equipment;
	}

	@Override
	public void purchase(MSPlayer msPlayer, EquipmentType equipment) throws EquipmentPurchaseException
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
