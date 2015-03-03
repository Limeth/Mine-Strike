package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class AUG extends ScopeGunType
{
	private static final AUG instance = new AUG();

	private AUG()
	{
		super("AUG", "AUG", "aug", null, true, false, 3.8F, 1.800F, 28, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 220, 30, 90, 3300, 8192, true, 0.40F, 0.55F, 0.50F, 2.88F, 3.85F, 6.16F, 135.45F, 0.693F, 0.208F, 110.040F, 0.305520F, 0.429727F, 60, 26F, 0, 1, true, 0.30F, 1.01F, 2.12F, 6.16F, 105.45F, 0.693F, 0.208F, 100.040F, 0, 18F, 0, 0.090F, 1);
	}

	public static AUG getInstance()
	{
		return instance;
	}

	@Override
	protected boolean initFrameShown()
	{
		return false;
	}

	@Override
	protected int[] initZoomCycle()
	{
		return new int[] {2};
	}
}
