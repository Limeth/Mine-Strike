package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.util.Vector;

import java.util.Set;

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

	@Deprecated
	public EntityGrenade(Grenade grenade, World world, double d0, double d1, double d2, int i)
	{
		this(grenade, world, d0, d1, d2, new ItemStack(Items.POTION, 1, i));
	}

	public static EntityGrenade spawn(MSPlayer msPlayer, Grenade grenade, Location location, Vector velocity)
	{
		GrenadeType type = grenade.getType();
		int color = type.getColor();
		org.bukkit.inventory.ItemStack bukkitItemStack = type.newItemStack(msPlayer);
		ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bukkitItemStack);
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		EntityGrenade nmsEntity = new EntityGrenade(grenade, nmsWorld, location.getX(), location.getY(), location.getZ(), nmsItemStack);
		nmsEntity.motX = velocity.getX();
		nmsEntity.motY = velocity.getY();
		nmsEntity.motZ = velocity.getZ();

		nmsWorld.addEntity(nmsEntity);

		return nmsEntity;
	}
	
	protected void a(MovingObjectPosition mop)
	{
		if(mop.type == EnumMovingObjectType.ENTITY)
			return;
		
		GrenadeType type = grenade.getType();
		GrenadeExplosionTrigger trigger = type.getTrigger();
        double speedPerTickSquared = motY * motY + motX * motX + motZ * motZ;
		
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
					if(speedPerTickSquared <= 0.01)
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

        MSPlayer msShooter = grenade.getShooter();
        Scene scene = msShooter.getScene();
        String soundBounce = type.getSoundBounce();
		
		grenade.setNMSEntity(entityGrenade);
		world.addEntity(entityGrenade, SpawnReason.CUSTOM);

        if(speedPerTickSquared > 0.01)
            scene.playSound(soundBounce, locX, locY, locZ, (float) speedPerTickSquared);
	}

	public Grenade getGrenade()
	{
		return grenade;
	}
}
