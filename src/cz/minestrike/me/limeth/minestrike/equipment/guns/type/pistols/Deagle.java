package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Deagle extends SimpleGunType
{
	private static final Deagle instance = new Deagle();

	private Deagle()
	{
		super("DEAGLE", "Desert Eagle", "deagle", null, false, false, 2.3F, 1.864F, 63, 0.81F, 0.225F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 230, 7, 35, 800, 4096, false, 0.4F, 0.55F, 2, 3.78F, 7.7F, 72.23F, 48.1F, 1.966F, 0.73F, 152, 0.449927F, 0.8112F, 60, 48.2F, 18, 1);
	}

	public static Deagle getInstance()
	{
		return instance;
	}
}
