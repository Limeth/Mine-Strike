package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.DamageRecord;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EnumMovingObjectType;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import darkBlade12.ParticleEffect;

public class IncendiaryEffect
{
	private static final long period = 2;
	private static final double damageRange = 1, damageMultiplier = 0.1, strength = 1, weight = 0.25;
	private LinkedList<IncendiaryFlame> flames;
	private Integer taskId;
	private int maxDuration, step;
	private double damageModifier;
	private final Random random;
	private MSPlayer shooter;
	
	public IncendiaryEffect(MSPlayer shooter, double damageModifier)
	{
		flames = Lists.newLinkedList();
		random = new Random();
		this.damageModifier = damageModifier;
		this.setShooter(shooter);
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
		
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), () -> {
            that.task();

            if(that.incrementStep() * period > maxDuration)
                that.cancelTask();
        }, 0L, period);
	}
	
	@SuppressWarnings("unchecked")
	public void task()
	{
		long time = step * period;
		
		playSound();
		
		Iterator<IncendiaryFlame> iterator = flames.iterator();
		HashMap<HumanEntity, Double> damages = Maps.newHashMap();
		
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
				
				if(!(entity instanceof HumanEntity))
					continue;
				
				HumanEntity livingEntity = (HumanEntity) entity;
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
		
		InventoryContainer container = shooter.getInventoryContainer();
		Equipment weapon = container.getEquippedCustomizedEquipment(GrenadeType.INCENDIARY);
		
		if(weapon == null)
			weapon = GrenadeType.INCENDIARY;
		
		for(Entry<HumanEntity, Double> entry : damages.entrySet())
		{
			HumanEntity entity = entry.getKey();
			MSPlayer msPlayer = MSPlayer.get((Player) entity);
			double damage = entry.getValue();
			double curDamage = damage * damageMultiplier * damageModifier;
			DamageRecord damageRecord = new DamageRecord(shooter, weapon, null, false, curDamage);

			msPlayer.damage(damageRecord);
			entity.setFireTicks(5);
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
	
	public MSPlayer getShooter()
	{
		return shooter;
	}

	public void setShooter(MSPlayer shooter)
	{
		this.shooter = shooter;
	}

	@SuppressWarnings("unused")
	private class IncendiaryFlame
	{
		private final Location $tempLoc = new Location(null, 0, 0, 0);
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
			
			$tempLoc.setX(loc.getX());
			$tempLoc.setY(loc.getY() + 0.125);
			$tempLoc.setZ(loc.getZ());
			$tempLoc.setWorld(loc.getWorld());
			
			ParticleEffect.INSTANT_SPELL.display($tempLoc, 0.5f, 0.25f, 0.5f, 0.01f, 8);
		}
		
		public void move()
		{
			MovingObjectPosition result;
			World world = loc.getWorld();
			WorldServer nmsWorld = ((CraftWorld) world).getHandle();
			double locX = loc.getX();
			double locY = loc.getY();
			double locZ = loc.getZ();
			double motX = vec.getX();
			double motY = vec.getY();
			double motZ = vec.getZ();
			
			Vec3D origin = Vec3D.a(locX, locY, locZ);
			Vec3D destination = Vec3D.a(locX + motX, locY + motY, locZ + motZ);
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
				loc = new Location(world, result.pos.a, result.pos.b, result.pos.c);
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
