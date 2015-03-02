package cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.ScopeGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class SSG08 extends ScopeGunType
{
	private static final SSG08 instance = new SSG08();

	private SSG08()
	{
		super("SSG_08", "SSG 08", "ssg08", null, true, false, 3.6F, 1.700F, 88, 0.980F, 1.250F, 3, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 230, 10, 90, 2000, 8192, false, 0.35F, 0.40F, 0.30F, 23.78F, 31.70F, 22.92F, 123.45F, 0.716F, 0.215F, 95.490F, 0.055783F, 0.142096F, 20, 33F, 15, 1, false, 0.30F, 3.00F, 4.00F, 22.92F, 123.45F, 0.716F, 0.215F, 95.490F, 0, 0F, 0, 1);
	}

	public static SSG08 getInstance()
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
