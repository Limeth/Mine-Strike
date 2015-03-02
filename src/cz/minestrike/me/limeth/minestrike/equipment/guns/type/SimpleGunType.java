package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import org.bukkit.block.Block;

/**
 * @author Limeth
 */
public class SimpleGunType extends GunType
{
	private final String                        name, directoryName, textureName;
	private final boolean primary, loadingContinuously, automatic;
	private final int killAwardCompetitive, killAwardCasual, penetration, clipSize, spareCapacity, price, range, bullets, recoilAngleVariance, recoilMagnitudeVariance;
	private final float reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, movementSpeed, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilMagnitude, armorReducedDamage, armorReduction, armorAbsorptionCost;

	protected SimpleGunType(String gunId, String name, String directoryName,
	                        String textureName, boolean primary, boolean loadingContinuously, float reloadTime,
	                        float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration,
	                        int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity,
	                        int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall,
	                        float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove,
	                        float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand,
	                        int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets)
	{
		super(gunId);

		this.name = name;
		this.directoryName = directoryName;
		this.textureName = textureName != null ? textureName : directoryName;
		this.primary = primary;
		this.loadingContinuously = loadingContinuously;
		this.reloadTime = reloadTime * 20F;
		this.weaponArmorRatio = weaponArmorRatio / 2F;
		this.damage = damage * 20 / MSConstant.CS_MAX_HEALTH;
		this.armorReducedDamage = this.damage * weaponArmorRatio;
		this.armorReduction = 1F - weaponArmorRatio;
		this.armorAbsorptionCost = armorReduction / 2F;
		this.rangeModifier = rangeModifier;
		this.cycleTime = cycleTime * 20F;
		this.penetration = penetration;
		this.killAwardCompetitive = killAwardCompetitive;
		this.killAwardCasual = killAwardCasual;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.clipSize = clipSize;
		this.spareCapacity = spareCapacity;
		this.price = price;
		this.range = (int) (range * MSConstant.CS_UNITS_TO_METERS_MODIFIER);
		this.automatic = automatic;
		this.flinchVelocityModifierLarge = flinchVelocityModifierLarge;
		this.flinchVelocityModifierSmall = flinchVelocityModifierSmall;
		this.spread = spread;
		this.inaccuracySneak = inaccuracySneak;
		this.inaccuracyStand = inaccuracyStand;
		this.inaccuracyFire = inaccuracyFire;
		this.inaccuracyMove = inaccuracyMove;
		this.inaccuracyJump = inaccuracyJump;
		this.inaccuracyLand = inaccuracyLand;
		this.inaccuracyLadder = inaccuracyLadder;
		this.recoveryTimeSneak = recoveryTimeSneak;
		this.recoveryTimeStand = recoveryTimeStand;
		this.recoilAngleVariance = recoilAngleVariance;
		this.recoilMagnitude = recoilMagnitude;
		this.recoilMagnitudeVariance = recoilMagnitudeVariance;
		this.bullets = bullets;
	}

	@Override
	public String getSoundShooting(MSPlayer msPlayer)
	{
		return "projectsurvive:counterstrike.weapons." + directoryName + "." + directoryName;
	}

	@Override
	public String getSoundDrawing()
	{
		return "projectsurvive:counterstrike.weapons." + directoryName + "." + directoryName + "_draw";
	}

	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		msPlayer.pressTrigger((Gun) msPlayer.getEquipmentInHand());
		return true;
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}

	@Override
	public String getDisplayName()
	{
		return getName();
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return price;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return movementSpeed;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getDirectoryName(MSPlayer msPlayer)
	{
		return directoryName;
	}

	@Override
	public String getTextureName()
	{
		return textureName;
	}

	@Override
	public boolean isPrimary(MSPlayer msPlayer)
	{
		return primary;
	}

	@Override
	public boolean isLoadingContinuously(MSPlayer msPlayer)
	{
		return loadingContinuously;
	}

	@Override
	public int getKillAwardCompetitive(MSPlayer msPlayer)
	{
		return killAwardCompetitive;
	}

	@Override
	public int getKillAwardCasual(MSPlayer msPlayer)
	{
		return killAwardCasual;
	}

	@Override
	public int getPenetration(MSPlayer msPlayer)
	{
		return penetration;
	}

	@Override
	public int getClipSize()
	{
		return clipSize;
	}

	@Override
	public int getSpareCapacity()
	{
		return spareCapacity;
	}

	@Override
	public int getRange(MSPlayer msPlayer)
	{
		return range;
	}

	@Override
	public float getReloadTime(MSPlayer msPlayer)
	{
		return reloadTime;
	}

	@Override
	public float getWeaponArmorRatio()
	{
		return weaponArmorRatio;
	}

	@Override
	public float getDamage(MSPlayer msPlayer)
	{
		return damage;
	}

	@Override
	public float getRangeModifier(MSPlayer msPlayer)
	{
		return rangeModifier;
	}

	@Override
	public float getCycleTime(MSPlayer msPlayer)
	{
		return cycleTime;
	}

	@Override
	public float getFlinchVelocityModifierLarge(MSPlayer msPlayer)
	{
		return flinchVelocityModifierLarge;
	}

	@Override
	public float getFlinchVelocityModifierSmall(MSPlayer msPlayer)
	{
		return flinchVelocityModifierSmall;
	}

	@Override
	public boolean isAutomatic(Gun gun)
	{
		return automatic;
	}

	@Override
	public int getBullets(Gun gun)
	{
		return bullets;
	}

	@Override
	public float getSpread(Gun gun)
	{
		return spread;
	}

	@Override
	public float getInaccuracySneak(Gun gun)
	{
		return inaccuracySneak;
	}

	@Override
	public float getInaccuracyStand(Gun gun)
	{
		return inaccuracyStand;
	}

	@Override
	public float getInaccuracyFire(Gun gun)
	{
		return inaccuracyFire;
	}

	@Override
	public float getInaccuracyMove(Gun gun)
	{
		return inaccuracyMove;
	}

	@Override
	public float getInaccuracyJump(Gun gun)
	{
		return inaccuracyJump;
	}

	@Override
	public float getInaccuracyLand(Gun gun)
	{
		return inaccuracyLand;
	}

	@Override
	public float getInaccuracyLadder(Gun gun)
	{
		return inaccuracyLadder;
	}

	@Override
	public float getRecoveryTimeSneak(Gun gun)
	{
		return recoveryTimeSneak;
	}

	@Override
	public float getRecoveryTimeStand(Gun gun)
	{
		return recoveryTimeStand;
	}

	@Override
	public int getRecoilAngleVariance(MSPlayer msPlayer)
	{
		return recoilAngleVariance;
	}

	@Override
	public float getRecoilMagnitude(MSPlayer msPlayer)
	{
		return recoilMagnitude;
	}

	@Override
	public int getRecoilMagnitudeVariance(MSPlayer msPlayer)
	{
		return recoilMagnitudeVariance;
	}

	@Override
	public float getArmorReducedDamage(MSPlayer msPlayer)
	{
		return armorReducedDamage;
	}

	@Override
	public float getArmorReduction(MSPlayer msPlayer)
	{
		return armorReduction;
	}

	@Override
	public float getArmorAbsorptionCost(MSPlayer msPlayer)
	{
		return armorAbsorptionCost;
	}
}
