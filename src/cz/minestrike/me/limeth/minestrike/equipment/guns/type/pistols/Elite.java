package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Elite extends SimpleGunType
{
	private static final Elite instance = new Elite();

	private Elite()
	{
		super("ELITE", "Beretta Elite", "elite", null, false, false, 3.82F, 1.05F, 38, 0.75F, 0.12F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 30, 120, 700, 4096, false, 0.5F, 0.65F, 2, 5.25F, 7, 11.16F, 17.85F, 0.849F, 0.255F, 102, 0.437491F, 0.524989F, 20, 27, 4, 1);
	}

	public static Elite getInstance()
	{
		return instance;
	}
}
