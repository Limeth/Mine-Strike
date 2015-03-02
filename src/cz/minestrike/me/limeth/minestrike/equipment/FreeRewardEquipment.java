package cz.minestrike.me.limeth.minestrike.equipment;

import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.M249;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.CZ75;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.Elite;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.FiveSeven;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.P250;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.AUG;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.M4A1S;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.SG556;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.SSG08;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.MAG7;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.Nova;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.UMP45;
import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 * @author Limeth
 */
public final class FreeRewardEquipment
{
	//TODO Load from a config file

	public static final Gun SG_556_JUNGLE_CAMO = new Gun(SG556.getInstance(), ChatColor.DARK_GREEN + "Jungle Camo", "JUNGLE_CAMO", Color.fromRGB(0, 127, 0));
	public static final Gun AUG_DESERT         = new Gun(AUG.getInstance(), ChatColor.WHITE + "Desert", "DESERT", Color.fromRGB(255, 255, 127));
	public static final Gun M249_DESERT        = new Gun(M249.getInstance(), ChatColor.WHITE + "Desert", "DESERT", Color.fromRGB(255, 255, 127));
	public static final Gun ELITE_PINE         = new Gun(Elite.getInstance(), ChatColor.DARK_GRAY + "Pine", "PINE", Color.fromRGB(0, 127, 0));
	public static final Gun UMP45_GRAPEFRUIT   = new Gun(UMP45.getInstance(), ChatColor.GOLD + "Grapefruit", "GRAPEFRUIT", Color.fromRGB(253, 89, 86));
	public static final Gun SSG_08_CLOUD       = new Gun(SSG08.getInstance(), ChatColor.DARK_BLUE + "Cloud", "CLOUD", Color.fromRGB(0, 0, 127));
	public static final Gun MAG7_METEORITE     = new Gun(MAG7.getInstance(), ChatColor.DARK_PURPLE + "Meteorite", "METEORITE", Color.PURPLE);
	public static final Gun NOVA_FABRIC        = new Gun(Nova.getInstance(), ChatColor.GRAY + "Fabric", "FABRIC", Color.GRAY);
	public static final Gun P250_RUSTED        = new Gun(P250.getInstance(), ChatColor.GOLD + "Rusted", "RUSTED", Color.ORANGE);
	public static final Gun CZ75_SEDIMENT      = new Gun(CZ75.getInstance(), ChatColor.YELLOW + "Sediment", "SEDIMENT", Color.YELLOW);
	public static final Gun FIVESEVEN_CARP     = new Gun(FiveSeven.getInstance(), ChatColor.DARK_GREEN + "Carp", "CARP", Color.fromRGB(0, 127, 0));
	public static final Gun M4A1_S_MINT        = new Gun(M4A1S.getInstance(), ChatColor.GREEN + "Mint", "MINT", Color.GREEN);

	public static final Equipment[] VALUES = {SG_556_JUNGLE_CAMO, AUG_DESERT, M249_DESERT, ELITE_PINE, UMP45_GRAPEFRUIT, SSG_08_CLOUD, MAG7_METEORITE, NOVA_FABRIC, P250_RUSTED, CZ75_SEDIMENT, FIVESEVEN_CARP, M4A1_S_MINT};

	private FreeRewardEquipment()
	{
	}
}
