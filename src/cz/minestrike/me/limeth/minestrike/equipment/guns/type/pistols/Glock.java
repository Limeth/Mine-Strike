package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.BurstFireGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Glock extends BurstFireGunType
{
	private static final Glock instance = new Glock();

	private Glock()
	{
		super("GLOCK", "Glock-18", "glock18", null, false, false, 2.3F, 0.94F, 28, 0.9F, 0.15F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 20, 120, 200, 4096, false, 0.5F, 0.65F, 2, 4.2F, 5.6F, 56, 12, 0.616F, 0.185F, 137, 0.27631F, 0.331572F, 20, 18, 0, 1, false, 15F, 3F, 5.6F, 45F, 12.95F, 0.15F, 0.185F, 119.25F, 20, 30F, 5, 3);
	}

	public static Glock getInstance()
	{
		return instance;
	}
}
