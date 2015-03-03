package cz.minestrike.me.limeth.minestrike.equipment;

import com.google.common.collect.Sets;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.M249;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.Negev;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.AWP;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.G3SG1;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.SCAR20;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.SSG08;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.MAG7;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.Nova;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.SawedOff;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.XM1014;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.*;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.equipment.simple.KevlarAndHelmet;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class EquipmentSection
{
	public static final EquipmentSection PISTOLS  = new EquipmentSection("PISTOLS", Translation.EQUIPMENT_CATEGORY_PISTOLS, new EquipmentSectionEntry[]{e(Glock.getInstance()), e(P250.getInstance(), CZ75.getInstance()), e(Deagle.getInstance()), e(Elite.getInstance()), e(TEC9.getInstance())}, new EquipmentSectionEntry[]{e(P2000.getInstance(), USPS.getInstance()), e(P250.getInstance(), CZ75.getInstance()), e(Deagle.getInstance()), e(Elite.getInstance()), e(FiveSeven.getInstance())});
	public static final EquipmentSection HEAVY    = new EquipmentSection("HEAVY", Translation.EQUIPMENT_CATEGORY_HEAVY, new EquipmentSectionEntry[]{e(Nova.getInstance()), e(XM1014.getInstance()), e(SawedOff.getInstance()), e(M249.getInstance()), e(Negev.getInstance())}, new EquipmentSectionEntry[]{e(Nova.getInstance()), e(XM1014.getInstance()), e(MAG7.getInstance()), e(M249.getInstance()), e(Negev.getInstance())});
	public static final EquipmentSection SMGS     = new EquipmentSection("SMGS", Translation.EQUIPMENT_CATEGORY_SMGS, new EquipmentSectionEntry[]{e(MAC10.getInstance()), e(MP7.getInstance()), e(UMP45.getInstance()), e(Bizon.getInstance()), e(P90.getInstance())}, new EquipmentSectionEntry[]{e(MP9.getInstance()), e(MP7.getInstance()), e(UMP45.getInstance()), e(Bizon.getInstance()), e(P90.getInstance())});
	public static final EquipmentSection RIFLES   = new EquipmentSection("RIFLES", Translation.EQUIPMENT_CATEGORY_RIFLES, new EquipmentSectionEntry[]{e(GalilAR.getInstance()), e(AK47.getInstance()), e(SSG08.getInstance()), e(SG556.getInstance()), e(AWP.getInstance()), e(G3SG1.getInstance())}, new EquipmentSectionEntry[]{e(FAMAS.getInstance()), e(M4A4.getInstance(), M4A1S.getInstance()), e(SSG08.getInstance()), e(AUG.getInstance()), e(AWP.getInstance()), e(SCAR20.getInstance())});
	public static final EquipmentSection GEAR     = new EquipmentSection("GEAR", Translation.EQUIPMENT_CATEGORY_GEAR, e(Kevlar.KEVLAR), e(KevlarAndHelmet.KEVLAR_AND_HELMET));
	public static final EquipmentSection GRENADES = new EquipmentSection("GRENADES", Translation.EQUIPMENT_CATEGORY_GRENADES, e(GrenadeType.INCENDIARY), e(GrenadeType.DECOY), e(GrenadeType.EXPLOSIVE), e(GrenadeType.FLASH), e(GrenadeType.SMOKE));

	private final String                  id;
	private final Translation             translation;
	private final ItemStack               icon;
	private final EquipmentSectionEntry[] tEquipment, ctEquipment;

	public EquipmentSection(String id, Translation translation, EquipmentSectionEntry[] tEquipment, EquipmentSectionEntry[] ctEquipment)
	{
		this.id = id;
		this.translation = translation;
		this.tEquipment = tEquipment;
		this.ctEquipment = ctEquipment;
		this.icon = initIcon();
	}

	public EquipmentSection(String id, Translation translation, EquipmentSectionEntry... equipment)
	{
		this(id, translation, equipment, equipment);
	}

	public static EquipmentSection getById(Collection<EquipmentSection> categories, String id)
	{
		for(EquipmentSection cat : categories)
			if(cat.id.equals(id))
				return cat;

		return null;
	}

	public static EquipmentSection getByName(Collection<EquipmentSection> categories, String name)
	{
		for(EquipmentSection cat : categories)
			if(cat.getName().equals(name))
				return cat;

		return null;
	}

	public static EquipmentSection getByInventory(Collection<EquipmentSection> categories, Inventory inv)
	{
		String title = inv.getTitle();
		int prefixLength = MSConstant.INVENTORY_NAME_PREFIX.length();

		if(title.length() <= prefixLength)
			return null;

		String name = title.substring(prefixLength);

		return getByName(categories, name);
	}

	public static EquipmentSection getByIcon(Collection<EquipmentSection> categories, ItemStack icon)
	{
		for(EquipmentSection cat : categories)
			if(cat.getIcon().equals(icon))
				return cat;

		return null;
	}

	private ItemStack initIcon()
	{
		ItemStack icon = new ItemStack(Material.FIREWORK_CHARGE);
		ItemMeta im = icon.getItemMeta();

		im.setDisplayName(ChatColor.BOLD + getName());
		icon.setItemMeta(im);

		LoreAttributes.TEMP.clear();
		LoreAttributes.TEMP.put("Type", "SHOP_SECTION_" + id);
		LoreAttributes.TEMP.apply(icon);

		return icon;
	}

	public ItemStack getIcon()
	{
		return icon.clone();
	}

	public void openInventory(MSPlayer msPlayer)
	{
		String name = getName();
		Player player = msPlayer.getPlayer();
		EquipmentSectionEntry[] entries = getEntries(msPlayer);
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
			EquipmentSectionEntry entry = entries[i];
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			Equipment equipment = invContainer.getEquippedEquipment(entry);
			Integer price = equipment.getPrice(msPlayer);
			ItemStack item = equipment.newItemStack(msPlayer);
			
			if(price != null)
			{
				EquipmentCustomization customization = EquipmentCustomization.builder()
						.name(Translation.GAME_SHOP_ICONPRICE.getMessage("{1}", price)).build();
				
				customization.apply(equipment, item, msPlayer);
			}
			
			inv.setItem(index, item);
		}
		
		player.openInventory(inv);
	}
	
	public EquipmentSectionEntry getEntry(MSPlayer msPlayer, int x, int y)
	{
		EquipmentSectionEntry[] entries = getEntries(msPlayer);
		int width = (int) Math.ceil(entries.length / 2D);
		int startX = 4 - (width / 2);
		
		if(x < startX || x >= startX + width)
			return null;
		
		x -= startX;
		y -= 1;
		
		int index = x + y * width;
		
		if(index < 0 || index >= entries.length)
			return null;
		
		return entries[index];
	}
	
	public EquipmentSectionEntry getEntry(MSPlayer msPlayer, int inventoryIndex)
	{
		return getEntry(msPlayer, inventoryIndex % MSConstant.INVENTORY_WIDTH, inventoryIndex / MSConstant.INVENTORY_WIDTH);
	}
	
	public EquipmentSectionEntry[] getEntries(MSPlayer msPlayer)
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
			return null;
		
		Game game = (Game) scene;
		
		if(!(game instanceof TeamGame))
			return getEntries();
		else
		{
			TeamGame teamGame = (TeamGame) game;
			Team team = teamGame.getTeam(msPlayer);
			
			return getEntries(team);
		}
	}
	
	public EquipmentSectionEntry[] getEntries(Team team)
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
	
	public EquipmentSectionEntry[] getEntries()
	{
		HashSet<EquipmentSectionEntry> set = Sets.newHashSet();

		Collections.addAll(set, tEquipment);
		Collections.addAll(set, ctEquipment);
		
		return set.toArray(new EquipmentSectionEntry[set.size()]);
	}

	public EquipmentSectionEntry[] getTerroristsEquipment()
	{
		return tEquipment;
	}

	public EquipmentSectionEntry[] getCounterTerroristsEquipment()
	{
		return ctEquipment;
	}
	
	private static EquipmentSectionEntry e(Equipment... equipment)
	{
		return EquipmentSectionEntry.valueOf(equipment);
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
