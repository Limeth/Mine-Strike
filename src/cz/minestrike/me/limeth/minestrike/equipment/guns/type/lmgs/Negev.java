package cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Negev extends SimpleGunType
{
	private static final Negev instance = new Negev();

	private Negev()
	{
		super("NEGEV", "Negev", "negev", null, true, false, 5.73F, 1.500F, 35, 0.970F, 0.060F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 195, 150, 200, 5700, 8192, true, 0.40F, 0.55F, 2.00F, 7.63F, 10.17F, 3.37F, 159.14F, 1.364F, 0.409F, 136.430F, 0.624987F, 0.874982F, 50, 22F, 2, 1);
	}

	public static Negev getInstance()
	{
		return instance;
	}
}
