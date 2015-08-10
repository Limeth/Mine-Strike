package cz.minestrike.me.limeth.minestrike.listeners;

import cz.minestrike.me.limeth.minestrike.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.events.BlockShotEvent;
import cz.minestrike.me.limeth.minestrike.events.PlayerShotEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;
import darkBlade12.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * @author Limeth
 */
public class UniversalShotListener extends MSListener
{
	private static final double PENETRATION_PLAYER = 0.8;

	@EventHandler(ignoreCancelled = true)
	public void onPlayerShot(PlayerShotEvent event, MSPlayer msPlayer)
	{
		DamageRecord damageRecord = event.getDamageRecord();
		MSPlayer msVictim = event.getMSVictim();
		Location hitLoc = event.getLocationBulletFinal();

		msVictim.damage(damageRecord);
		ParticleEffect.displayBlockCrack(hitLoc, Material.REDSTONE_BLOCK.getId(), (byte) 0, 0, 0, 0, 1.5F, 20);
		event.penetrate(PENETRATION_PLAYER);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockShot(BlockShotEvent event, MSPlayer msPlayer)
	{
		Location hitLoc = event.getLocationBulletFinal();
		World bukkitWorld = hitLoc.getWorld();
		Block block = event.getBlock();
		Material type = block.getType();
		int id = type.getId();
		byte data = block.getData();
		Sound sound = getDigSound(type);
		BlockProperties properties = BlockPropertiesManager.getProperties(block);
		double penetration = properties.getPenetration();

		ParticleEffect.displayBlockDust(hitLoc, id, data, 0, 0, 0, 0.1F, 25);
		bukkitWorld.playSound(hitLoc, sound, 1F, (float) (1 + Math.random()));
		event.penetrate(penetration);
	}

	private final static Sound[] digSounds = {
			Sound.DIG_GRASS, Sound.DIG_STONE, Sound.DIG_GRAVEL,
			Sound.DIG_SAND, Sound.DIG_SNOW,
			Sound.DIG_WOOD, Sound.DIG_WOOL, Sound.GLASS
	};

	private static Sound getDigSound(Material type)
	{
		switch(type)
		{
			case DIRT: return Sound.DIG_GRAVEL;
			case CARPET: return Sound.DIG_WOOL;
			default: break;
		}

		String name = type.name();

		for(Sound sound : digSounds)
		{
			String soundName = sound.name();

			if(soundName.startsWith("DIG_"))
				soundName = soundName.substring(4);

			if(name.contains(soundName))
				return sound;
		}

		return Sound.DIG_STONE;
	}
}
