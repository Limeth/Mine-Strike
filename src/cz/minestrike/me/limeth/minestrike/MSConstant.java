package cz.minestrike.me.limeth.minestrike;

import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class MSConstant
{
	static
	{
		ItemStack backgroundItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta backgroundItemIM = backgroundItem.getItemMeta();
		
		backgroundItemIM.setDisplayName("");
		backgroundItem.setItemMeta(backgroundItemIM);
		
		ITEM_BACKGROUND = backgroundItem;
	}

	public static final Random RANDOM = new Random();
	public static final String CONSOLE_PREFIX = ChatColor.BLUE + "Mine" + ChatColor.GRAY + "-" + ChatColor.GOLD + "Strike" + ChatColor.GRAY + ": " + ChatColor.RESET,
			INVENTORY_NAME_PREFIX = ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Mine-Strike" + ChatColor.BLACK + " | " + ChatColor.BOLD,
			PERMISSION_ADVANCED_TEAM_JOIN = "MineStrike.advancedTeamJoin",
			PERMISSION_VOTE_STRENGTH = "MineStrike.voteStrength";
	public static final int INVENTORY_WIDTH = 9;
	public static final float CS_NORMAL_SPEED = 250, CS_MAX_HEALTH = 100,
			CS_UNITS_TO_METERS_MODIFIER = 0.01905F, CS_UNITS_TO_METERS_PER_TICK_MODIFIER = CS_UNITS_TO_METERS_MODIFIER / 20,
			MOVEMENT_SPEED_DEFAULT = 250 * CS_UNITS_TO_METERS_PER_TICK_MODIFIER, BOMB_POWER = 100F;
	public static final ItemStack
			ITEM_BACKGROUND,
			QUIT_SERVER_ITEM = customTextured(Translation.BUTTON_QUITGAME, "BUTTON_QUITGAME"),
			QUIT_MENU_ITEM = customTextured(Translation.BUTTON_TEAMCHANGE, "BUTTON_TEAMCHANGE");
	public static final Material
			MATERIAL_ARROW_UP = Material.GOLD_RECORD,
			MATERIAL_ARROW_DOWN = Material.GREEN_RECORD,
			MATERIAL_ARROW_LEFT = Material.RECORD_3,
			MATERIAL_ARROW_RIGHT = Material.RECORD_4,
			MATERIAL_CONFIRM = Material.RECORD_5,
			MATERIAL_DENY = Material.RECORD_6,
			MATERIAL_BACK = Material.RECORD_7;

	private static ItemStack customTextured(Translation translation, String type)
	{
		ItemStack is = new ItemStack(Material.FIREWORK_CHARGE);
		ItemMeta im = is.getItemMeta();

		im.setDisplayName(translation.getMessage());
		is.setItemMeta(im);

		LoreAttributes.TEMP.clear();
		LoreAttributes.TEMP.put("Type", type);
		LoreAttributes.TEMP.apply(is);

		return is;
	}
}
