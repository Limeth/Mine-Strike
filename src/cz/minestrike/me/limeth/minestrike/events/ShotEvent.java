package cz.minestrike.me.limeth.minestrike.events;

import com.sk89q.worldedit.event.Cancellable;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import org.bukkit.Location;

/**
 * @author Limeth
 */
public interface ShotEvent extends Cancellable
{
	MSPlayer getMSPlayer();
	Location getLocationBulletFinal();
	double getDamage();
	void setDamage(double damage);

	/**
     * Defaults to 1
     *
	 * @return The relative damage modifier, describes the penetration value of this object.
	 */
	double getRelativePenetration();

	/**
     * Relative Penetration * Absolute Penetration of the previous object or 1 if none
     *
	 * @return The absolute damage modifier applied to bullets after penetrating this object
	 */
	double getAbsolutePenetration();
	double penetrate(double penetrationModifier);
	boolean isPenetrated();

	default Location getLocationBulletInitial()
	{
		return getMSPlayer().getPlayer().getEyeLocation();
	}

	default Gun getGun()
	{
		MSPlayer msPlayer = getMSPlayer();
		HotbarContainer container = msPlayer.getHotbarContainer();

		return (Gun) container.getHeld(msPlayer);
	}
}
