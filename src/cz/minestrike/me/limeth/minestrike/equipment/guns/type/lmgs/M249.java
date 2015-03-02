package cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class M249 extends SimpleGunType
{
	private static final M249 instance = new M249();

	private M249()
	{
		super("M249", "M249", "m249", null, true, false, 5.73F, 1.600F, 32, 0.970F, 0.080F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 195, 100, 200, 5200, 8192, true, 0.40F, 0.55F, 2.00F, 5.34F, 7.70F, 3.56F, 156.25F, 1.328F, 0.398F, 132.810F, 0.592093F, 0.828931F, 50, 25F, 2, 1);
	}

	public static M249 getInstance()
	{
		return instance;
	}
}
