package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public class ZoomExtension extends GunExtension
{
	public static final float SLOWNESS_RATE = 0.15F;
	private int stateIndex;
	private int[] states; //0-6 inc.
	
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
			return new int[] {3};
		case G3SG1:
		case SCAR_20:
			return new int[] {5};
		case SSG_08:
		case AWP:
			return new int[] {3, 6};
		default: return new int[0];
		}
	}
	
	@Override
	public void onLeftClick(MSPlayer msPlayer)
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
