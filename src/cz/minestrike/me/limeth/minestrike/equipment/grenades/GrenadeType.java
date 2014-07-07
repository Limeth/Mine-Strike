package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.BlockHalfTransparent;
import net.minecraft.server.v1_7_R1.BlockTransparent;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentType;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.games.TeamValue;
import cz.minestrike.me.limeth.minestrike.games.team.TeamGame;
import darkBlade12.ParticleEffect;

public enum GrenadeType implements EquipmentType
{
	EXPLOSIVE(ChatColor.RED + "HE Grenade", 1, new TeamValue<Integer>(300), 240F, 16460, 40)
	{
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			EntityGrenade entityGrenade = grenade.getNMSEntity();
			
			entityGrenade.die();
			
			ThrownPotion potion = grenade.getEntity();
			Location loc = potion.getLocation();
			org.bukkit.World world = loc.getWorld();
			Location effectLoc = loc.clone().add(0, 0.5, 0);
			
			ParticleEffect.LAVA.display(effectLoc, 0.5F, 0.5F, 0.5F, 1, 100);
			ParticleEffect.LARGE_SMOKE.display(effectLoc, 0, 0, 0, 1, 25);
			world.playSound(effectLoc, Sound.EXPLODE, 8F, 1F);
			
			return false;
		}
	},
	INCENDIARY(ChatColor.GOLD + "Incendiary Grenade", 1, new TeamValue<Integer>(400, 600, 500), 250F, 16453, GrenadeExplosionTrigger.LANDING)
	{
		private static final int yawSteps = 30, pitchSteps = 30, duration = 20 * 8;
		
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			grenade.getNMSEntity().die();
			
			ThrownPotion entity = grenade.getEntity();
			Location loc = entity.getLocation();
			World world = loc.getWorld();
			IncendiaryEffect effect = new IncendiaryEffect();
			
			world.playSound(loc, Sound.GLASS, 4F, (float) (0.5 + 0.5 * Math.random()));
			
			for(int pitchStep = 0; pitchStep < pitchSteps; pitchStep++)
				for(int yawStep = 0; yawStep < yawSteps; yawStep++)
				{
					float yaw = -180F + 360F * (float) yawStep * (1F + (float) Math.random() * 0.1F) / (float) yawSteps;
					float pitch = -90 + 180 * (float) pitchStep * (1F + (float) Math.random() * 0.1F) / (float) pitchSteps;
					Location curLoc = loc.clone();
					
					curLoc.setYaw(yaw);
					curLoc.setPitch(pitch);
					
					int curDuration = (int) (Math.sqrt(duration * duration * Math.random()));
					
					effect.addFlame(curLoc, curDuration);
				}
			
			effect.startTask();
			
			return false;
		}
	},
	DECOY(ChatColor.GRAY + "Decoy Grenade", 1, new TeamValue<Integer>(50), 250F, 16450, GrenadeExplosionTrigger.STABILIZATION)
	{
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			grenade.getNMSEntity().die();
			
			return false; //TODO true
		}
	},
	SMOKE(ChatColor.GREEN + "Smoke Grenade", 1, new TeamValue<Integer>(300), 245F, 16452, GrenadeExplosionTrigger.STABILIZATION)
	{
		@Override
		public boolean onExplosion(final Grenade grenade)
		{
			BukkitScheduler scheduler = Bukkit.getScheduler();
			MineStrike mineStrike = MineStrike.getInstance();
			ThrownPotion entity = grenade.getEntity();
			Location loc = entity.getLocation();
			World world = loc.getWorld();
			
			world.playSound(loc, Sound.FIREWORK_BLAST, 4F, (float) (1.5 + 0.5 * Math.random()));
			
			for(int i = 0; i < 20 * 8; i += 1)
			{
				final double smokeDistance = i < 40 ? i / 40.0 : 1;
				
				scheduler.scheduleSyncDelayedTask(mineStrike, new Runnable() {
					@Override
					public void run()
					{
						EntityGrenade entityGrenade = grenade.getNMSEntity();
						
						if(!entityGrenade.isAlive())
							return;
						
						ThrownPotion entity = grenade.getEntity();
						Location loc = entity.getLocation();
						World world = loc.getWorld();
						Location effectLoc = loc.clone().add(0, 1.5 * smokeDistance, 0);
						float spread = (float) (2F * smokeDistance);
						int particles = smokeDistance <= 0 ? 1 : (int) Math.ceil(100.0 * smokeDistance);
						
						ParticleEffect.CLOUD.display(effectLoc, spread, spread, spread, 0.2F, particles);
						world.playSound(loc, Sound.DIG_SAND, 0.5F, 1.5F);
					}
				}, i * 2);
			}
			
			scheduler.scheduleSyncDelayedTask(mineStrike, new Runnable() {
				@Override
				public void run()
				{
					EntityGrenade entityGrenade = grenade.getNMSEntity();
					
					if(!entityGrenade.isAlive())
						return;
					
					entityGrenade.die();
				}
			}, 20 * 8 * 2L);
			
			return true;
		}
	},
	FLASH(ChatColor.AQUA + "Flashbang", 2, new TeamValue<Integer>(200), 240, 16419, 40)
	{
		private static final double maxDistance = 32;
		private static final double maxDuration = 6; //seconds
		
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			EntityGrenade entityGrenade = grenade.getNMSEntity();
			ThrownPotion potion = grenade.getEntity();
			Location loc = potion.getLocation();
			org.bukkit.World world = loc.getWorld();
			
			entityGrenade.die();
			ParticleEffect.FIREWORKS_SPARK.display(loc, 0, 0, 0, 0.75F, 50);
			
			for(int i = 0; i < 10; i++)
				((CraftWorld) world).playSound(loc, Sound.ORB_PICKUP, 8F, 0.5F + 1.5F * (float) (Math.random()));
			
			flashPlayers(loc, Bukkit.getOnlinePlayers()); //TODO only players in game
			return false;
		}
		
		public void flashPlayers(Location loc, Player[] players)
		{
			World world = loc.getWorld();
			WorldServer nmsWorld = ((CraftWorld) world).getHandle();
			Vec3D oVec = nmsWorld.getVec3DPool().create(loc.getX(), loc.getY(), loc.getZ());
			
			for(Player player : players)
			{
				Location pLoc = player.getEyeLocation();
				Vec3D pVec = nmsWorld.getVec3DPool().create(pLoc.getX(), pLoc.getY(), pLoc.getZ());
				MovingObjectPosition mop = nmsWorld.a(oVec, pVec);
				boolean cont = false;
				
				while(mop != null)
				{
					if(mop.type == EnumMovingObjectType.BLOCK)
					{
						Block block = nmsWorld.getType(mop.b, mop.c, mop.d);
						
						if(block instanceof BlockTransparent || block instanceof BlockHalfTransparent)
						{
							pVec = nmsWorld.getVec3DPool().create(mop.pos.c, mop.pos.d, mop.pos.e);
							mop = nmsWorld.a(oVec, pVec);
							
							continue;
						}
					}
					
					if(mop.type != EnumMovingObjectType.MISS)
					{
						cont = true;
						break;
					}
				}
				
				if(cont)
					continue;
				
				double distance = pLoc.distance(loc);
				
				if(distance > maxDistance)
					continue;
				
				Vector dVec = pLoc.getDirection();
				double directionMultiplier = getDirectionMultiplier(pLoc, loc, dVec);
				
				int duration = (int) ((1 - distance / maxDistance) * maxDuration * directionMultiplier * 20.0);
				boolean hasLongerLastingEffect = false;
				
				for(PotionEffect effect : player.getActivePotionEffects())
				{
					PotionEffectType type = effect.getType();
					
					if(type != PotionEffectType.BLINDNESS)
						continue;
					
					int curDuration = effect.getDuration();
					
					if(curDuration > duration)
					{
						hasLongerLastingEffect = true;
						break;
					}
				}
				
				if(hasLongerLastingEffect)
					continue;
				
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, true), true);
			}
		}
		
		public double getDirectionMultiplier(Location from, Location to, Vector direction)
		{
			Vector difference = to.toVector().subtract(from.toVector());
			difference.multiply(1 / difference.length());
			difference.subtract(direction);
			
			return (2.0 - difference.length()) / 2.0;
		}
	};
	
	private final String name;
	private final TeamValue<Integer> price;
	private final float movementSpeed;
	private final int color, maxAmount;
	private final Long ticksUntilExplosion;
	private final GrenadeExplosionTrigger trigger;
	
	private GrenadeType(String name, int maxAmount, TeamValue<Integer> price, float movementSpeed, int color, long ticksUntilExplosion)
	{
		this.name = name;
		this.maxAmount = maxAmount;
		this.price = price;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.color = color;
		this.ticksUntilExplosion = ticksUntilExplosion;
		this.trigger = GrenadeExplosionTrigger.TIMEOUT;
	}
	
	private GrenadeType(String name, int maxAmount, TeamValue<Integer> price, float movementSpeed, int color, GrenadeExplosionTrigger trigger)
	{
		this.name = name;
		this.maxAmount = maxAmount;
		this.price = price;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.color = color;
		ticksUntilExplosion = null;
		this.trigger = trigger;
	}
	
	public static GrenadeType valueOf(ItemStack is)
	{
		if(is == null)
			return null;
		
		Material type = is.getType();
		
		if(type != Material.POTION)
			return null;
		
		short durability = is.getDurability();
		
		for(GrenadeType grenade : values())
			if(grenade.getColor() == durability)
				return grenade;
		
		return null;
	}
	
	public abstract boolean onExplosion(Grenade grenade);

	public int getColor()
	{
		return color;
	}

	public Long getTicksUntilExplosion()
	{
		return ticksUntilExplosion;
	}

	public GrenadeExplosionTrigger getTrigger()
	{
		return trigger;
	}
	
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return new ItemStack(Material.POTION, 1, (short) color);
	}
	
	public int getPrice(MSPlayer msPlayer)
	{
		Game<?, ?, ?, ?> game = msPlayer.getGame();
		
		if(!(game instanceof TeamGame))
			return price.getNone();
		else
		{
			TeamGame<?, ?, ?, ?> teamGame = (TeamGame<?, ?, ?, ?>) game;
			Team team = teamGame.getTeam(msPlayer);
			
			return price.get(team);
		}
	}
	
	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return movementSpeed;
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDisplayName()
	{
		return name;
	}

	public int getMaxAmount()
	{
		return maxAmount;
	}
	
	@Override
	public String getId()
	{
		return "GRENADE_" + name();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Equipment.class;
	}
}
