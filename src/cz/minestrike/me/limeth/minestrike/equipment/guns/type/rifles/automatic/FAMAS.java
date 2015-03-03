package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.BurstFireGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class FAMAS extends BurstFireGunType
{
	private static final FAMAS instance = new FAMAS();

	private FAMAS()
	{
		super("FAMAS", "FAMAS", "famas", null, true, false, 3.35F, 1.400F, 30, 0.960F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 220, 25, 90, 2250, 8192, true, 0.40F, 0.55F, 0.60F, 7.39F, 9.85F, 6.70F, 99.34F, 0.685F, 0.205F, 118.716F, 0.336177F, 0.470648F, 60, 20F, 1, 1, false, 0.60F, 3.95F, 3.69F, 3.35F, 99.34F, 0.685F, 0.205F, 118.716F, 50, 20F, 1, 0.55F, 1);
	}

	public static FAMAS getInstance()
	{
		return instance;
	}

	@Override
	public int initBurstFireSize()
	{
		return 3;
	}

	@Override
	public float initBurstFireBulletDelay()
	{
		return 1 / 20F;
	}
}
