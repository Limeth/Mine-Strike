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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.BodyPart;
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
					MovingObjectPosition movingobjectposition1 = getIntersectionPoint(axisalignedbb, vec3d, vec3d1);

					if(movingobjectposition1 != null)
					{
						double curDistance = vec3d
								.distanceSquared(movingobjectposition1.pos);

						/*if((curDistance < d0) || (d0 == 0.0D))
						{
							entity = entity1;
							d0 = curDistance;
						}*/
						MovingObjectPosition entityMOP = new MovingObjectPosition(entity1, movingobjectposition1.pos);
						
						hitDistances.put(entityMOP, curDistance);
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
	
	public static MovingObjectPosition getIntersectionPoint(AxisAlignedBB bb, Vec3D paramVec3D1, Vec3D paramVec3D2)
	{
	    Vec3D localVec3D1 = paramVec3D1.b(paramVec3D2, bb.a);
	    Vec3D localVec3D2 = paramVec3D1.b(paramVec3D2, bb.d);

	    Vec3D localVec3D3 = paramVec3D1.c(paramVec3D2, bb.b);
	    Vec3D localVec3D4 = paramVec3D1.c(paramVec3D2, bb.e);

	    Vec3D localVec3D5 = paramVec3D1.d(paramVec3D2, bb.c);
	    Vec3D localVec3D6 = paramVec3D1.d(paramVec3D2, bb.f);
	    
	    Vec3D[] vectors = new Vec3D[]
	    		{
	    		localVec3D1,
	    		localVec3D2,
	    		localVec3D3,
	    		localVec3D4,
	    		localVec3D5,
	    		localVec3D6
	    		};
	    
	    Vec3D result = null;
	    Double minDistance = null;
	    
	    for(Vec3D vector : vectors)
	    {
	    	if(vector == null)
	    		continue;
	    	
	    	double distance = vector.distanceSquared(paramVec3D1);
	    	
	    	if(result == null || distance < minDistance)
	    	{
	    		result = vector;
	    		minDistance = distance;
	    	}
	    }
	    
	    return result != null ? new MovingObjectPosition(0, 0, 0, -1, result) : null;

/*	    System.out.println("1: " + localVec3D1);
	    System.out.println("2: " + localVec3D2);
	    System.out.println("3: " + localVec3D3);
	    System.out.println("4: " + localVec3D4);
	    System.out.println("5: " + localVec3D5);
	    System.out.println("6: " + localVec3D6);
	    
	    try
	    {
		    
		    Method b = AxisAlignedBB.class.getDeclaredMethod("b", Vec3D.class);
		    Method c = AxisAlignedBB.class.getDeclaredMethod("b", Vec3D.class);
		    Method d = AxisAlignedBB.class.getDeclaredMethod("b", Vec3D.class);
		    
		    b.setAccessible(true);
		    c.setAccessible(true);
		    d.setAccessible(true);
		    
		    if (!(Boolean) b.invoke(bb, localVec3D1)) localVec3D1 = null;
		    if (!(Boolean) b.invoke(bb, localVec3D2)) localVec3D2 = null;
		    if (!(Boolean) c.invoke(bb, localVec3D3)) localVec3D3 = null;
		    if (!(Boolean) c.invoke(bb, localVec3D4)) localVec3D4 = null;
		    if (!(Boolean) d.invoke(bb, localVec3D5)) localVec3D5 = null;
		    if (!(Boolean) d.invoke(bb, localVec3D6)) localVec3D6 = null;
		    
	    }
	    catch(Exception e)
	    {
	    	throw new RuntimeException(e);
	    }

	    Vec3D localVec3D7 = null;

	    if ((localVec3D1 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D1) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D1;
	    if ((localVec3D2 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D2) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D2;
	    if ((localVec3D3 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D3) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D3;
	    if ((localVec3D4 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D4) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D4;
	    if ((localVec3D5 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D5) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D5;
	    if ((localVec3D6 != null) && ((localVec3D7 == null) || (paramVec3D1.distanceSquared(localVec3D6) < paramVec3D1.distanceSquared(localVec3D7)))) localVec3D7 = localVec3D6;

	    if (localVec3D7 == null) return null;

	    int i = -1;

	    if (localVec3D7 == localVec3D1) i = 4;
	    if (localVec3D7 == localVec3D2) i = 5;
	    if (localVec3D7 == localVec3D3) i = 0;
	    if (localVec3D7 == localVec3D4) i = 1;
	    if (localVec3D7 == localVec3D5) i = 2;
	    if (localVec3D7 == localVec3D6) i = 3;

	    return new MovingObjectPosition(0, 0, 0, i, localVec3D7);*/
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
				Location hitLoc = new Location(bukkitWorld, mop.b, mop.c, mop.d);
				Block block = bukkitWorld.getBlockAt(hitLoc);
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
				
				Game<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game = msPlayer.getGame();
				EquipmentProvider ep = game.getEquipmentManager();
				Equipment equipment = ep.getCurrentlyEquipped(msPlayer);
				
				if(!(equipment instanceof Gun))
					return;
				
				Gun gun = (Gun) equipment;
				GunType type = gun.getEquipment();
				double damageDivision = Math.pow(2, i);
				Player bukkitVictim = (Player) rawBukkitVictim;
				MSPlayer msVictim = MSPlayer.get(bukkitVictim);
				Location effectLoc = new Location(bukkitWorld, mop.pos.c, mop.pos.d, mop.pos.e);
				double damage = type.getDamage() / damageDivision;
				double hitY = mop.pos.d;
				double victimY = mop.entity.locY;
				double relHitY = hitY - victimY;
				BodyPart bodyPart = BodyPart.getByY(relHitY);
				
				msVictim.damage(damage, msPlayer, gun, bodyPart);
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
