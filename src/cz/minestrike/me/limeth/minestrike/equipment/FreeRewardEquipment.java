package cz.minestrike.me.limeth.minestrike.equipment;

import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 * @author Limeth
 */
public final class FreeRewardEquipment
{
	//TODO Load from a config file

	public static final Gun SG_556_JUNGLE_CAMO = new Gun(GunType.SG_556, ChatColor.DARK_GREEN + "Jungle Camo", "JUNGLE_CAMO", Color.fromRGB(0, 127, 0));
	public static final Gun AUG_DESERT = new Gun(GunType.AUG, ChatColor.WHITE + "Desert", "DESERT", Color.fromRGB(255, 255, 127));

	public static final Equipment[] VALUES = {SG_556_JUNGLE_CAMO, AUG_DESERT};

	private FreeRewardEquipment()
	{
	}
}
