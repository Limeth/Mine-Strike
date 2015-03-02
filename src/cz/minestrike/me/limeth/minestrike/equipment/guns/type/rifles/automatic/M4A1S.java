package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SilencableGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class M4A1S extends SilencableGunType
{
	private static final M4A1S instance = new M4A1S();

	private M4A1S()
	{
		super("M4A1_S", "M4A1-S", "m4a1", null, true, false, 3.15F, 1.400F, 33, 0.990F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 225, 20, 40, 2900, 8192, true, 0.40F, 0.55F, 0.60F, 3.68F, 4.90F, 12.00F, 92.88F, 0.656F, 0.197F, 110.994F, 0.302625F, 0.423676F, 65, 25F, 3, 1);//, true, 0.45F, 3.68F, 4.90F, 7.00F, 122.00F, 0.656F, 0.197F, 113.672F, 65, 21F, 0, 1);
	}

	public static M4A1S getInstance()
	{
		return instance;
	}
}
