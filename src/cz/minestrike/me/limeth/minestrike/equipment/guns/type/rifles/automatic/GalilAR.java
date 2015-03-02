package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.DoubleModeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class GalilAR extends DoubleModeGunType
{
	private static final GalilAR instance = new GalilAR();

	private GalilAR()
	{
		super("GALIL_AR", "Galil AR", "galilar", null, true, false, 3.05F, 1.550F, 30, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 35, 90, 2000, 8192, true, 0.40F, 0.55F, 0.60F, 6.58F, 8.77F, 8.78F, 123.56F, 0.852F, 0.256F, 113.580F, 0.384861F, 0.538805F, 70, 21F, 1, 1, true, 0.60F, 4.84F, 7.78F, 5.85F, 106.50F, 0.852F, 0.256F, 113.580F, 0, 0F, 0, 1);
	}

	public static GalilAR getInstance()
	{
		return instance;
	}
}
