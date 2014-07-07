package cz.minestrike.me.limeth.minestrike.equipment.guns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.games.Game;
import darkBlade12.ParticleEffect;

public class GunManager
{
	private static double size = 0;
	private static float victimMargin = 0F;//0.3F;
//	private AxisAlignedBB boundingBox = AxisAlignedBB.a(-size / 2, -size / 2, -size / 2, size / 2, size / 2, size / 2);
	
	public static void showTrace(Location from, Location to)
	{
		double stepSize = 0.2;
		
		Vector difference = to.clone().subtract(from).toVector();
		double diffLength = difference.length();
		Vector step = difference.clone().multiply(stepSize / diffLength);
		Location currentLocation = from.clone();
		org.bukkit.World world = from.getWorld();
		Block fromBlock = from.getBlock();
		Material fromType = fromBlock.getType();
		boolean inWater = fromType == Material.WATER || fromType == Material.STATIONARY_WATER;
		
		while(currentLocation.distance(to) > stepSize)
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
	
	/**
	 * Made with CB 1.7.2 R0.3
	 */
	public static void shoot(Location location, MSPlayer msShooter, GunType gunType)
	{
		Player bukkitShooter = msShooter.getPlayer();
		EntityHuman shooter = (EntityHuman) ((CraftPlayer) bukkitShooter).getHandle();
		World world = shooter.getWorld();
		location = location.clone();
		int range = gunType.getRange();
		Vector direction = location.getDirection();
		Vector inaccuracyDirection = msShooter.getInaccuracyVector(gunType);
		Vector recoilDirection = msShooter.getRecoilVector(direction, gunType);
		direction.add(recoilDirection).add(inaccuracyDirection);
		direction.multiply(range / direction.length()); //Normalize to range
		
		msShooter.increaseRecoil(gunType.getRecoilMagnitude());
		
		double locX = location.getX();
		double locY = location.getY();
		double locZ = location.getZ();
		double motX = direction.getX();
		double motY = direction.getY();
		double motZ = direction.getZ();

		Vec3D vec3d = world.getVec3DPool().create(locX, locY,
				locZ);
		Vec3D vec3d1 = world.getVec3DPool().create(locX + motX,
				locY + motY, locZ + motZ);
		MovingObjectPosition[] movingobjectposition = { world.a(vec3d, vec3d1) };

		vec3d = world.getVec3DPool().create(locX, locY,
				locZ);
		vec3d1 = world.getVec3DPool().create(locX + motX,
				locY + motY, locZ + motZ);
		
		if(movingobjectposition[0] != null)
		{
			vec3d1 = world.getVec3DPool().create(
					movingobjectposition[0].pos.c, movingobjectposition[0].pos.d,
					movingobjectposition[0].pos.e);
		}

		if(!(world.isStatic))
		{
			//Entity entity = null;
			HashMap<MovingObjectPosition, Double> hitDistances = new HashMap<MovingObjectPosition, Double>();
			AxisAlignedBB targetBoundingBox = AxisAlignedBB.a(-size / 2, -size / 2, -size / 2, size / 2, size / 2, size / 2).d(locX, locY, locZ).a(motX, motY, motZ).grow(
					1.0D, 1.0D, 1.0D
			);
			
			@SuppressWarnings("unchecked")
			List<? extends Entity> list = world.getEntities(
					shooter,
					targetBoundingBox
			);
			//double d0 = 0.0D;
			
			for(int i = 0; i < list.size(); ++i)
			{
				Entity entity1 = (Entity) list.get(i);
				
				if(entity1.R() && entity1 != shooter)
				{
					AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(victimMargin,
							victimMargin, victimMargin);
					MovingObjectPosition movingobjectposition1 = axisalignedbb
							.a(vec3d, vec3d1);

					if(movingobjectposition1 != null)
					{
						double curDistance = vec3d
								.distanceSquared(movingobjectposition1.pos);

						/*if((curDistance < d0) || (d0 == 0.0D))
						{
							entity = entity1;
							d0 = curDistance;
						}*/
						hitDistances.put(new MovingObjectPosition(entity1, vec3d1), curDistance);
					}
				}
			}
			
			/*if(entity != null)
			{
				movingobjectposition = new MovingObjectPosition(entity, vec3d1);
			}*/
			
			if(hitDistances.size() > 0)
			{
				LinkedList<MovingObjectPosition> positions = new LinkedList<MovingObjectPosition>();
				
				do
				{
					MovingObjectPosition closestPosition = null;
					double closestDistance = Double.POSITIVE_INFINITY;
					
					for(Entry<MovingObjectPosition, Double> entry : hitDistances.entrySet())
					{
						MovingObjectPosition position = entry.getKey();
						double distance = entry.getValue();
						
						if(distance <= closestDistance)
						{
							closestPosition = position;
							closestDistance = distance;
						}
					}
					
					positions.add(closestPosition);
					hitDistances.remove(closestPosition);
				}
				while(hitDistances.size() > 0);
				
				movingobjectposition = positions.toArray(new MovingObjectPosition[positions.size()]);
			}
		}

		if(movingobjectposition[0] != null && movingobjectposition[0].type != EnumMovingObjectType.MISS)
		{
			onBulletHit(movingobjectposition, bukkitShooter);
		}
		else
		{
			showTrace(location, location.clone().add(direction));
		}
	}

	@SuppressWarnings("deprecation")
	private static void onBulletHit(MovingObjectPosition[] mops, Player bukkitPlayer)
	{
		MSPlayer msPlayer = MSPlayer.get(bukkitPlayer);
		Location eyeLoc = bukkitPlayer.getEyeLocation();
		org.bukkit.World bukkitWorld = eyeLoc.getWorld();
		MovingObjectPosition lastMOP = mops[mops.length - 1];
		
		for(int i = 0; i < mops.length; i++)
		{
			MovingObjectPosition mop = mops[i];
			
			if(mop.type == EnumMovingObjectType.BLOCK)
			{
				Location blockLoc = new Location(bukkitWorld, mop.b, mop.c, mop.d);
				Block block = bukkitWorld.getBlockAt(blockLoc);
				Material type = block.getType();
				int id = type.getId();
				byte data = block.getData();
				Sound sound = getDigSound(type);
				Location hitLoc = new Location(bukkitWorld, mop.pos.c, mop.pos.d, mop.pos.e);
				
				ParticleEffect.displayBlockDust(hitLoc, id, data, 0, 0, 0, 0.1F, 25);
				bukkitWorld.playSound(hitLoc, sound, 1F, (float) (1 + Math.random()));
			}
			else if(mop.type == EnumMovingObjectType.ENTITY)
			{
				org.bukkit.entity.Entity rawBukkitVictim = mop.entity.getBukkitEntity();
				
				if(!(rawBukkitVictim instanceof LivingEntity))
					return;
				
				Game<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game = msPlayer.getGame();
				EquipmentProvider ep = game.getEquipmentManager();
				Equipment equipment = ep.getCurrentlyEquipped(msPlayer);
				
				if(!(equipment instanceof Gun))
					return;
				
				Gun gun = (Gun) equipment;
				GunType type = gun.getEquipment();
				double damageDivision = Math.pow(2, i);
				LivingEntity bukkitVictim = (LivingEntity) rawBukkitVictim;
				Location victimLoc = bukkitVictim.getLocation();
				double height = mop.entity.boundingBox.e - mop.entity.boundingBox.b;
				Location effectLoc = victimLoc.clone().add(0, height / 2, 0);
				double damage = type.getDamage() / damageDivision;
				EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(bukkitPlayer, bukkitVictim, DamageCause.CUSTOM, damage);
				PluginManager pm = Bukkit.getPluginManager();
				
				pm.callEvent(event);
				
				if(event.isCancelled())
					continue;
				
				double victimHealth = ((Damageable) bukkitVictim).getHealth() - damage;
				
				if(victimHealth < 0)
					victimHealth = 0;
				
				bukkitVictim.setHealth(victimHealth);
				ParticleEffect.displayBlockCrack(effectLoc, Material.REDSTONE_BLOCK.getId(), (byte) 0, 0, 0, 0, 1.5F, 20);
			}
		}
		
		Location endLoc = new Location(bukkitWorld, lastMOP.pos.c, lastMOP.pos.d, lastMOP.pos.e);
		showTrace(eyeLoc, endLoc);
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

	protected float i()
	{
		return 0.03F;
	}

	protected float e()
	{
		return 1.5F;
	}

	protected float f()
	{
		return 0.0F;
	}
}
