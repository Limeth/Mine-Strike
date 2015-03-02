package cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class MAG7 extends SimpleGunType
{
	private static final MAG7 instance = new MAG7();

	private MAG7()
	{
		super("MAG7", "MAG-7", "mag7", null, true, false, 2.5F, 1.5F, 28, 0.45F, 0.85F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 225, 5, 32, 1800, 1500, true, 0.4F, 0.45F, 40, 5.25F, 7, 11.19F, 15.99F, 0.343F, 0.103F, 134.26F, 0.285521F, 0.399729F, 20, 165, 25, 9);
	}

	public static MAG7 getInstance()
	{
		return instance;
	}
}
