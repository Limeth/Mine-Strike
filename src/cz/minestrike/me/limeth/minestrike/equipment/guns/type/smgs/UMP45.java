package cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class UMP45 extends SimpleGunType
{
	private static final UMP45 instance = new UMP45();

	private UMP45()
	{
		super("UMP45", "UMP-45", "ump45", null, true, false, 3.5F, 1.100F, 35, 0.850F, 0.090F, 1, MoneyAward.KILL_SMG.getAmount(), MoneyAward.KILL_SMG.getAmount(), 230, 25, 100, 1200, 3700, true, 0.45F, 0.60F, 1.00F, 10.07F, 13.43F, 3.42F, 28.76F, 0.282F, 0.085F, 42.350F, 0.249995F, 0.349993F, 40, 23F, 1, 1);
	}

	public static UMP45 getInstance()
	{
		return instance;
	}
}
