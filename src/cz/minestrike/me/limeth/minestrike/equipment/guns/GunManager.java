package cz.minestrike.me.limeth.minestrike.equipment.guns;

import net.minecraft.server.v1_7_R4.EnumMovingObjectType;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.BodyPart;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import darkBlade12.ParticleEffect;

public class GunManager
{
	public static void showTrace(Location from, Location to)
	{
		double stepSize = 1;
		
		Vector difference = to.clone().subtract(from).toVector();
		double diffLength = difference.length();
		Vector step = difference.clone().multiply(stepSize / diffLength);
		Location currentLocation = from.clone().add(step.clone().multiply(Math.random()));
		org.bukkit.World world = from.getWorld();
		Block fromBlock = from.getBlock();
		Material fromType = fromBlock.getType();
		boolean inWater = fromType == Material.WATER || fromType == Material.STATIONARY_WATER;
		
		while(currentLocation.distanceSquared(to) > stepSize * stepSize)
		{
			currentLocation.add(step);
			
			Block block = currentLocation.getBlock();
			Material type = block.getType();
			boolean curInWater = type == Material.WATER || type == Material.STATIONARY_WATER;
			
			if(curInWater != inWater)
			{
				inWater = curInWater;
				
				ParticleEffect.SPLASH.display(currentLocation, 0, 0, 0, 1, 20);
				world.playSound(currentLocation, Sound.SPLASH, 1F, 2F);
			}
			
			if(inWater)
			{
				ParticleEffect.BUBBLE.display(currentLocation, 0, 0, 0, 1, 1);
			}
			else
			{
				ParticleEffect.TOWN_AURA.display(currentLocation, 0, 0, 0, 1, 1);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void onBulletHit(MovingObjectPosition[] mops, Player bukkitPlayer)
	{
		MSPlayer msPlayer = MSPlayer.get(bukkitPlayer);
		Location eyeLoc = bukkitPlayer.getEyeLocation();
		org.bukkit.World bukkitWorld = eyeLoc.getWorld();
		
		for(int i = 0; i < mops.length; i++)
		{
			MovingObjectPosition mop = mops[i];
			
			if(mop.type == EnumMovingObjectType.BLOCK)
			{
				Location hitLoc = new Location(bukkitWorld, mop.pos.a, mop.pos.b, mop.pos.c);
				Block block = bukkitWorld.getBlockAt(mop.b, mop.c, mop.d);
				Material type = block.getType();
				int id = type.getId();
				byte data = block.getData();
				Sound sound = getDigSound(type);
				
				ParticleEffect.displayBlockDust(hitLoc, id, data, 0, 0, 0, 0.1F, 25);
				bukkitWorld.playSound(hitLoc, sound, 1F, (float) (1 + Math.random()));
			}
			else if(mop.type == EnumMovingObjectType.ENTITY)
			{
				org.bukkit.entity.Entity rawBukkitVictim = mop.entity.getBukkitEntity();
				
				if(!(rawBukkitVictim instanceof Player))
					return;
				
				HotbarContainer hotbarContainer = msPlayer.getHotbarContainer();
				Equipment equipment = hotbarContainer.getHeld(msPlayer);
				
				if(!(equipment instanceof Gun))
					return;
				
				Gun gun = (Gun) equipment;
				GunType type = gun.getEquipment();
				double damageDivision = Math.pow(2, i);
				Player bukkitVictim = (Player) rawBukkitVictim;
				MSPlayer msVictim = MSPlayer.get(bukkitVictim);
				Location effectLoc = new Location(bukkitWorld, mop.pos.a, mop.pos.b, mop.pos.c);
				double distance = eyeLoc.distance(effectLoc);
				double rangeModifier = type.getRangeModifier();
				double damage = type.getDamage() / damageDivision;
				
//				if(bukkitPlayer.isOp())
//					bukkitPlayer.sendMessage("damage: " + damage + "\nrangeModifier: " + rangeModifier + "\ndistance: " + distance
//							+ "\nexp: " + (distance / (500 * MSConstant.CS_UNITS_TO_METERS_MODIFIER)) + "\nresult: " + (Math.pow(rangeModifier, distance / (500 * MSConstant.CS_UNITS_TO_METERS_MODIFIER))* damage));
				
				damage *= Math.pow(rangeModifier, distance / (500 * MSConstant.CS_UNITS_TO_METERS_MODIFIER));
				double hitY = mop.pos.b;
				double victimY = mop.entity.locY;
				double relHitY = hitY - victimY;
				BodyPart bodyPart = BodyPart.getByY(relHitY);
				
				msVictim.damage(damage, msPlayer, gun, bodyPart);
				ParticleEffect.displayBlockCrack(effectLoc, Material.REDSTONE_BLOCK.getId(), (byte) 0, 0, 0, 0, 1.5F, 20);
			}
		}
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
