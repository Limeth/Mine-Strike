package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import net.minecraft.server.v1_7_R4.EntityPotion;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftThrownPotion;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;

public class Grenade
{
	private final GrenadeType type;
	private MSPlayer msShooter;
	private EntityGrenade nmsEntity;
	private boolean exploded;
	private Integer taskId;
	
	public Grenade(GrenadeType type, MSPlayer msShooter)
	{
		this.type = type;
		this.msShooter = msShooter;
	}
	
	public static Grenade throwGrenade(GrenadeType type, MSPlayer msShooter, Location loc, Vector vec)
	{
		Grenade grenade = new Grenade(type, msShooter);
		int color = type.getColor();
		GrenadeExplosionTrigger trigger = type.getTrigger();
		WorldServer nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
		EntityGrenade nmsEntity = new EntityGrenade(grenade, nmsWorld, loc.getX(), loc.getY(), loc.getZ(), color);
		nmsEntity.motX = vec.getX();
		nmsEntity.motY = vec.getY();
		nmsEntity.motZ = vec.getZ();
		
		nmsWorld.addEntity(nmsEntity);
		grenade.setNMSEntity(nmsEntity);
		
		if(trigger == GrenadeExplosionTrigger.TIMEOUT)
		{
			long ticksUntilExplosion = type.getTicksUntilExplosion();
			
			grenade.startCountdown(ticksUntilExplosion);
		}
		
		return grenade;
	}

	public static Grenade throwGrenade(GrenadeType type, MSPlayer msShooter, double force)
	{
		Player shooter = msShooter.getPlayer();
		Location loc = shooter.getEyeLocation();
		Vector movementVec = shooter.getVelocity();
		Vector direction = loc.getDirection();
		Vector vec = direction.clone().multiply(force).add(movementVec);
		
		loc.add(direction);
		
		return throwGrenade(type, msShooter, loc, vec);
	}
	
	public int startCountdown(long ticks)
	{
		
		return taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this::explode, ticks);
	}
	
	public boolean explode()
	{
		boolean spawn = type.onExplosion(this);
		exploded = true;
		
		return spawn;
	}
	
	public GrenadeType getType()
	{
		return type;
	}
	
	public ThrownPotion getEntity()
	{
		return (ThrownPotion) nmsEntity.getBukkitEntity();
	}
	
	public void setEntity(ThrownPotion entity)
	{
		EntityPotion nmsEntity = ((CraftThrownPotion) entity).getHandle();
		
		if(!(nmsEntity instanceof EntityGrenade))
			throw new IllegalArgumentException("The entity " + entity + " is not a grenade!");
		
		this.nmsEntity = (EntityGrenade) nmsEntity;
	}
	
	public EntityGrenade getNMSEntity()
	{
		return nmsEntity;
	}
	
	public void setNMSEntity(EntityGrenade nmsEntity)
	{
		this.nmsEntity = nmsEntity;
	}

	public Integer getTaskId()
	{
		return taskId;
	}
	
	public MSPlayer getShooter()
	{
		return msShooter;
	}

	public void setShooter(MSPlayer msShooter)
	{
		this.msShooter = msShooter;
	}

	public boolean hasExploded()
	{
		return exploded;
	}

	public void setExploded(boolean exploded)
	{
		this.exploded = exploded;
	}
}
