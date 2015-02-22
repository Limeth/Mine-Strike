package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.tasks.SilencerToggle;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import org.bukkit.inventory.ItemStack;

public class SilencableExtension extends GunExtension
{
	public static final String  ATTRIBUTE_SUFFIX_SILENCED = " | SILENCED";
	private             boolean silenced                  = true;

	public SilencableExtension(Gun gun)
	{
		super(gun);
	}

	@Override
	public String getSoundShooting(MSPlayer msPlayer)
	{
		String sound = super.getSoundShooting(msPlayer);

		if(!silenced)
			sound += "_unsil";

		return sound;
	}

	@Override
	public boolean onLeftClick(MSPlayer msPlayer)
	{
		if(msPlayer.hasGunTask())
			return true;

		Gun gun = getGun();
		String sound = super.getSoundShooting(msPlayer) + "_silencer_" + (silenced ? "off" : "on");
		
		msPlayer.setGunTask(new SilencerToggle(msPlayer, gun, sound).startLoop());
		
		return true;
	}

	@Override
	public void apply(ItemStack itemStack, MSPlayer msPlayer)
	{
		if(!silenced)
			return;

		LoreAttributes.TEMP.clear();
		LoreAttributes.extract(itemStack, LoreAttributes.TEMP);

		String type = LoreAttributes.TEMP.get("Type");

		LoreAttributes.TEMP.put("Type", type + ATTRIBUTE_SUFFIX_SILENCED);
		LoreAttributes.TEMP.apply(itemStack);
	}

	public boolean isSilenced()
	{
		return silenced;
	}
	
	public void setSilenced(boolean silenced)
	{
		this.silenced = silenced;
	}
	
	public boolean toggleSilencer()
	{
		return silenced = !silenced;
	}
}
