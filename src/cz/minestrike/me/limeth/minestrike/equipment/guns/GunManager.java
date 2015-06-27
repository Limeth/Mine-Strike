package cz.minestrike.me.limeth.minestrike.equipment.guns;

import com.google.common.collect.Sets;
import cz.minestrike.me.limeth.minestrike.*;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.events.BlockShotEvent;
import cz.minestrike.me.limeth.minestrike.events.PlayerShotEvent;
import cz.minestrike.me.limeth.minestrike.util.BoundUtil;
import darkBlade12.ParticleEffect;
import net.minecraft.server.v1_7_R4.EnumMovingObjectType;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.Set;

public class GunManager
{
	public static void shootBullet(MSPlayer msPlayer, Location location, Vector direction, double range, double penetration, Set<Object> hitObjects)
	{
		Vector deltaLocation = direction.clone().multiply(range);
		Player player = msPlayer.getPlayer();
		MovingObjectPosition[] obstacles = BoundUtil.findObstaclesByMotion(player, location, deltaLocation);
		World world = location.getWorld();
		Location endLocation = null;
		Object hitObject = null;
		HitResult hitResult = null;
		boolean uniqueHit = false;

		if(obstacles.length > 0)
		{
			MovingObjectPosition firstObstacle = obstacles[0];
			hitObject = getHitObject(firstObstacle, world);
			uniqueHit = hitObject != null && hitObjects.add(hitObject);

			if(uniqueHit)
				hitResult = GunManager.onBulletHit(firstObstacle, msPlayer, penetration);

			endLocation = new Location(world, firstObstacle.pos.a, firstObstacle.pos.b, firstObstacle.pos.c);
		}

		if(endLocation != null)
		{
			double nextPenetration = hitResult == null ? penetration : (penetration * hitResult.penetrationModifier);

			if(nextPenetration > 0)
			{
				double remainingRange = range - location.distance(endLocation);

				if(remainingRange > 0)
				{
					Location nextLocation = endLocation.clone().add(direction);

					shootBullet(msPlayer, nextLocation, direction, remainingRange, nextPenetration, hitObjects);
				}
			}
		}
		else
		{
			endLocation = location.clone().add(deltaLocation);
		}

		GunManager.showTrace(location, endLocation, 1 / penetration);
	}

	public static void shootBullet(MSPlayer msPlayer, Location location, Vector direction, double range)
	{
		shootBullet(msPlayer, location, direction, range, 1, Sets.newHashSet());
	}

	public static void showTrace(Location from, Location to, double stepSize)
	{
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
	public static HitResult onBulletHit(MovingObjectPosition mop, MSPlayer msPlayer, double penetration)
	{
		Player player = msPlayer.getPlayer();
		Location eyeLoc = player.getEyeLocation();
		org.bukkit.World bukkitWorld = eyeLoc.getWorld();
		PluginManager pm = Bukkit.getPluginManager();
		HotbarContainer hotbarContainer = msPlayer.getHotbarContainer();
		Equipment equipment = hotbarContainer.getHeld(msPlayer);

		if(!(equipment instanceof Gun))
			throw new IllegalStateException("The shooting player seems to not be holding a gun.");

		Gun gun = (Gun) equipment;
		GunType gunType = gun.getEquipment();
		Location hitLoc = new Location(bukkitWorld, mop.pos.a, mop.pos.b, mop.pos.c);
		double distance = eyeLoc.distance(hitLoc);
		double rangeModifier = gunType.getRangeModifier(msPlayer);
		double damage = gunType.getDamage(msPlayer);

		damage *= Math.pow(rangeModifier, distance / (500 * MSConstant.CS_UNITS_TO_METERS_MODIFIER));
		damage *= penetration;

		if(mop.type == EnumMovingObjectType.BLOCK)
		{
			Block block = bukkitWorld.getBlockAt(mop.b, mop.c, mop.d);
			BlockShotEvent event = new BlockShotEvent(msPlayer, hitLoc, damage, block);

			pm.callEvent(event);

			double currentPenetration = event.getPenetration();

			return new HitResult(currentPenetration, block);

		}
		else if(mop.type == EnumMovingObjectType.ENTITY)
		{
			org.bukkit.entity.Entity rawBukkitVictim = mop.entity.getBukkitEntity();

			if(!(rawBukkitVictim instanceof Player))
				return new HitResult(1, null);

			Player bukkitVictim = (Player) rawBukkitVictim;
			MSPlayer msVictim = MSPlayer.get(bukkitVictim);
			PlayerShotEvent event = new PlayerShotEvent(msPlayer, hitLoc, damage, msVictim);

			pm.callEvent(event);

			double currentPenetration = event.getPenetration();

			return new HitResult(currentPenetration, rawBukkitVictim);
		}

		throw new IllegalStateException("Invalid MovingObjectPosition; type: " + mop.type);
	}

	private static Object getHitObject(MovingObjectPosition mop, World world)
	{
		if(mop.type == EnumMovingObjectType.BLOCK)
			return world.getBlockAt(mop.b, mop.c, mop.d);
		else if(mop.type == EnumMovingObjectType.ENTITY)
			return mop.entity;
		else
			return null;
	}

	private static class HitResult
	{
		public final double penetrationModifier;
		public final Object hitObject;

		public HitResult(double penetrationModifier, Object hitObject)
		{
			this.penetrationModifier = penetrationModifier;
			this.hitObject = hitObject;
		}

		public HitResult(double penetrationModifier, MovingObjectPosition mop, World world)
		{
			this(penetrationModifier, getHitObject(mop, world));
		}
	}
}
