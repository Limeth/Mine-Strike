package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.equipment.simple.KevlarAndHelmet;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

public class EquipmentCategory
{
	public static final EquipmentCategory PISTOLS = new EquipmentCategory("PISTOLS", Translation.EQUIPMENT_CATEGORY_PISTOLS, new ItemStack(Material.FIREWORK_CHARGE), new EquipmentCategoryEntry[] {
				e(GunType.GLOCK), e(GunType.P250, GunType.CZ75), e(GunType.DEAGLE), e(GunType.ELITE),
				e(GunType.TEC9)
			}, new EquipmentCategoryEntry[]  {
				e(GunType.P2000, GunType.USP_S), e(GunType.P250, GunType.CZ75), e(GunType.DEAGLE),
				e(GunType.ELITE), e(GunType.FIVESEVEN)
			});
	public static final EquipmentCategory HEAVY = new EquipmentCategory("HEAVY", Translation.EQUIPMENT_CATEGORY_HEAVY, new ItemStack(Material.FIREWORK_CHARGE), new EquipmentCategoryEntry[] {
				e(GunType.NOVA), e(GunType.XM1014), e(GunType.SAWEDOFF), e(GunType.M249),
				e(GunType.NEGEV)
			}, new EquipmentCategoryEntry[] {
				e(GunType.NOVA), e(GunType.XM1014), e(GunType.MAG7), e(GunType.M249), e(GunType.NEGEV)
			});
	public static final EquipmentCategory SMGS = new EquipmentCategory("SMGS", Translation.EQUIPMENT_CATEGORY_SMGS, new ItemStack(Material.FIREWORK_CHARGE), new EquipmentCategoryEntry[] {
				e(GunType.MAC10), e(GunType.MP7), e(GunType.UMP45), e(GunType.BIZON), e(GunType.P90)
			}, new EquipmentCategoryEntry[] {
				e(GunType.MP9), e(GunType.MP7), e(GunType.UMP45), e(GunType.BIZON), e(GunType.P90)
			});
	public static final EquipmentCategory RIFLES = new EquipmentCategory("RIFLES", Translation.EQUIPMENT_CATEGORY_RIFLES, new ItemStack(Material.FIREWORK_CHARGE), new EquipmentCategoryEntry[] {
				e(GunType.GALIL_AR), e(GunType.AK_47), e(GunType.SSG_08), e(GunType.SG_556),
				e(GunType.AWP), e(GunType.G3SG1)
			}, new EquipmentCategoryEntry[] {
				e(GunType.FAMAS), e(GunType.M4A4, GunType.M4A1_S), e(GunType.SSG_08),
				e(GunType.AUG), e(GunType.AWP), e(GunType.SCAR_20)
			});
	public static final EquipmentCategory GEAR = new EquipmentCategory("GEAR", Translation.EQUIPMENT_CATEGORY_GEAR, new ItemStack(Material.LEATHER_CHESTPLATE), new EquipmentCategoryEntry[] {
				e(Kevlar.KEVLAR), e(KevlarAndHelmet.KEVLAR_AND_HELMET)
			});
	public static final EquipmentCategory GRENADES = new EquipmentCategory("GRENADES", Translation.EQUIPMENT_CATEGORY_GRENADES, new ItemStack(Material.POTION), new EquipmentCategoryEntry[] {
				e(GrenadeType.INCENDIARY), e(GrenadeType.DECOY), e(GrenadeType.EXPLOSIVE), e(GrenadeType.FLASH), e(GrenadeType.SMOKE)
			});
	
	private final String id;
	private final Translation translation;
	private final ItemStack icon;
	private final EquipmentCategoryEntry[] tEquipment, ctEquipment;
	
	public EquipmentCategory(String id, Translation translation, ItemStack icon, EquipmentCategoryEntry[] tEquipment, EquipmentCategoryEntry[] ctEquipment)
	{
		this.id = id;
		this.translation = translation;
		this.icon = icon;
		this.tEquipment = tEquipment;
		this.ctEquipment = ctEquipment;
		
		LoreAttributes.TEMP_ATTRIBUTES.clear();
		LoreAttributes.extract(icon, LoreAttributes.TEMP_ATTRIBUTES);
		LoreAttributes.TEMP_ATTRIBUTES.put("id", id);
		LoreAttributes.TEMP_ATTRIBUTES.apply(icon);
	}
	
	public EquipmentCategory(String id, Translation translation, ItemStack icon, EquipmentCategoryEntry... equipment)
	{
		this(id, translation, icon, equipment, equipment);
	}
	
	public static EquipmentCategory getById(Collection<EquipmentCategory> categories, String id)
	{
		for(EquipmentCategory cat : categories)
			if(cat.id.equals(id))
				return cat;
		
		return null;
	}
	
	public static EquipmentCategory getByName(Collection<EquipmentCategory> categories, String name)
	{
		for(EquipmentCategory cat : categories)
			if(cat.getName().equals(name))
				return cat;
		
		return null;
	}
	
