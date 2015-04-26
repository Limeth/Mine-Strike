package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.*;
import cz.minestrike.me.limeth.minestrike.equipment.containers.ArmorContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.*;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.minecraft.server.v1_7_R4.NBTTagString;
import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class DefuseEquipmentProvider implements EquipmentProvider
{
	static
	{
		NBTTagList destroyable = new NBTTagList(); //For some reason doesn't work
		
		destroyable.add(new NBTTagString("obsidian"));
		
		ItemStack defaultKit = new ItemStack(Material.IRON_PICKAXE);
		defaultKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
		ItemMeta defaultKitMeta = defaultKit.getItemMeta();
		
		defaultKitMeta.setDisplayName(Translation.EQUIPMENT_DEFUSEKIT_DEFAULT.getMessage());
		defaultKit.setItemMeta(defaultKitMeta);
		
		net.minecraft.server.v1_7_R4.ItemStack nmsDefaultKit = CraftItemStack.asNMSCopy(defaultKit);
		
		nmsDefaultKit.tag.set("CanDestroy", destroyable);
		
		defaultKit = CraftItemStack.asCraftMirror(nmsDefaultKit);
		
		DEFUSE_KIT_DEFAULT = new SimpleEquipment("KIT_DEFAULT", defaultKit, EquipmentCategory.MISCELLANEOUS, false, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.movement", false, false);
		
		ItemStack boughtKit = new ItemStack(Material.DIAMOND_PICKAXE);
		boughtKit.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
		ItemMeta boughtKitMeta = boughtKit.getItemMeta();
		
		boughtKitMeta.setDisplayName(Translation.EQUIPMENT_DEFUSEKIT_BOUGHT.getMessage());
		boughtKit.setItemMeta(boughtKitMeta);
		
		net.minecraft.server.v1_7_R4.ItemStack nmsBoughtKit = CraftItemStack.asNMSCopy(boughtKit);
		
		nmsBoughtKit.tag.set("CanDestroy", destroyable);
		
		boughtKit = CraftItemStack.asCraftMirror(nmsBoughtKit);
		
		DEFUSE_KIT_BOUGHT = new SimpleEquipment("KIT_BOUGHT", boughtKit, EquipmentCategory.MISCELLANEOUS, false, 400, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.movement", false, true);
		
		ItemStack bomb = new ItemStack(Material.OBSIDIAN);
		ItemMeta bombIM = bomb.getItemMeta();
		
		bombIM.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "C4");
		bomb.setItemMeta(bombIM);
		
		BOMB = new SimpleEquipment("BOMB", bomb, EquipmentCategory.MISCELLANEOUS, false, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.c4.c4_draw", true, true);

		EquipmentManager.register(DefuseEquipmentProvider.BOMB);
		EquipmentManager.register(DefuseEquipmentProvider.DEFUSE_KIT_DEFAULT);
		EquipmentManager.register(DefuseEquipmentProvider.DEFUSE_KIT_BOUGHT);

		FilledArrayList<EquipmentSection> categories = new FilledArrayList<>();
		
		categories.add(EquipmentSection.PISTOLS);
		categories.add(EquipmentSection.HEAVY);
		categories.add(EquipmentSection.SMGS);
		categories.add(EquipmentSection.RIFLES);
		
		EquipmentSectionEntry[] gearCTOriginal = EquipmentSection.GEAR.getCounterTerroristsEquipment();
		EquipmentSectionEntry[] gearTOriginal = EquipmentSection.GEAR.getTerroristsEquipment();
		EquipmentSectionEntry[] gearCTNew = new EquipmentSectionEntry[]
				{ EquipmentSectionEntry.valueOf(DefuseEquipmentProvider.DEFUSE_KIT_BOUGHT) };
		EquipmentSectionEntry[] gearCT = ArrayUtils.addAll(gearCTNew, gearCTOriginal);
		EquipmentSection gear = new EquipmentSection(EquipmentSection.GEAR.getId(), EquipmentSection.GEAR.getTranslation(), gearTOriginal, gearCT);
		
		categories.add(gear);
		categories.add(EquipmentSection.GRENADES);
		
		EQUIPMENT_CATEGORIES = categories;
	}
	
	public static final SimpleEquipment DEFUSE_KIT_DEFAULT, DEFUSE_KIT_BOUGHT, BOMB;
	public static final int INDEX_GUN_PRIMARY = 0, INDEX_GUN_SECONDARY = 1, INDEX_GRENADES = 2, INDEX_EXTRA = 6, INDEX_KNIFE = 7,
			INDEX_RADAR = 8, GRENADE_AMOUNT = 4;
	private static final FilledArrayList<EquipmentSection> EQUIPMENT_CATEGORIES;
	private final        DefuseGame                        game;

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

		equipRadar(msPlayer);
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

	public void equipBomb(MSPlayer msPlayer)
	{
		HotbarContainer gameContainer = msPlayer.getHotbarContainer();
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment equipment = invContainer.getEquippedCustomizedEquipment(BOMB);

		if(equipment == null)
			equipment = BOMB;

		ItemStack item = equipment.newItemStack(msPlayer);

		gameContainer.setItem(INDEX_EXTRA, equipment);
		inv.setItem(INDEX_EXTRA, item);
	}

	public boolean hasBomb(MSPlayer msPlayer)
	{
		Container gameContainer = msPlayer.getHotbarContainer();
		Equipment equipment = gameContainer.getItem(INDEX_EXTRA);

		if(equipment == null)
			return false;

		Equipment source = equipment.getSource();

		return source.equals(BOMB);
	}

	public boolean removeBomb(MSPlayer msPlayer)
	{
		boolean hasBomb = hasBomb(msPlayer);

		if(!hasBomb)
			return false;

		Container gameContainer = msPlayer.getHotbarContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();

		gameContainer.setItem(INDEX_EXTRA, null);
		inv.setItem(INDEX_EXTRA, null);

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

	private Gun getDefaultPistol(MSPlayer msPlayer)
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
		boolean primary = gunType.isPrimary(msPlayer);
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
		EquipmentSectionEntry entry = EquipmentSectionEntry.valueOf(Helmet.HELMET);
		Equipment helmet = invContainer.getEquippedEquipment(entry);
		
		armorContainer.setHelmet(helmet);
		armorContainer.apply(msPlayer);
	}
	
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
	
	@Override
	public boolean hasHelmet(MSPlayer msPlayer)
	{
		ArmorContainer armorContainer = msPlayer.getArmorContainer();
		
		return armorContainer.hasHelmet();
	}
	
	public void equipDefuseKit(MSPlayer msPlayer, boolean bought)
	{
		HotbarContainer gameContainer = msPlayer.getHotbarContainer();
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		Equipment kit = bought ? DEFUSE_KIT_BOUGHT : DEFUSE_KIT_DEFAULT;
		Equipment equipment = invContainer.getEquippedCustomizedEquipment(kit);
		
		if(equipment == null)
			equipment = kit;
		
		ItemStack item = equipment.newItemStack(msPlayer);
		
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

	public void purchase(MSPlayer msPlayer, Equipment equipment) throws EquipmentPurchaseException
	{
		String additionError = getAdditionError(msPlayer, equipment, true);

		if(additionError != null)
			throw new EquipmentPurchaseException(equipment, additionError);
		
		int price = equipment.getPrice(msPlayer);
		DefuseGame game = getGame();
		int balance = game.getBalance(msPlayer);
		
		if(price > balance)
			throw new EquipmentPurchaseException(equipment, Translation.GAME_SHOP_ERROR_BALANCE.getMessage(equipment.getDisplayName()));
		
		Equipment thrownEquipment = add(msPlayer, equipment);
		
		if(thrownEquipment != null)
			game.drop(thrownEquipment, msPlayer, true);
		
		game.addBalance(msPlayer, -price);
	}

	public String getAdditionError(MSPlayer msPlayer, Equipment equipment, boolean purchase)
	{
		Equipment sourceEquipment = equipment.getSource();
		Team team = game.getTeam(msPlayer);

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
		else if(sourceEquipment.equals(DEFUSE_KIT_BOUGHT))
		{
			if(team != Team.COUNTER_TERRORISTS)
				return Translation.GAME_SHOP_ERROR_DEFUSEKITTEAM.getMessage();
			else if(hasBoughtDefuseKit(msPlayer))
				return Translation.GAME_SHOP_ERROR_DEFUSEKITPRESENT.getMessage();
		}
		else if(sourceEquipment.equals(BOMB))
		{
			if(team != Team.TERRORISTS)
				return Translation.GAME_SHOP_ERROR_BOMBTEAM.getMessage();
			else if(hasBomb(msPlayer))
				return Translation.GAME_SHOP_ERROR_BOMBPRESENT.getMessage();
		}

		return null;
	}
	
	/**
	 * @return Thrown equipment
	 */
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
		else if(sourceEquipment.equals(DEFUSE_KIT_BOUGHT))
			equipDefuseKit(msPlayer, true);
		else if(sourceEquipment.equals(BOMB))
			equipBomb(msPlayer);
		
		return null;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public FilledArrayList<EquipmentSection> getEquipmentCategories()
	{
		return (FilledArrayList<EquipmentSection>) EQUIPMENT_CATEGORIES.clone();
	}

	public DefuseGame getGame()
	{
		return game;
	}
}
