package cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns;

import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SimpleGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;

/**
 * @author Limeth
 */
public class XM1014 extends SimpleGunType
{
	private static final XM1014 instance = new XM1014();

	private XM1014()
	{
		super("XM1014", "XM1014", "xm1014", null, true, true, 2F /*TODO*/, 1.6F, 20, 0.7F, 0.35F, 0, MoneyAward.KILL_SHOTGUN.getAmount(), MoneyAward.KILL_SHOTGUN.getAmount(), 215, 7, 32, 2200, 3000, true, 0.4F, 0.45F, 40, 5.25F, 7, 8.83F, 36.03F, 0.772F, 0.232F, 77.21F, 0.361835F, 0.506569F, 20, 80, 20, 6);
	}

	public static XM1014 getInstance()
	{
		return instance;
	}
}
