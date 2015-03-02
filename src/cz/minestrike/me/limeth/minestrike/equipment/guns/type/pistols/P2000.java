package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class P2000 extends SimpleGunType
{
	private static final P2000 instance = new P2000();

	private P2000()
	{
		super("P2000", "P2000", "hkp2000", null, false, false, 2.3F, 1.01F, 35, 0.91F, 0.17F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 13, 52, 200, 4096, false, 0.5F, 0.65F, 2, 3.68F, 4.9F, 50, 13, 0.638F, 0.191F, 138.32F, 0.291277F, 0.349532F, 0, 26, 0, 1);//, false, 1.5F, 3.68F, 4.9F, 13.15F, 13.87F, 0.66F, 0.198F, 119.9F, 0, 0F, 0, 1);
	}

	public static P2000 getInstance()
	{
		return instance;
	}
}
