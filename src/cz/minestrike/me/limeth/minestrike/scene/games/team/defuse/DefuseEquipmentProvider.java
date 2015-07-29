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
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProviderImpl;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
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

public class DefuseEquipmentProvider extends EquipmentProviderImpl<DefuseGame>
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
		EquipmentSectionEntry.updateManagedEquipment();

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

	private static final FilledArrayList<EquipmentSection> EQUIPMENT_CATEGORIES;

	public DefuseEquipmentProvider(DefuseGame game)
	{
		super(game);
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

	@Override
	public void equip(MSPlayer msPlayer)
	{
		DefuseGame game = getGame();
		Team team = game.getTeam(msPlayer);

		if(team == Team.COUNTER_TERRORISTS)
			equipDefuseKit(msPlayer, false);

		super.equip(msPlayer);
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

    @Override
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
        DefuseGame game = getGame();
		Team team = game.getTeam(msPlayer);

        if(sourceEquipment.equals(DEFUSE_KIT_BOUGHT))
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

		return super.getAdditionError(msPlayer, equipment, purchase);
	}

	/**
	 * @return Thrown equipment
	 */
	public Equipment add(MSPlayer msPlayer, Equipment equipment)
	{
		Equipment sourceEquipment = equipment.getSource();

		if(sourceEquipment.equals(DEFUSE_KIT_BOUGHT))
			equipDefuseKit(msPlayer, true);
		else if(sourceEquipment.equals(BOMB))
			equipBomb(msPlayer);
		
		return super.add(msPlayer, equipment);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FilledArrayList<EquipmentSection> getEquipmentCategories()
	{
		return (FilledArrayList<EquipmentSection>) EQUIPMENT_CATEGORIES.clone();
	}
}
