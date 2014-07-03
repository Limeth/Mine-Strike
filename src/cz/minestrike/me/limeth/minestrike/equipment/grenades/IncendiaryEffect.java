package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MineStrike;
import darkBlade12.ParticleEffect;

public class IncendiaryEffect
{
	private static final long period = 2;
	private static final double damageRange = 1, damageMultiplier = 0.75, strength = 1, weight = 0.25;
	private LinkedList<IncendiaryFlame> flames;
	private Integer taskId;
	private int maxDuration, step;
	private final Random random;
	
	public IncendiaryEffect()
	{
		flames = new LinkedList<IncendiaryFlame>();
		random = new Random();
	}
	
	public void addFlame(Location loc, int duration)
	{
		IncendiaryFlame flame = new IncendiaryFlame(loc, duration);
		
		flames.add(flame);
		
		if(duration > maxDuration)
			maxDuration = duration;
	}
	
	public void startTask()
	{
		step = 0;
		final IncendiaryEffect that = this;
		
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				that.task();
				
				if(that.incrementStep() * period > maxDuration)
				{
					that.cancelTask();
					return;
				}
			}
		}, 0L, period);
	}
	
	@SuppressWarnings("unchecked")
	public void task()
	{
		long time = step * period;
		
		playSound();
		
		Iterator<IncendiaryFlame> iterator = flames.iterator();
		HashMap<LivingEntity, Double> damages = new HashMap<LivingEntity, Double>();
		
		while(iterator.hasNext())
		{
			IncendiaryFlame flame = iterator.next();
			Location loc = flame.getLocation();
			World world = loc.getWorld();
			WorldServer nmsWorld = ((CraftWorld) world).getHandle();
			List<Entity> nmsEntities = nmsWorld.getEntities(null, AxisAlignedBB.a(loc.getX() - damageRange, loc.getY() - damageRange, loc.getZ() - damageRange, loc.getX() + damageRange, loc.getY() + damageRange, loc.getZ() + damageRange));
			
			for(Entity nmsEntity : nmsEntities)
			{
				org.bukkit.entity.Entity entity = nmsEntity.getBukkitEntity();
				
				if(!(entity instanceof LivingEntity))
					continue;
				
				LivingEntity livingEntity = (LivingEntity) entity;
				Double curDamage = damages.get(livingEntity);
				
				if(curDamage == null)
					curDamage = 0.0;
				
				Location entityLoc = livingEntity.getLocation();
				double newDamage = damageRange - entityLoc.distance(loc);
				
				if(newDamage > 0)
					curDamage += newDamage;
				
				damages.put(livingEntity, curDamage);
			}
			
			flame.task();
			
			if(time > flame.getDuration())
				iterator.remove();
		}
		
		for(Entry<LivingEntity, Double> entry : damages.entrySet())
		{
			LivingEntity entity = entry.getKey();
			double damage = entry.getValue();
			double curDamage = damage * damageMultiplier;
			double curFireTicks = entity.getFireTicks();
			
			if(curFireTicks < curDamage)
				entity.setFireTicks((int) (curDamage * 20));
		}
		
		if(flames.size() <= 0)
			cancelTask();
	}
	
	public void playSound()
	{
		IncendiaryFlame flame = flames.get(random.nextInt(flames.size()));
		Location loc = flame.getLocation();
		World world = loc.getWorld();
		Sound sound = Sound.FIRE;
		
		world.playSound(loc, sound, 1F, (float) (0.5F + 1.5 * random.nextDouble()));
	}
	
	public boolean cancelTask()
	{
		if(taskId == null)
			return false;
		
		Bukkit.getScheduler().cancelTask(taskId);
		return true;
	}
	
	public Integer getTaskId()
	{
		return taskId;
	}

	public int getStep()
	{
		return step;
	}

	public int incrementStep()
	{
		return step++;
	}
	
	@SuppressWarnings("unused")
	private class IncendiaryFlame
	{
		private Location loc;
		private Vector vec;
		private int duration;
		private boolean landed;
		
		public IncendiaryFlame(Location loc, int duration)
		{
			this.loc = loc;
			this.duration = duration;
			vec = loc.getDirection().clone().multiply(strength);
		}
		
		public void task()
		{
			if(!landed)
				move();
			
			ParticleEffect.FLAME.display(loc, 0, 0, 0, (float) (0.03 * Math.random()), 1);
		}
		
		public void move()
		{
			MovingObjectPosition result = null;
			World world = loc.getWorld();
			WorldServer nmsWorld = ((CraftWorld) world).getHandle();
			double locX = loc.getX();
			double locY = loc.getY();
			double locZ = loc.getZ();
			double motX = vec.getX();
			double motY = vec.getY();
			double motZ = vec.getZ();
			
			Vec3D origin = nmsWorld.getVec3DPool().create(locX, locY, locZ);
			Vec3D destination = nmsWorld.getVec3DPool().create(locX + motX, locY + motY, locZ + motZ);
			result = nmsWorld.a(origin, destination);
			
		/*	Location debugLoc = new Location(world, locX, locY, locZ);
			Vector change = new Vector(motX, motY, motZ).multiply(0.25);
			
			for(int i = 0; i < 4; i++)
			{
				ParticleEffect.TOWN_AURA.display(debugLoc, 0, 0, 0, 1, 1);
				debugLoc.add(change);
			}*/
			
			loc.add(motX, motY, motZ);
			vec.setY(vec.getY() - weight);
			
			if(result != null && result.type == EnumMovingObjectType.BLOCK)
			{
				loc = new Location(world, result.pos.c, result.pos.d, result.pos.e);
				landed = true;
			}
		}
		
		public Location getLocation()
		{
			return loc;
		}

		public void setLocation(Location loc)
		{
			this.loc = loc;
		}

		public int getDuration()
		{
			return duration;
		}

		public void setDuration(int duration)
		{
			this.duration = duration;
		}

		public boolean isLanded()
		{
			return landed;
		}

		public void setLanded(boolean landed)
		{
			this.landed = landed;
		}
	}
}
