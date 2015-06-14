package cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.SilencableGunType;
import cz.minestrike.me.limeth.minestrike.scene.games.MoneyAward;
import org.bukkit.block.Block;

/**
 * @author Limeth
 */
public class USPS extends SilencableGunType
{
	private static final USPS instance = new USPS();

	private USPS()
	{
		super("USP_S", "USP-S", "usp", null, false, false, 2.3F /*TODO*/, 1.01F, 35, 0.91F, 0.17F, 1, MoneyAward.KILL_OTHER_COMPETITIVE.getAmount(), MoneyAward.KILL_OTHER_CASUAL.getAmount(), 240, 12, 24, 200, 4096, false, 0.5F, 0.65F, 2.5F, 3.68F, 4.9F, 71, 13.87F, 0.638F, 0.191F, 138.32F, 0.291277F, 0.349532F, 0, 29, 0, 1, false, 1.5F, 3.68F, 4.9F, 52F, 13.87F, 0.66F, 0.198F, 119.9F, 0, 23F, 0, 0.17F, 1);
	}

	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return super.rightClick(msPlayer, clickedBlock);
	}

	public static USPS getInstance()
	{
		return instance;
	}
}
