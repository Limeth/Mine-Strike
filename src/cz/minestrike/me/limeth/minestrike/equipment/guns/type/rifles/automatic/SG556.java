package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class SG556 extends ScopeGunType
{
	private static final SG556 instance = new SG556();

	private SG556()
	{
		super("SG_556", "SG 556", "sg556", null, true, false, 2.8F, 2.000F, 30, 0.980F, 0.090F, 2, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 210, 30, 90, 3000, 8192, true, 0.40F, 0.55F, 0.50F, 2.84F, 3.78F, 6.68F, 136.01F, 0.627F, 0.188F, 83.660F, 0.379204F, 0.452886F, 60, 28F, 2, 1, true, 0.30F, 1.04F, 2.18F, 6.68F, 136.01F, 0.627F, 0.188F, 138.758F, 0, 19F, 0, 1);
	}

	public static SG556 getInstance()
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
