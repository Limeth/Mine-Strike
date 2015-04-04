package cz.minestrike.me.limeth.minestrike.equipment.guns.type;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Limeth
 */
public abstract class ScopeGunType extends DoubleModeGunType
{
	private static final String DATA_SCOPE_INDEX = "type.scope.index";
	public static final float SLOWNESS_RATE = 0.15F;
	private Boolean lazyFrameShown;
	private int[] lazyZoomCycle;

	public ScopeGunType(String gunId, String name, String directoryName, String textureName, boolean primary, boolean loadingContinuously, float reloadTime, float weaponArmorRatio, int damage, float rangeModifier, float cycleTime, int penetration, int killAwardCompetitive, int killAwardCasual, int movementSpeed, int clipSize, int spareCapacity, int price, int range, boolean automatic, float flinchVelocityModifierLarge, float flinchVelocityModifierSmall, float spread, float inaccuracySneak, float inaccuracyStand, float inaccuracyFire, float inaccuracyMove, float inaccuracyJump, float inaccuracyLand, float inaccuracyLadder, float recoveryTimeSneak, float recoveryTimeStand, int recoilAngleVariance, float recoilMagnitude, int recoilMagnitudeVariance, int bullets, boolean automaticAlt, float spreadAlt, float inaccuracySneakAlt, float inaccuracyStandAlt, float inaccuracyFireAlt, float inaccuracyMoveAlt, float inaccuracyJumpAlt, float inaccuracyLandAlt, float inaccuracyLadderAlt, int recoilAngleVarianceAlt, float recoilMagnitudeAlt, int recoilMagnitudeVarianceAlt, float cycleTimeAlt, int bulletsAlt)
	{
		super(gunId, name, directoryName, textureName, primary, loadingContinuously, reloadTime, weaponArmorRatio, damage, rangeModifier, cycleTime, penetration, killAwardCompetitive, killAwardCasual, movementSpeed, clipSize, spareCapacity, price, range, automatic, flinchVelocityModifierLarge, flinchVelocityModifierSmall, spread, inaccuracySneak, inaccuracyStand, inaccuracyFire, inaccuracyMove, inaccuracyJump, inaccuracyLand, inaccuracyLadder, recoveryTimeSneak, recoveryTimeStand, recoilAngleVariance, recoilMagnitude, recoilMagnitudeVariance, bullets, automaticAlt, spreadAlt, inaccuracySneakAlt, inaccuracyStandAlt, inaccuracyFireAlt, inaccuracyMoveAlt, inaccuracyJumpAlt, inaccuracyLandAlt, inaccuracyLadderAlt, recoilAngleVarianceAlt, recoilMagnitudeAlt, recoilMagnitudeVarianceAlt, cycleTimeAlt, bulletsAlt);
	}

	protected abstract boolean initFrameShown();
	protected abstract int[] initZoomCycle();

	public boolean isFrameShown()
	{
		return lazyFrameShown != null ? lazyFrameShown : (lazyFrameShown = initFrameShown());
	}

	public int[] getZoomCycle()
	{
		return lazyZoomCycle != null ? lazyZoomCycle : (lazyZoomCycle = initZoomCycle());
	}

	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
		unzoom(msPlayer);
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block block)
	{
		Gun gun = msPlayer.getEquipmentInHand();

		if(msPlayer.hasGunTask())
			return true;

		nextZoom(gun);

		Integer zoom = getZoom(gun);
		Player player = msPlayer.getPlayer();
		boolean previousMode = isSecondMode(gun);

		if(zoom == null)
		{
			player.removePotionEffect(PotionEffectType.SLOW);
			setSecondMode(gun, false);

			if(previousMode && isFrameShown())
				msPlayer.updateInventory();
		}
		else
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, zoom, true), true);
			setSecondMode(gun, true);

			if(!previousMode && isFrameShown())
			{
				CraftPlayer craftPlayer = ((CraftPlayer) msPlayer.getPlayer());
				EntityPlayer nmsPlayer = craftPlayer.getHandle();
				ItemStack nmsItem = new ItemStack(Blocks.PUMPKIN);

				PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, 5, nmsItem);
				nmsPlayer.playerConnection.sendPacket(packet);
			}
		}

		msPlayer.updateMovementSpeed();

		return true;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		Gun gun = msPlayer.getEquipmentInHand();
		Integer zoom = getZoom(gun);

		if(zoom == null)
			return super.getMovementSpeed(msPlayer);

		float modifier = 1 - (zoom + 1) * SLOWNESS_RATE;

		return super.getMovementSpeed(msPlayer) * 0.8F / modifier;
	}

	public void unzoom(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		Gun gun = msPlayer.getEquipmentInHand();

		setZoomIndex(gun, 0);
		setSecondMode(gun, false);
		msPlayer.updateMovementSpeed();
		player.removePotionEffect(PotionEffectType.SLOW);

		if(isFrameShown())
			msPlayer.updateInventory();
	}

	public void nextZoom(Gun gun)
	{
		int[] states = getZoomCycle();
		int stateIndex = getZoomIndex(gun);
		stateIndex = (stateIndex + 1) % (states.length + 1);

		setZoomIndex(gun, stateIndex);
	}

	public Integer getZoom(Gun gun)
	{
		int stateIndex = getZoomIndex(gun);
		int[] states = getZoomCycle();

		return stateIndex == 0 ? null : states[stateIndex - 1];
	}

	public boolean isZoomed(Gun gun)
	{
		return getZoomIndex(gun) > 0;
	}

	public int getZoomIndex(Gun gun)
	{
		Object index = gun.getCustomData().get(DATA_SCOPE_INDEX);

		try
		{
			return index != null ? (Integer) index : 0;
		}
		catch(ClassCastException e)
		{
			return 0;
		}
	}

	public void setZoomIndex(Gun gun, int index)
	{
		gun.getCustomData().put(DATA_SCOPE_INDEX, index);
	}
}