	public static EquipmentCategory getByInventory(Collection<EquipmentCategory> categories, Inventory inv)
	{
		String title = inv.getTitle();
		int prefixLength = MSConstant.INVENTORY_NAME_PREFIX.length();
		
		if(title.length() <= prefixLength)
			return null;
		
		String name = title.substring(prefixLength);
		
		return getByName(categories, name);
	}
	
	public static EquipmentCategory getByIcon(Collection<EquipmentCategory> categories, ItemStack icon)
	{
		for(EquipmentCategory cat : categories)
			if(cat.getIcon().equals(icon))
				return cat;
		
		return null;
	}
	
	public ItemStack getIcon()
	{
		ItemStack icon = this.icon.clone();
		ItemMeta im = icon.getItemMeta();
		
		im.setDisplayName(ChatColor.BOLD + getName());
		icon.setItemMeta(im);
		
		return icon;
	}

	public void openInventory(MSPlayer msPlayer)
	{
		String name = getName();
		Player player = msPlayer.getPlayer();
		EquipmentCategoryEntry[] entries = getEntries(msPlayer);
		Inventory inv = Bukkit.createInventory(player, PlayerUtil.INVENTORY_WIDTH * 4, MSConstant.INVENTORY_NAME_PREFIX + name);
		
		for(int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, MSConstant.ITEM_BACKGROUND);
		
		int width = (int) Math.ceil(entries.length / 2D);
		int x = 4 - (width / 2);
		
		for(int i = 0; i < entries.length; i++)
		{
			int relX = i % width;
			int relY = i / width;
			int absX = x + relX;
			int absY = 1 + relY;
			int index = absX + absY * MSConstant.INVENTORY_WIDTH;
			EquipmentCategoryEntry entry = entries[i];
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			Equipment equipment = invContainer.getEquippedEquipment(entry);
			Integer price = equipment.getPrice(msPlayer);
			ItemStack item = equipment.newItemStack(msPlayer);
			
			if(price != null)
			{
				EquipmentCustomization customization = EquipmentCustomization.builder()
						.name(Translation.GAME_SHOP_ICONPRICE.getMessage(price)).build();
				
				customization.apply(equipment, item);
			}
			
			inv.setItem(index, item);
		}
		
		player.openInventory(inv);
	}
	
	public EquipmentCategoryEntry getEntry(MSPlayer msPlayer, int x, int y)
	{
		EquipmentCategoryEntry[] entries = getEntries(msPlayer);
		int width = (int) Math.ceil(entries.length / 2D);
		int startX = 4 - (width / 2);
		
		if(x >= startX + width)
			return null;
		
		x -= startX;
		y -= 1;
		
		int index = x + y * width;
		
		if(index < 0 || index >= entries.length)
			return null;
		
		return entries[index];
	}
	
	public EquipmentCategoryEntry getEntry(MSPlayer msPlayer, int inventoryIndex)
	{
		return getEntry(msPlayer, inventoryIndex % MSConstant.INVENTORY_WIDTH, inventoryIndex / MSConstant.INVENTORY_WIDTH);
	}
	
	public EquipmentCategoryEntry[] getEntries(MSPlayer msPlayer)
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
			return null;
		
		Game<?, ?, ?, ?> game = (Game<?, ?, ?, ?>) scene;
		
		if(!(game instanceof TeamGame))
			return getEntries();
		else
		{
			TeamGame<?, ?, ?, ?> teamGame = (TeamGame<?, ?, ?, ?>) game;
			Team team = teamGame.getTeam(msPlayer);
			
			return getEntries(team);
		}
	}
	
	public EquipmentCategoryEntry[] getEntries(Team team)
	{
		if(team == null)
			throw new IllegalArgumentException("The team cannot be null!");
		else if(team == Team.TERRORISTS)
			return tEquipment;
		else if(team == Team.COUNTER_TERRORISTS)
			return ctEquipment;
		else
			throw new IllegalArgumentException("Unknown team " + team + "!");
	}
	
	public EquipmentCategoryEntry[] getEntries()
	{
		HashSet<EquipmentCategoryEntry> set = new HashSet<EquipmentCategoryEntry>();
		
		for(EquipmentCategoryEntry e : tEquipment)
			set.add(e);
		
		for(EquipmentCategoryEntry e : ctEquipment)
			set.add(e);
		
		return set.toArray(new EquipmentCategoryEntry[set.size()]);
	}

	public EquipmentCategoryEntry[] getTerroristsEquipment()
	{
		return tEquipment;
	}

	public EquipmentCategoryEntry[] getCounterTerroristsEquipment()
	{
		return ctEquipment;
	}
	
	private static EquipmentCategoryEntry e(Equipment... equipment)
	{
		return EquipmentCategoryEntry.valueOf(equipment);
	}

	public Translation getTranslation()
	{
		return translation;
	}

	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return translation.getMessage();
	}
}
