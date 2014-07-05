package cz.minestrike.me.limeth.minestrike;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MSConstant
{
	public static final boolean DEBUG = true;
	public static final Random RANDOM = new Random();
	public static final String CONSOLE_PREFIX = ChatColor.BLUE + "Mine" + ChatColor.GRAY + "-" + ChatColor.GOLD + "Strike" + ChatColor.GRAY + ": " + ChatColor.RESET,
			INVENTORY_NAME_PREFIX = ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Mine-Strike" + ChatColor.BLACK + " | " + ChatColor.BOLD;
	public static final int INVENTORY_WIDTH = 9;
	public static final float CS_NORMAL_SPEED = 250, CS_MAX_HEALTH = 100,
			CS_UNITS_TO_METERS_MODIFIER = 0.01905F, CS_UNITS_TO_METERS_PER_TICK_MODIFIER = CS_UNITS_TO_METERS_MODIFIER / 20,
			MOVEMENT_SPEED_DEFAULT = 250 * CS_UNITS_TO_METERS_PER_TICK_MODIFIER, BOMB_POWER = 100F;
	public static final ItemStack
			BACKGROUND_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15),
			QUIT_SERVER_ITEM = new ItemStack(Material.IRON_DOOR),
			QUIT_MENU_ITEM = new ItemStack(Material.WOOD_DOOR);
}
