package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class AK47 extends SimpleGunType
{
	private static final AK47 instance = new AK47();

	private AK47()
	{
		super("AK_47", "AK-47", "ak47", null, true, false, 2.5F, 1.55F, 36, 0.98F, 0.1F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 30, 90, 2700, 8192, true, 0.40F, 0.55F, 0.6F, 4.81F, 6.41F, 7.8F, 175.06F, 0.807F, 0.242F, 140F, 0.381571F, 0.46F, 70, 30, 0, 1);
	}

	public static AK47 getInstance()
	{
		return instance;
	}
}
