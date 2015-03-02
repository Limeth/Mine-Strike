package cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class Nova extends SimpleGunType
{
	private static final Nova instance = new Nova();

	private Nova()
	{
		super("NOVA", "Nova", "nova", null, true, true, 2F /*TODO*/, 1, 26, 0.7F, 0.88F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 220, 8, 32, 1200, 3000, true, 0.4F, 0.45F, 40, 5.25F, 7, 9.72F, 36.75F, 0.788F, 0.236F, 78.75F, 0.328941F, 0.460517F, 20, 143, 22, 9);
	}

	public static Nova getInstance()
	{
		return instance;
	}
}
