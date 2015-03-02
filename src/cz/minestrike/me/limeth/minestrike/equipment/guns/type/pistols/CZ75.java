package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class CZ75 extends SimpleGunType
{
	private static final CZ75 instance = new CZ75();

	private CZ75()
	{
		super("CZ75", "CZ75-Auto", "cz75a", null, false, false, 2.3F /*TODO*/, 1.553F, 35, 0.85F, 0.1F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 12, 12, 300, 4096, true, 0.5F, 0.65F, 3, 6.83F, 9.1F, 25, 13.41F, 0.633F, 0.19F, 138, 0.287823F, 0.345388F, 180, 25, 10, 1);
	}

	public static CZ75 getInstance()
	{
		return instance;
	}
}
