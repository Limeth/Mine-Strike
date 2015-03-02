package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Bizon extends SimpleGunType
{
	private static final Bizon instance = new Bizon();

	private Bizon()
	{
		super("BIZON","PP-Bizon", "bizon", null, true, false, 2.5F, 0.950F, 27, 0.800F, 0.080F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 240, 64, 120, 1400, 3600, true, 0.45F, 0.60F, 1.00F, 10.50F, 14.00F, 2.88F, 27.57F, 0.265F, 0.080F, 169.650F, 0.236837F, 0.331572F, 70, 18F, 1, 1);
	}

	public static Bizon getInstance()
	{
		return instance;
	}
}
