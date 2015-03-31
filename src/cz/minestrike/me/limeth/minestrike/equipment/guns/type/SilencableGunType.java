package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.SilencerToggle;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * @author Limeth
 */
public class SilencableGunType extends DoubleModeGunType
{
	public static final String  ATTRIBUTE_SUFFIX_SILENCED = " | SILENCED";

	public SilencableGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets, boolean automaticAlt, float spreadAlt, float inaccuracySneakAlt, float inaccuracyStandAlt, float inaccuracyFireAlt, float inaccuracyMoveAlt, float inaccuracyJumpAlt, float inaccuracyLandAlt, float inaccuracyLadderAlt, int recoilAngleVarianceAlt, float recoilMagnitudeAlt, int recoilMagnitudeVarianceAlt, float cycleTimeAlt, int bulletsAlt)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets, automaticAlt, spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt, recoilAngleVarianceAlt, recoilMagnitudeAlt, recoilMagnitudeVarianceAlt, cycleTimeAlt, bulletsAlt);
	}

	public SilencableGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets);
	}

	@Override
	public void initialize(Gun gun)
	{
		setSecondMode(gun, true);
	}

	@Override
	public String getSoundShooting(Gun gun)
	{
		String sound = super.getSoundShooting(gun);

		if(!isSecondMode(gun))
			sound += "_unsil";

		return sound;
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block block)
	{
		if(msPlayer.hasGunTask())
			return true;

		Gun gun = msPlayer.getEquipmentInHand();
		boolean silenced = isSecondMode(gun);
		String sound = super.getSoundShooting(gun) + "_silencer_" + (silenced ? "off" : "on");

		msPlayer.setGunTask(new SilencerToggle(msPlayer, gun, sound).startLoop());

		return true;
	}

	@Override
	public void apply(ItemStack itemStack, MSPlayer msPlayer, Gun gun)
	{
		if(!isSecondMode(gun))
			return;

		LoreAttributes.TEMP.clear();
		LoreAttributes.extract(itemStack, LoreAttributes.TEMP);

		String type = LoreAttributes.TEMP.get("Type");

		LoreAttributes.TEMP.put("Type", type + ATTRIBUTE_SUFFIX_SILENCED);
		LoreAttributes.TEMP.apply(itemStack);
	}
}
