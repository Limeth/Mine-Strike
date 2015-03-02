package cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class SawedOff extends SimpleGunType
{
	private static final SawedOff instance = new SawedOff();

	private SawedOff()
	{
		super("SAWEDOFF", "Sawed-Off", "sawedoff", null, true, true, 2F /*TODO*/, 1.5F, 30, 0.45F, 0.85F, 1, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 210, 7, 32, 1200, 750, false, 0.4F, 0.45F, 60, 5.25F, 7, 9.72F, 16.8F, 0.36F, 0.108F, 36, 0.328941F, 0.460517F, 20, 143, 22, 9);
	}

	public static SawedOff getInstance()
	{
		return instance;
	}
}
