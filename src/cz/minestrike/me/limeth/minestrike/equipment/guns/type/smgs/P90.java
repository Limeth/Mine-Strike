package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class P90 extends SimpleGunType
{
	private static final P90 instance = new P90();

	private P90()
	{
		super("P90", "P90", "p90", null, true, false, 3.4F, 1.300F, 26, 0.860F, 0.070F, 1, MoneyAward.KILL_P90.getAmount(), MoneyAward.KILL_P90.getAmount(), 230, 50, 100, 2350, 3700, true, 0.45F, 0.60F, 1.00F, 10.24F, 13.65F, 2.85F, 31.00F, 0.650F, 0.082F, 132.170F, 0.265784F, 0.372098F, 70, 16F, 1, 1);
	}

	public static P90 getInstance()
	{
		return instance;
	}
}
