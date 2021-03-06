package cz.minestrike.me.limeth.minestrike.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.util.Vector;

/**
 * Made with CB 1.7.2 R0.3
 */
public class BoundUtil
{
	private BoundUtil() {}
	
	public static Vec3D getVec3D(Location location)
	{
		return Vec3D.a(location.getX(), location.getY(), location.getZ());
//		return ((CraftWorld) location.getWorld()).getHandle().getVec3DPool()
//				.create(location.getX(), location.getY(), location.getZ());
	}
	
	public static Vec3D getVec3D(Vector vector)
	{
		return Vec3D.a(vector.getX(), vector.getY(), vector.getZ());
//		return Vec3D.a.create(vector.getX(), vector.getY(), vector.getZ());
	}
	
	public static Location getLocation(org.bukkit.World world, Vec3D vec3D)
	{
		return new Location(world, vec3D.a, vec3D.b, vec3D.c);
	}
	
	public static Vec3D getIntersectionPoint(AxisAlignedBB box, Vec3D pointA, Vec3D pointB)
	{
	    Vec3D localVec3D1 = pointA.b(pointB, box.a);
	    Vec3D localVec3D2 = pointA.b(pointB, box.d);

	    Vec3D localVec3D3 = pointA.c(pointB, box.b);
	    Vec3D localVec3D4 = pointA.c(pointB, box.e);

	    Vec3D localVec3D5 = pointA.d(pointB, box.c);
	    Vec3D localVec3D6 = pointA.d(pointB, box.f);
	    
	    try
	    {
		    
		    Method b = AxisAlignedBB.class.getDeclaredMethod("b", Vec3D.class);
		    Method c = AxisAlignedBB.class.getDeclaredMethod("c", Vec3D.class);
		    Method d = AxisAlignedBB.class.getDeclaredMethod("d", Vec3D.class);
		    
		    b.setAccessible(true);
		    c.setAccessible(true);
		    d.setAccessible(true);
		    
		    if (!(Boolean) b.invoke(box, localVec3D1)) localVec3D1 = null;
		    if (!(Boolean) b.invoke(box, localVec3D2)) localVec3D2 = null;
		    if (!(Boolean) c.invoke(box, localVec3D3)) localVec3D3 = null;
		    if (!(Boolean) c.invoke(box, localVec3D4)) localVec3D4 = null;
		    if (!(Boolean) d.invoke(box, localVec3D5)) localVec3D5 = null;
		    if (!(Boolean) d.invoke(box, localVec3D6)) localVec3D6 = null;
		    
	    }
	    catch(Exception e)
	    {
	    	throw new RuntimeException(e);
	    }
	    
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
	    	
	    	double distance = vector.distanceSquared(pointA);
	    	
	    	if(result == null || distance < minDistance)
	    	{
	    		result = vector;
	    		minDistance = distance;
	    	}
	    }
	    
	    return result;
	}
	
	private static MovingObjectPosition[] findObstacles(Entity excludedEntity, World world, Vec3D from, Vec3D to, Vec3D mot)
	{
		Vec3D fromClone = Vec3D.a(from.a, from.b, from.c);
		Vec3D toClone = Vec3D.a(to.a, to.b, to.c);
		
		MovingObjectPosition blockObstacle = world.a(fromClone, toClone);
		MovingObjectPosition[] entityObstacles = null;
		
		if(blockObstacle != null)
			to = Vec3D.a(
					blockObstacle.pos.a,
					blockObstacle.pos.b,
					blockObstacle.pos.c);
		
		if(!world.isStatic)
		{
			HashMap<MovingObjectPosition, Double> hitDistances = new HashMap<MovingObjectPosition, Double>();
			AxisAlignedBB targetBoundingBox = AxisAlignedBB
					.a(0, 0, 0, 0, 0, 0)
					.d(from.a, from.b, from.c)
					.a(mot.a, mot.b, mot.c)
					.grow(1, 1, 1);
			
			@SuppressWarnings("unchecked")
			List<? extends Entity> entitiesInBB = world.getEntities(
					excludedEntity,
					targetBoundingBox
			);
			
			for(Entity entityInBB : entitiesInBB)
			{
				if(entityInBB.R() && entityInBB != excludedEntity)
				{
					AxisAlignedBB axisalignedbb = entityInBB.boundingBox.clone();
					Vec3D intersection = BoundUtil.getIntersectionPoint(axisalignedbb, from, to);
					
					if(intersection != null)
					{
						double curDistance = from.distanceSquared(intersection);
						MovingObjectPosition obstacle = new MovingObjectPosition(entityInBB, intersection);
						
						hitDistances.put(obstacle, curDistance);
					}
				}
			}
			
			if(hitDistances.size() > 0)
			{
				LinkedList<MovingObjectPosition> dynamicObstacles = new LinkedList<MovingObjectPosition>();
				
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
					
					dynamicObstacles.add(closestPosition);
					hitDistances.remove(closestPosition);
				}
				while(hitDistances.size() > 0);
				
				entityObstacles = dynamicObstacles.toArray(new MovingObjectPosition[dynamicObstacles.size()]);
			}
		}
		
		//First entities, then block
		
		MovingObjectPosition[] obstacles;
		
		if(blockObstacle != null)
			if(entityObstacles != null)
			{
				obstacles = new MovingObjectPosition[entityObstacles.length + 1];
				obstacles[obstacles.length - 1] = blockObstacle;
				
				System.arraycopy(entityObstacles, 0, obstacles, 0, entityObstacles.length);
			}
			else
				obstacles = new MovingObjectPosition[] { blockObstacle };
		else
			obstacles = entityObstacles != null ? entityObstacles : new MovingObjectPosition[0];
		
		return obstacles;
	}
	
	public static MovingObjectPosition[] findObstaclesByDestination(Entity excludedEntity, World world, Vec3D from, Vec3D to)
	{
		return findObstacles(excludedEntity, world, from, to, Vec3D.a(to.a - from.a, to.b - from.b, to.c - from.c));
	}
	
	public static MovingObjectPosition[] findObstaclesByMotion(Entity excludedEntity, World world, Vec3D from, Vec3D mot)
	{
		return findObstacles(excludedEntity, world, from, Vec3D.a(from.a + mot.a, from.b + mot.b, from.c + mot.c), mot);
	}
	
	public static MovingObjectPosition[] findObstaclesByDestination(org.bukkit.entity.Entity excludedEntity, Location from, Location to)
	{
		if(from.getWorld() != to.getWorld())
			throw new IllegalArgumentException("The worlds of the locations are different.");
		
		return findObstaclesByDestination(((CraftEntity) excludedEntity).getHandle(),
				((CraftWorld) from.getWorld()).getHandle(),
				getVec3D(from),
				getVec3D(to));
	}
	
	public static MovingObjectPosition[] findObstaclesByMotion(org.bukkit.entity.Entity excludedEntity, Location from, Vector motion)
	{
		return findObstaclesByMotion(((CraftEntity) excludedEntity).getHandle(),
				((CraftWorld) from.getWorld()).getHandle(),
				getVec3D(from),
				getVec3D(motion));
	}
}
