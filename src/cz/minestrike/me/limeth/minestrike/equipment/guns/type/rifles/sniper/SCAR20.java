package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class SCAR20 extends ScopeGunType
{
	private static final SCAR20 instance = new SCAR20();

	private SCAR20()
	{
		super("SCAR_20", "SCAR-20", "scar20", null, true, false, 3.11F, 1.650F, 80, 0.980F, 0.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 20, 90, 5000, 8192, true, 0.35F, 0.40F, 0.30F, 19.35F, 25.80F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0.388808F, 0.544331F, 30, 31F, 4, 1, true, 0.30F, 1.50F, 2.00F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0, 0F, 0, 1);
	}

	public static SCAR20 getInstance()
	{
		return instance;
	}

	@Override
	protected boolean initFrameShown()
	{
		return true;
	}

	@Override
	protected int[] initZoomCycle()
	{
		return new int[] {2, 4};
	}
}
