package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class TEC9 extends SimpleGunType
{
	private static final TEC9 instance = new TEC9();

	private TEC9()
	{
		super("TEC9", "Tec-9", "tec9", null, false, false, 2.7F, 1.812F, 33, 0.831F, 0.12F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 32, 120, 500, 4096, false, 0.5F, 0.65F, 2, 8.57F, 11.43F, 52.88F, 13.81F, 0.504F, 0.211F, 120.6F, 0.322362F, 0.386834F, 60, 29, 3, 1);
	}

	public static TEC9 getInstance()
	{
		return instance;
	}
}
