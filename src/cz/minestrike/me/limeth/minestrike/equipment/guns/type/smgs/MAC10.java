package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class MAC10 extends SimpleGunType
{
	private static final MAC10 instance = new MAC10();

	private MAC10()
	{
		super("MAC10", "MAC-10", "mac10", null, true, false, 2.6F, 0.950F, 29, 0.800F, 0.075F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 30, 100, 1050, 3600, true, 0.45F, 0.60F, 1.00F, 9.98F, 13.30F, 2.76F, 24.99F, 0.228F, 0.069F, 34.260F, 0.285521F, 0.399729F, 70, 18F, 1, 1);
	}

	public static MAC10 getInstance()
	{
		return instance;
	}
}
