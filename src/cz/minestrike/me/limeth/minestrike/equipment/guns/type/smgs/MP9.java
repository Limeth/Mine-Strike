package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class MP9 extends SimpleGunType
{
	private static final MP9 instance = new MP9();

	private MP9()
	{
		super("MP9", "MP9", "mp9", null, true, false, 2.25F, 1.000F, 26, 0.830F, 0.070F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 30, 120, 1250, 3600, true, 0.45F, 0.60F, 0.75F, 10.50F, 14.00F, 3.70F, 19.04F, 0.186F, 0.056F, 148.913F, 0.184207F, 0.257890F, 70, 19F, 1, 1);
	}

	public static MP9 getInstance()
	{
		return instance;
	}
}
