package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class G3SG1 extends ScopeGunType
{
	private static final G3SG1 instance = new G3SG1();

	private G3SG1()
	{
		super("G3SG1", "G3SG/1", "g3sg1", null, true, false, 4.65F, 1.650F, 80, 0.980F, 0.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 215, 20, 90, 5000, 8192, true, 0.50F, 0.65F, 0.30F, 19.35F, 25.80F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0.388808F, 0.544331F, 30, 30F, 4, 1, true, 0.30F, 1.50F, 2.00F, 18.61F, 150.48F, 0.873F, 0.262F, 116.390F, 0, 0F, 0, 0.250F, 1);
	}

	public static G3SG1 getInstance()
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
