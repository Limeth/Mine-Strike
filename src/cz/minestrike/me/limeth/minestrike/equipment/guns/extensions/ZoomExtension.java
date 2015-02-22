package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZoomExtension extends GunExtension
{
	public static final float SLOWNESS_RATE = 0.15F;
	private int stateIndex;
	private int[] states; //0-4 inc.
	
	public ZoomExtension(Gun gun)
	{
		super(gun);
		
		states = initStates(gun);
	}
	
	private static int[] initStates(Gun gun)
	{
		switch(gun.getEquipment())
		{
		case AUG:
		case SG_556:
			return new int[] {2};
		case G3SG1:
		case SCAR_20:
			return new int[] {2, 4};
		case SSG_08:
		case AWP:
			return new int[] {2, 4};
		default: return new int[0];
		}
	}
	
	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
		unzoom(msPlayer);
	}
	
	@Override
	public boolean onLeftClick(MSPlayer msPlayer)
	{
		nextZoom();
		
		Integer zoom = getZoom();
		Player player = msPlayer.getPlayer();
		Gun gun = getGun();
		
		if(zoom == null)
		{
			player.removePotionEffect(PotionEffectType.SLOW);
			gun.setSecondaryState(false);
		}
		else
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, zoom, true), true);
			gun.setSecondaryState(true);
		}
		
		msPlayer.updateMovementSpeed();
		
		return true;
	}
	
	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		Integer zoom = getZoom();
		
		if(zoom == null)
			return super.getMovementSpeed(msPlayer);
		
		float modifier = 1 - (zoom + 1) * SLOWNESS_RATE;
		
		return super.getMovementSpeed(msPlayer) * 0.8F / modifier;
	}
	
	public void unzoom(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		Gun gun = getGun();
		stateIndex = 0;
		
		gun.setSecondaryState(false);
		msPlayer.updateMovementSpeed();
		player.removePotionEffect(PotionEffectType.SLOW);
	}
	
	public void nextZoom()
	{
		stateIndex = (stateIndex + 1) % (states.length + 1);
	}
	
	public Integer getZoom()
	{
		return stateIndex == 0 ? null : states[stateIndex - 1];
	}
	
	public boolean isZoomed()
	{
		return stateIndex > 0;
	}
}
