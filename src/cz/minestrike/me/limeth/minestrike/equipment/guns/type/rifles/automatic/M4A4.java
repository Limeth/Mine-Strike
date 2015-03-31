package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class M4A4 extends SimpleGunType
{
	private static final M4A4 instance = new M4A4();

	private M4A4()
	{
		super("M4A4", "M4A4", "m4a1", "m4a4", true, false, 3.15F, 1.400F, 33, 0.970F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 225, 30, 90, 3100, 8192, true, 0.40F, 0.55F, 0.60F, 3.68F, 4.90F, 7.00F, 137.88F, 0.640F, 0.192F, 110.994F, 0.302625F, 0.423676F, 70, 23F, 0, 1);//, true, 0.45F, 3.68F, 4.90F, 6.34F, 122.00F, 0.656F, 0.197F, 113.672F, 0, 0F, 0, 1);
	}

	public static M4A4 getInstance()
	{
		return instance;
	}

	@Override
	public String getSoundShooting(Gun gun)
	{
		return super.getSoundShooting(gun) + "_unsil";
	}
}
