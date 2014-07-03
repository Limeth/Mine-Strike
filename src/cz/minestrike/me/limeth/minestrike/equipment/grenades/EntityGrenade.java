package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EntityPotion;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.Items;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.World;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class EntityGrenade extends EntityPotion//EntityProjectile
{
	private static final double frictionMultiplier = 0.5;
	private final Grenade grenade;
	public static final long countdown = 20 * 3;
	public ItemStack item;
	
	public EntityGrenade(Grenade grenade, World world)
	{
		super(world);
		
		this.grenade = grenade;
	}

	public EntityGrenade(Grenade grenade, World world, EntityLiving entityliving, ItemStack itemstack)
	{
		super(world, entityliving, itemstack);
		
		this.grenade = grenade;
	}

	public EntityGrenade(Grenade grenade, World world, EntityLiving entityliving, int i)
	{
		this(grenade, world, entityliving, new ItemStack(Items.POTION, 1, i));
	}

	public EntityGrenade(Grenade grenade, World world, double d0, double d1, double d2, ItemStack itemstack)
	{
		super(world, d0, d1, d2, itemstack);
		
		this.grenade = grenade;
	}

	public EntityGrenade(Grenade grenade, World world, double d0, double d1, double d2, int i)
	{
		this(grenade, world, d0, d1, d2, new ItemStack(Items.POTION, 1, i));
	}
	
	protected void a(MovingObjectPosition mop)
	{
		if(mop.type == EnumMovingObjectType.ENTITY)
			return;
		
		GrenadeType type = grenade.getType();
		GrenadeExplosionTrigger trigger = type.getTrigger();
		
		if(!grenade.hasExploded())
			if(trigger == GrenadeExplosionTrigger.LANDING)
			{
				if(mop.face == 1)
				{
					boolean spawn = grenade.explode();
					
					if(!spawn)
						return;
				}
			}
			else if(trigger == GrenadeExplosionTrigger.STABILIZATION)
			{
				if(mop.face == 1)
				{
					double length = Math.sqrt(motY * motY + motX * motX + motZ * motZ);
					
					if(length <= 0.1)
					{
						boolean spawn = grenade.explode();
						
						if(!spawn)
							return;
					}
				}
			}
		
		die();
		
		switch(mop.face / 2)
		{
		case 0: motY *= -1; break;
		case 1: motZ *= -1; break;
		case 2: motX *= -1; break;
		}

		motX *= frictionMultiplier;
		motY *= frictionMultiplier;
		motZ *= frictionMultiplier;
		
		int color = type.getColor();
		
		EntityGrenade entityGrenade = new EntityGrenade(grenade, world, locX, locY, locZ, color);
		entityGrenade.motX = motX;
		entityGrenade.motY = motY;
		entityGrenade.motZ = motZ;
		
		grenade.setNMSEntity(entityGrenade);
		world.addEntity(entityGrenade, SpawnReason.CUSTOM);
	}

	public Grenade getGrenade()
	{
		return grenade;
	}
}
