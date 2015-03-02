package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * @author Limeth
 */
public class BurstFireGunType extends DoubleModeGunType
{
	public static final String SOUND_TOGGLE = "projectsurvive:counterstrike.weapons.auto_semiauto_switch";

	public BurstFireGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets, boolean automaticAlt, float spreadAlt, float inaccuracySneakAlt, float inaccuracyStandAlt, float inaccuracyFireAlt, float inaccuracyMoveAlt, float inaccuracyJumpAlt, float inaccuracyLandAlt, float inaccuracyLadderAlt, int recoilAngleVarianceAlt, float recoilMagnitudeAlt, int recoilMagnitudeVarianceAlt, int bulletsAlt)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets, automaticAlt, spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt, recoilAngleVarianceAlt, recoilMagnitudeAlt, recoilMagnitudeVarianceAlt, bulletsAlt);
	}

	public BurstFireGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets);
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block block)
	{
		Gun gun = (Gun) msPlayer.getEquipmentInHand();

		setSecondMode(gun, !isSecondMode(gun));
		SoundManager.play(SOUND_TOGGLE, msPlayer.getPlayer());

		return true;
	}

	@Override
	public boolean rightClick(MSPlayer msPlayer, Block block)
	{
		Gun gun = (Gun) msPlayer.getEquipmentInHand();

		if(!isSecondMode(gun))
			return super.rightClick(msPlayer, block);

		if(!gun.isShotDelaySatisfied(msPlayer))
			return true;

		shootBurst(msPlayer);

		return true;
	}

	public void shootBurst(MSPlayer msPlayer)
	{
		Gun gun = (Gun) msPlayer.getEquipmentInHand();
		int bulletAmount = gun.getShootingBullets();

		if(bulletAmount >= 1)
			shootSingle(msPlayer);

		for(int i = 0; i < bulletAmount - 1; i++)
			Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new BurstRunnable(msPlayer), i);
	}

	private void shootSingle(MSPlayer msPlayer)
	{
		Gun gun = (Gun) msPlayer.getEquipmentInHand();

		if(!gun.isLoaded())
			return;

		HotbarContainer container = msPlayer.getHotbarContainer();

		gun.decreaseLoadedBullets();
		msPlayer.shoot(gun);
		container.apply(msPlayer, gun);
	}

	private class BurstRunnable implements Runnable
	{
		private final MSPlayer msPlayer;

		public BurstRunnable(MSPlayer msPlayer)
		{
			this.msPlayer = msPlayer;
		}

		@Override
		public void run()
		{
			shootSingle(msPlayer);
		}
	}
}
