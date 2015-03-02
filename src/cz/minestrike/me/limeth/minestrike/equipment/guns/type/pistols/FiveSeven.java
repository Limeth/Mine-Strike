package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class FiveSeven extends SimpleGunType
{
	private static final FiveSeven instance = new FiveSeven();

	private FiveSeven()
	{
		super("FIVESEVEN", "Five-SeveN", "fiveseven", null, false, false, 2.3F, 1.823F, 32, 0.81F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 20, 100, 500, 4096, false, 0.5F, 0.65F, 2, 6.83F, 9.1F, 32.45F, 13.41F, 0.633F, 0.19F, 138, 0.273844F, 0.332613F, 5, 25, 4, 1);
	}

	public static FiveSeven getInstance()
	{
		return instance;
	}
}
