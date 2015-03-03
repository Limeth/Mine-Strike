package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

import java.util.Map;

/**
 * @author Limeth
 */
@SuppressWarnings("unused")
public class DoubleModeGunType extends SimpleGunType
{
	private static final String DATA_SECOND_MODE = "type.mode.second";
	private final float spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt, recoilMagnitudeAlt, cycleTimeAlt;
	private final int recoilAngleVarianceAlt, recoilMagnitudeVarianceAlt, bulletsAlt;
	private final boolean automaticAlt;

	protected DoubleModeGunType(String gunId, String name, String directoryName, String textureName, boolean primary,
	                            boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage,
	                            float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive,
	                            int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price,
	                            int range, boolean automatic, float flinchVelocityModifierLarge,
	                            float flinchVelocityModifierSmall, float spread, float inaccuracySneak,
	                            float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump,
	                            float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak,
	                            float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude,
	                            int recoilMagnitudeVariance, int bullets, boolean automaticAlt, float spreadAlt,
	                            float inaccuracySneakAlt, float inaccuracyStandAlt, float inaccuracyFireAlt,
	                            float inaccuracyMoveAlt, float inaccuracyJumpAlt, float inaccuracyLandAlt,
	                            float inaccuracyLadderAlt, int recoilAngleVarianceAlt, float recoilMagnitudeAlt,
	                            int recoilMagnitudeVarianceAlt, float cycleTimeAlt, int bulletsAlt)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets);
		this.spreadAlt = spreadAlt;
		this.inaccuracySneakAlt = inaccuracySneakAlt;
		this.inaccuracyStandAlt = inaccuracyStandAlt;
		this.inaccuracyFireAlt = inaccuracyFireAlt;
		this.inaccuracyMoveAlt = inaccuracyMoveAlt;
		this.inaccuracyJumpAlt = inaccuracyJumpAlt;
		this.inaccuracyLandAlt = inaccuracyLandAlt;
		this.inaccuracyLadderAlt = inaccuracyLadderAlt;
		this.recoilMagnitudeAlt = recoilMagnitudeAlt;
		this.recoilAngleVarianceAlt = recoilAngleVarianceAlt;
		this.recoilMagnitudeVarianceAlt = recoilMagnitudeVarianceAlt;
		this.cycleTimeAlt = cycleTimeAlt * 20F;
		this.bulletsAlt = bulletsAlt;
		this.automaticAlt = automaticAlt;
	}

	protected DoubleModeGunType(String gunId, String name, String directoryName,
	                        String textureName, boolean primary, boolean loadingContinuously, float reloadTime,
	                        float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration,
	                        int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity,
	                        int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall,
	                        float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove,
	                        float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand,
	                        int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		this(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration,
		     killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall,
		     spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand,
		     recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets, automatic, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire,
		     inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, cycleTime, bullets);
	}

	public static boolean isSecondMode(MSPlayer msPlayer)
	{
		return isSecondMode((Gun) msPlayer.getEquipmentInHand());
	}

	public static boolean isSecondMode(Gun gun)
	{
		Object rawMode = gun.getCustomData().get(DATA_SECOND_MODE);

		if(rawMode == null)
			return false;

		try
		{
			return (Boolean) rawMode;
		}
		catch(ClassCastException e)
		{
			return false;
		}
	}

	public static void setSecondMode(Gun gun, boolean enabled)
	{
		Map<String, Object> data = gun.getCustomData();

		if(enabled)
			data.put(DATA_SECOND_MODE, true);
		else
			data.remove(DATA_SECOND_MODE);
	}

	public static boolean toggleSecondMode(Gun gun)
	{
		boolean newValue = !isSecondMode(gun);

		setSecondMode(gun, newValue);

		return newValue;
	}

	@Override
	public float getSpread(Gun gun)
	{
		return isSecondMode(gun) ? getSpreadAlt(gun) : super.getSpread(gun);
	}

	public float getSpreadAlt(Gun gun)
	{
		return spreadAlt;
	}

	@Override
	public float getInaccuracySneak(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracySneakAlt(gun) : super.getInaccuracySneak(gun);
	}

	public float getInaccuracySneakAlt(Gun gun)
	{
		return inaccuracySneakAlt;
	}

	@Override
	public float getInaccuracyStand(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyStandAlt(gun) : super.getInaccuracyStand(gun);
	}

	public float getInaccuracyStandAlt(Gun gun)
	{
		return inaccuracyStandAlt;
	}

	@Override
	public float getInaccuracyFire(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyFireAlt(gun) : super.getInaccuracyFire(gun);
	}

	public float getInaccuracyFireAlt(Gun gun)
	{
		return inaccuracyFireAlt;
	}

	@Override
	public float getInaccuracyMove(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyMoveAlt(gun) : super.getInaccuracyMove(gun);
	}

	public float getInaccuracyMoveAlt(Gun gun)
	{
		return inaccuracyMoveAlt;
	}

	@Override
	public float getInaccuracyJump(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyJumpAlt(gun) : super.getInaccuracyJump(gun);
	}

	public float getInaccuracyJumpAlt(Gun gun)
	{
		return inaccuracyJumpAlt;
	}

	@Override
	public float getInaccuracyLand(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyLandAlt(gun) : super.getInaccuracyLand(gun);
	}

	public float getInaccuracyLandAlt(Gun gun)
	{
		return inaccuracyLandAlt;
	}

	@Override
	public float getInaccuracyLadder(Gun gun)
	{
		return isSecondMode(gun) ? getInaccuracyLadderAlt(gun) : super.getInaccuracyLadder(gun);
	}

	public float getInaccuracyLadderAlt(Gun gun)
	{
		return inaccuracyLadderAlt;
	}

	@Override
	public float getRecoilMagnitude(MSPlayer msPlayer)
	{
		return isSecondMode(msPlayer) ? getRecoilMagnitudeAlt(msPlayer) : super.getRecoilMagnitude(msPlayer);
	}

	public float getRecoilMagnitudeAlt(MSPlayer msPlayer)
	{
		return recoilMagnitudeAlt;
	}

	@Override
	public int getRecoilAngleVariance(MSPlayer msPlayer)
	{
		return isSecondMode(msPlayer) ? getRecoilAngleVarianceAlt(msPlayer) : super.getRecoilAngleVariance(msPlayer);
	}

	public int getRecoilAngleVarianceAlt(MSPlayer msPlayer)
	{
		return recoilAngleVarianceAlt;
	}

	@Override
	public int getRecoilMagnitudeVariance(MSPlayer msPlayer)
	{
		return isSecondMode(msPlayer) ? getRecoilMagnitudeVarianceAlt(msPlayer) : super.getRecoilMagnitudeVariance(msPlayer);
	}

	public int getRecoilMagnitudeVarianceAlt(MSPlayer msPlayer)
	{
		return recoilMagnitudeVarianceAlt;
	}

	@Override
	public float getCycleTime(MSPlayer msPlayer)
	{
		return isSecondMode(msPlayer) ? getCycleTimeAlt(msPlayer) : super.getCycleTime(msPlayer);
	}

	public float getCycleTimeAlt(MSPlayer msPlayer)
	{
		return cycleTimeAlt;
	}

	@Override
	public int getBullets(Gun gun)
	{
		return isSecondMode(gun) ? getBulletsAlt(gun) : super.getBullets(gun);
	}

	public int getBulletsAlt(Gun gun)
	{
		return bulletsAlt;
	}

	@Override
	public boolean isAutomatic(Gun gun)
	{
		return isSecondMode(gun) ? isAutomaticAlt(gun) : super.isAutomatic(gun);
	}

	public boolean isAutomaticAlt(Gun gun)
	{
		return automaticAlt;
	}
}
