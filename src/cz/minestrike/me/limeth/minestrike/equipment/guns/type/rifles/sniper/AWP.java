package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class AWP extends ScopeGunType
{
	private static final AWP instance = new AWP();

	private AWP()
	{
		super("AWP", "AWP", "awp", null, true, false, 3.7F, 1.950F, 115, 0.990F, 1.455F, 3, MoneyAward.KILL_AWP.getAmount(), MoneyAward.KILL_AWP.getAmount(), 200, 10, 30, 4750, 8192, false, 0.35F, 0.40F, 0.20F, 60.60F, 80.80F, 53.85F, 176.48F, 1.024F, 0.307F, 136.500F, 0.246710F, 0.345390F, 20, 78F, 15, 1, false, 0.20F, 1.50F, 2.00F, 53.85F, 176.48F, 1.024F, 0.100F, 136.500F, 0, 0F, 0, 1);
	}

	public static AWP getInstance()
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
