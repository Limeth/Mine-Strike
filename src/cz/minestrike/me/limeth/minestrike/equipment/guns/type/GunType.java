package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.M249;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.Negev;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.AWP;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.G3SG1;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.SCAR20;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.SSG08;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.MAG7;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.Nova;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.SawedOff;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.XM1014;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.*;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Limeth
 */
public abstract class GunType implements Equipment
{
	private static Map<String, GunType> lazyRegisteredTypes;

	private static Map<String, GunType> initRegisteredTypes()
	{
		@SuppressWarnings("unchecked")
		Class<? extends GunType>[] types = new Class[] {
				//Pistols
				Deagle.class,
				Elite.class,
				FiveSeven.class,
				Glock.class,
				P2000.class,
				UspS.class,
				P250.class,
				CZ75.class,
				TEC9.class,

				//Shotguns
				MAG7.class,
				Nova.class,
				SawedOff.class,
				XM1014.class,

				//Sub-Machine Guns
				Bizon.class,
				MAC10.class,
				MP7.class,
				MP9.class,
				P90.class,
				UMP45.class,

				//Automatic Rifles
				AK47.class,
				AUG.class,
				FAMAS.class,
				GalilAR.class,
				M4A4.class,
				M4A1S.class,
				SG556.class,

				//Light Machine Guns
				M249.class,
				Negev.class,

				//Sniper Rifles
				AWP.class,
				G3SG1.class,
				SCAR20.class,
				SSG08.class
		};

		return Arrays.stream(types)
                    .map(GunType::getInstanceOf)
                    .collect(Collectors.toMap(Equipment::getId, type -> type));
	}

	private static <T> T getInstanceOf(Class<? extends T> clazz)
	{
		try
		{
			return (T) clazz.getMethod("getInstance").invoke(null);
		}
		catch(NoSuchMethodException | InvocationTargetException | ClassCastException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Map<String, GunType> getRegisteredTypes()
	{
		return Maps.newHashMap(lazyRegisteredTypes != null
		                       ? lazyRegisteredTypes
		                       : (lazyRegisteredTypes = initRegisteredTypes()));
	}

	private final String gunId;

	public GunType(String gunId)
	{
		Preconditions.checkNotNull(gunId);
		Preconditions.checkArgument(gunId.length() > 0);

		this.gunId = gunId;
	}

	public abstract String getSoundShooting(MSPlayer msPlayer);
	public abstract String getName();
	public abstract String getDirectoryName(MSPlayer msPlayer);
	public abstract String getTextureName();
	public abstract boolean isPrimary(MSPlayer msPlayer);
	public abstract boolean isLoadingContinuously(MSPlayer msPlayer);
	public abstract boolean isAutomatic(Gun gun);
	public abstract int getKillAwardCompetitive(MSPlayer msPlayer);
	public abstract int getKillAwardCasual(MSPlayer msPlayer);
	public abstract int getPenetration(MSPlayer msPlayer);
	public abstract int getClipSize();
	public abstract int getSpareCapacity();
	public abstract int getRange(MSPlayer msPlayer);
	public abstract int getBullets(Gun gun);
	public abstract float getReloadTime(MSPlayer msPlayer);
	public abstract float getWeaponArmorRatio();
	public abstract float getDamage(MSPlayer msPlayer);
	public abstract float getRangeModifier(MSPlayer msPlayer);
	public abstract float getCycleTime(MSPlayer msPlayer);
	public abstract float getMovementSpeed(MSPlayer msPlayer);
	public abstract float getFlinchVelocityModifierLarge(MSPlayer msPlayer);
	public abstract float getFlinchVelocityModifierSmall(MSPlayer msPlayer);
	public abstract float getSpread(Gun gun);
	public abstract float getInaccuracySneak(Gun gun);
	public abstract float getInaccuracyStand(Gun gun);
	public abstract float getInaccuracyFire(Gun gun);
	public abstract float getInaccuracyMove(Gun gun);
	public abstract float getInaccuracyJump(Gun gun);
	public abstract float getInaccuracyLand(Gun gun);
	public abstract float getInaccuracyLadder(Gun gun);
	public abstract float getRecoveryTimeSneak(Gun gun);
	public abstract float getRecoveryTimeStand(Gun gun);
	public abstract int getRecoilAngleVariance(MSPlayer msPlayer);
	public abstract float getRecoilMagnitude(MSPlayer msPlayer);
	public abstract int getRecoilMagnitudeVariance(MSPlayer msPlayer);
	public abstract float getArmorReducedDamage(MSPlayer msPlayer);
	public abstract float getArmorReduction(MSPlayer msPlayer);
	public abstract float getArmorAbsorptionCost(MSPlayer msPlayer);

	public static GunType getById(String id)
	{
		return getRegisteredTypes().get(id);
	}

	public static GunType getByGunId(String gunId)
	{
		return getById("GUN_" + gunId);
	}

	public void initialize(Gun gun) {}

	public void apply(ItemStack itemStack, MSPlayer msPlayer, Gun gun) {}

	public String getGunId()
	{
		return gunId;
	}

	@Override
	public void onSelect(MSPlayer msPlayer) {}

	@Override
	public void onDeselect(MSPlayer msPlayer) {}

	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		return true;
	}

	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		return new FilledArrayList<>();
	}

	@Override
	public String getId()
	{
		return "GUN_" + getGunId();
	}

	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		 return Gun.class;
	}

	@Override
	public Equipment getSource()
	{
		return this;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return new Gun(this).newItemStack(msPlayer);
	}

	@Override
	public EquipmentCategory getCategory()
	{
		return EquipmentCategory.FIREARMS;
	}

	@Override
	public boolean isTradable()
	{
		return false;
	}

	@Override
	public String getDefaultSkin(MSPlayer msPlayer)
	{
		return "DEFAULT";
	}

	@Override
	public boolean isDroppableManually()
	{
		return false;
	}

	@Override
	public boolean isDroppedOnDeath()
	{
		return false;
	}
}
