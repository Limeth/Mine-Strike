package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import net.darkseraphim.actionbar.ActionBarAPI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Limeth
 */
public abstract class BurstFireGunType extends DoubleModeGunType
{
	public static final String SOUND_TOGGLE = "projectsurvive:counterstrike.weapons.auto_semiauto_switch";
	private Integer lazyBurstFireSize;
	private Float lazyBurstFireBulletDelay;

	public BurstFireGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets, boolean automaticAlt, float spreadAlt, float inaccuracySneakAlt, float inaccuracyStandAlt, float inaccuracyFireAlt, float inaccuracyMoveAlt, float inaccuracyJumpAlt, float inaccuracyLandAlt, float inaccuracyLadderAlt, int recoilAngleVarianceAlt, float recoilMagnitudeAlt, int recoilMagnitudeVarianceAlt, float cycleTimeAlt, int bulletsAlt)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets, automaticAlt, spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt, recoilAngleVarianceAlt, recoilMagnitudeAlt, recoilMagnitudeVarianceAlt, cycleTimeAlt, bulletsAlt);
	}

	public BurstFireGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets);
	}

	public abstract int initBurstFireSize();
	public abstract float initBurstFireBulletDelay();

	public int getBurstFireSize()
	{
		return lazyBurstFireSize != null ? lazyBurstFireSize : (lazyBurstFireSize = initBurstFireSize());
	}

	public float getBurstFireBulletDelay()
	{
		return lazyBurstFireBulletDelay != null ? lazyBurstFireBulletDelay : (lazyBurstFireBulletDelay = initBurstFireBulletDelay());
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block block)
	{
		Gun gun = msPlayer.getEquipmentInHand();
		boolean enabled = !isSecondMode(gun);
		Translation message = enabled ? Translation.EQUIPMENT_BURSTFIRE_ENABLED : Translation.EQUIPMENT_BURSTFIRE_DISABLED;
		Player player = msPlayer.getPlayer();

		setSecondMode(gun, enabled);
		SoundManager.play(SOUND_TOGGLE, msPlayer.getPlayer());
		ActionBarAPI.sendActionBar(message.getMessage(), player);

		return true;
	}

	@Override
	public boolean rightClick(MSPlayer msPlayer, Block block)
	{
		Gun gun = msPlayer.getEquipmentInHand();

		if(!isSecondMode(gun))
			return super.rightClick(msPlayer, block);

		if(!gun.isShotDelaySatisfied(msPlayer))
			return true;

		shootBurst(msPlayer);

		return true;
	}

	public void shootBurst(MSPlayer msPlayer)
	{
		int bulletAmount = getBurstFireSize();

		if(bulletAmount <= 0)
			return;

		float bulletDelay = getBurstFireBulletDelay();
		MineStrike plugin = MineStrike.getInstance();
		Runnable runnable = () -> shootSingle(msPlayer);

		shootSingle(msPlayer);

		for(int i = 1; i < bulletAmount; i++)
		{
			int currentDelay = Math.round(bulletDelay * i * 20);

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, currentDelay);
		}
	}

	private void shootSingle(MSPlayer msPlayer)
	{
		Gun gun = msPlayer.getEquipmentInHand();

		if(!gun.isLoaded())
			return;

		HotbarContainer container = msPlayer.getHotbarContainer();

		gun.decreaseLoadedBullets();
		shoot(msPlayer);
		container.apply(msPlayer, gun);
	}
}
