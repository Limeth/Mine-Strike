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
	public static final Gun M249_DESERT = new Gun(GunType.M249, ChatColor.WHITE + "Desert", "DESERT", Color.fromRGB(255, 255, 127));
	public static final Gun ELITE_PINE = new Gun(GunType.ELITE, ChatColor.DARK_GRAY + "Pine", "PINE", Color.fromRGB(0, 127, 0));
	public static final Gun UMP45_GRAPEFRUIT = new Gun(GunType.UMP45, ChatColor.GOLD + "Grapefruit", "GRAPEFRUIT", Color.fromRGB(253, 89, 86));
	public static final Gun SSG_08_CLOUD = new Gun(GunType.SSG_08, ChatColor.DARK_BLUE + "Cloud", "CLOUD", Color.fromRGB(0, 0, 127));
	public static final Gun MAG7_METEORITE = new Gun(GunType.MAG7, ChatColor.DARK_PURPLE + "Meteorite", "METEORITE", Color.PURPLE);
	public static final Gun NOVA_FABRIC = new Gun(GunType.NOVA, ChatColor.GRAY + "Fabric", "FABRIC", Color.GRAY);
	public static final Gun P250_RUSTED = new Gun(GunType.P250, ChatColor.GOLD + "Rusted", "RUSTED", Color.ORANGE);
	public static final Gun CZ75_SEDIMENT = new Gun(GunType.CZ75, ChatColor.YELLOW + "Sediment", "SEDIMENT", Color.YELLOW);
	public static final Gun FIVESEVEN_CARP = new Gun(GunType.FIVESEVEN, ChatColor.DARK_GREEN + "Carp", "CARP", Color.fromRGB(0, 127, 0));
	public static final Gun M4A1_S_MINT = new Gun(GunType.M4A1_S, ChatColor.GREEN + "Mint", "MINT", Color.GREEN);

	public static final Equipment[] VALUES = {
			SG_556_JUNGLE_CAMO, AUG_DESERT, M249_DESERT, ELITE_PINE, UMP45_GRAPEFRUIT, SSG_08_CLOUD, MAG7_METEORITE,
			NOVA_FABRIC, P250_RUSTED, CZ75_SEDIMENT, FIVESEVEN_CARP, M4A1_S_MINT
	};

	private FreeRewardEquipment()
	{
	}
}
