package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class MP7 extends SimpleGunType
{
	private static final MP7 instance = new MP7();

	private MP7()
	{
		super("MP7", "MP7", "mp7", null, true, false, 3.15F, 1.050F, 29, 0.850F, 0.080F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 220, 30, 120, 1700, 3600, true, 0.45F, 0.60F, 1.00F, 9.02F, 12.03F, 2.18F, 39.86F, 0.384F, 0.115F, 57.560F, 0.312494F, 0.437491F, 70, 16F, 1, 1);
	}

	public static MP7 getInstance()
	{
		return instance;
	}
}
