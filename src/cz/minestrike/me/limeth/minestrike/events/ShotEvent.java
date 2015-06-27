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
	double getPenetration();
	void setPenetration(double penetration);

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
