package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class P250 extends SimpleGunType
{
	private static final P250 instance = new P250();

	private P250()
	{
		super("P250", "P250", "p250", null, false, false, 2.3F, 1.553F, 35, 0.85F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 13, 52, 300, 4096, false, 0.5F, 0.65F, 2, 6.83F, 9.1F, 52.45F, 13.41F, 0.633F, 0.190F, 138, 0.287823F, 0.345388F, 10, 26, 3, 1);
	}

	public static P250 getInstance()
	{
		return instance;
	}
}
