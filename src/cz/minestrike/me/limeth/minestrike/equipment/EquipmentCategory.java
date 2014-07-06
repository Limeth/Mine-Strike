package cz.minestrike.me.limeth.minestrike.equipment;

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
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

public enum EquipmentCategory
{
	PISTOLS("Pistols", new ItemStack(Material.FIREWORK_CHARGE), new EquipmentType[] {
				GunType.GLOCK, GunType.P250, GunType.DEAGLE, GunType.ELITE, GunType.TEC9, GunType.CZ75
			}, new EquipmentType[]  {//TODO add usp-s support
				GunType.P2000, GunType.P250, GunType.DEAGLE, GunType.ELITE, GunType.FIVESEVEN, GunType.CZ75
			}), //TODO change to custom textures
	HEAVY("Heavy", new ItemStack(Material.FIREWORK_CHARGE), new EquipmentType[] {
				GunType.NOVA, GunType.XM1014, GunType.SAWEDOFF, GunType.M249, GunType.NEGEV
			}, new EquipmentType[] {
				GunType.NOVA, GunType.XM1014, GunType.MAG7, GunType.M249, GunType.NEGEV
			}),
	SMGS("SMGs", new ItemStack(Material.FIREWORK_CHARGE), new EquipmentType[] {
				GunType.MAC10, GunType.MP7, GunType.UMP45, GunType.BIZON, GunType.P90
			}, new EquipmentType[] {
				GunType.MP9, GunType.MP7, GunType.UMP45, GunType.BIZON, GunType.P90
			}),
	RIFLES("Rifles", new ItemStack(Material.FIREWORK_CHARGE), new EquipmentType[] {
				GunType.GALIL_AR, GunType.AK_47, GunType.SSG_08, GunType.SG_556, GunType.AWP, GunType.G3SG1
			}, new EquipmentType[] {
				GunType.FAMAS, GunType.M4A4, GunType.SSG_08, GunType.AUG, GunType.AWP, GunType.SCAR_20
			}),
	GEAR("Gear", new ItemStack(Material.LEATHER_CHESTPLATE), new EquipmentType[] {
				
			}, new EquipmentType[] {
				
			}),
	GRENADES("Grenades", new ItemStack(Material.POTION), new EquipmentType[] {
				GrenadeType.INCENDIARY, GrenadeType.DECOY, GrenadeType.EXPLOSIVE, GrenadeType.FLASH, GrenadeType.SMOKE
			});
	
	private final String name;
	private final ItemStack icon;
	private final EquipmentType[] tEquipment, ctEquipment;
	
	private EquipmentCategory(String name, ItemStack icon, EquipmentType[] tEquipment, EquipmentType[] ctEquipment)
	{
		this.name = name;
		this.icon = icon;
		this.tEquipment = tEquipment;
		this.ctEquipment = ctEquipment;
		
		ItemMeta iconMeta = icon.getItemMeta();
		
		iconMeta.setDisplayName(ChatColor.BOLD + name);
		icon.setItemMeta(iconMeta);
	}
	
	private EquipmentCategory(String name, ItemStack icon, EquipmentType... equipment)
	{
		this(name, icon, equipment, equipment);
	}
	
	public static EquipmentCategory getByName(String name)
	{
		for(EquipmentCategory cat : values())
			if(cat.name.equals(name))
				return cat;
		
		return null;
	}
	
	public static EquipmentCategory getByInventory(Inventory inv)
	{
		String title = inv.getTitle();
		int prefixLength = MSConstant.INVENTORY_NAME_PREFIX.length();
		
		if(title.length() <= prefixLength)
			return null;
		
		String name = title.substring(prefixLength);
		
		return getByName(name);
	}
	
	public static EquipmentCategory getByIcon(ItemStack icon)
	{
		for(EquipmentCategory cat : values())
			if(cat.getIcon().equals(icon))
				return cat;
		
		return null;
	}
	
	public ItemStack getIcon()
	{
		return icon;
	}

	public void openInventory(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		EquipmentType[] equipment = getEquipment(msPlayer);
		Inventory inv = Bukkit.createInventory(player, PlayerUtil.INVENTORY_WIDTH * 4, MSConstant.INVENTORY_NAME_PREFIX + name);
		
		for(int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, MSConstant.BACKGROUND_ITEM);
		
		int width = (int) Math.ceil(equipment.length / 2D);
		int x = 4 - (width / 2);
		
		for(int i = 0; i < equipment.length; i++)
		{
			int relX = i % width;
			int relY = i / width;
			int absX = x + relX;
			int absY = 1 + relY;
			int index = absX + absY * MSConstant.INVENTORY_WIDTH;
			ItemStack item = equipment[i].newItemStack(msPlayer);
			
			inv.setItem(index, item);
		}
		
		player.openInventory(inv);
	}
	
	public EquipmentType getEquipment(MSPlayer msPlayer, int x, int y)
	{
		EquipmentType[] equipment = getEquipment(msPlayer);
		int width = (int) Math.ceil(equipment.length / 2D);
		int startX = 4 - (width / 2);
		
		x -= startX;
		y -= 1;
		
		int index = x + y * width;
		
		if(index < 0 || index >= equipment.length)
			return null;
		
		return equipment[index];
	}
	
	public EquipmentType getEquipment(MSPlayer msPlayer, int inventoryIndex)
	{
		return getEquipment(msPlayer, inventoryIndex % MSConstant.INVENTORY_WIDTH, inventoryIndex / MSConstant.INVENTORY_WIDTH);
	}
	
	public EquipmentType[] getEquipment(MSPlayer msPlayer)
	{
		Game<?, ?, ?, ?> game = msPlayer.getGame();
		
		if(!(game instanceof TeamGame))
			return getEquipment();
		else
		{
			TeamGame<?, ?, ?, ?> teamGame = (TeamGame<?, ?, ?, ?>) game;
			Team team = teamGame.getTeam(msPlayer);
			
			return getEquipment(team);
		}
	}
	
	public EquipmentType[] getEquipment(Team team)
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
	
	public EquipmentType[] getEquipment()
	{
		HashSet<EquipmentType> set = new HashSet<EquipmentType>();
		
		for(EquipmentType e : tEquipment)
			set.add(e);
		
		for(EquipmentType e : ctEquipment)
			set.add(e);
		
		return set.toArray(new EquipmentType[set.size()]);
	}

	public EquipmentType[] getTerroristsEquipment()
	{
		return tEquipment;
	}

	public EquipmentType[] getCounterTerroristsEquipment()
	{
		return ctEquipment;
	}

	public String getName()
	{
		return name;
	}
}
