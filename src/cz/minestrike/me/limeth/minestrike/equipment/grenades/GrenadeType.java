package cz.minestrike.me.limeth.minestrike.equipment.grenades;

import java.util.List;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.DamageSource;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.containers.Container;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.TeamValue;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import darkBlade12.ParticleEffect;

public enum GrenadeType implements Equipment, DamageSource
{
	EXPLOSIVE(ChatColor.RED + "HE Grenade", 1, new TeamValue<Integer>(300), 240F, 16460, 40, "hegrenade", "he_draw")
	{
		public static final double RANGE = 6;
		public static final float DAMAGE = 96 * 20 / MSConstant.CS_MAX_HEALTH,
				ARMOR_DAMAGE_RATIO = 57F / (float) DAMAGE;
		
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			EntityGrenade entityGrenade = grenade.getNMSEntity();
			
			entityGrenade.die();
			
			MSPlayer msShooter = grenade.getShooter();
			ThrownPotion potion = grenade.getEntity();
			Location loc = potion.getLocation();
			List<Entity> targetEntities = potion.getNearbyEntities(RANGE, RANGE, RANGE);
			
			playExplosionEffect(loc, 1);
			
			for(Entity targetEntity : targetEntities)
			{
				if(!(targetEntity instanceof Player))
					continue;
				
				Location targetLoc = targetEntity.getLocation();
				double distance = loc.distance(targetLoc);
				
				if(distance > RANGE)
					continue;
				
				double damageModifier = (RANGE - distance) / RANGE;
				double damage = DAMAGE * damageModifier;
				Player target = (Player) targetEntity;
				MSPlayer msTarget = MSPlayer.get(target);
				
				msTarget.damage(damage, msShooter, this, null);
			}
			
			return false;
		}
		
		@Override
		public float getArmorDamageRatio()
		{
			return ARMOR_DAMAGE_RATIO;
		}
	},
	INCENDIARY(ChatColor.GOLD + "Incendiary Grenade", 1, new TeamValue<Integer>(400, 600, 500), 250F, 16453, GrenadeExplosionTrigger.LANDING, "incgrenade", "inc_grenade_draw")
	{
		private static final int yawSteps = 30, pitchSteps = 30, duration = 20 * 8;
		
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			grenade.getNMSEntity().die();
			
			ThrownPotion entity = grenade.getEntity();
			Location loc = entity.getLocation();
			IncendiaryEffect effect = new IncendiaryEffect();
			
			SoundManager.play("projectsurvive:counterstrike.weapons.incgrenade.inc_grenade_detonate", loc, Bukkit.getOnlinePlayers());
			
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
	DECOY(ChatColor.GRAY + "Decoy Grenade", 1, new TeamValue<Integer>(50), 250F, 16450, GrenadeExplosionTrigger.STABILIZATION, "decoy", "decoy_draw")
	{
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			MSPlayer msPlayer = grenade.getShooter();
			Scene scene = msPlayer.getScene();
			
			if(!(scene instanceof Game))
			{
				grenade.getNMSEntity().die();
				return false;
			}
			
			@SuppressWarnings("unchecked")
			Game<?, ?, ?, ? extends EquipmentProvider> game = (Game<?, ?, ?, ? extends EquipmentProvider>) scene;
			EquipmentProvider ep = game.getEquipmentProvider();
			Gun gun = ep.getGun(msPlayer, true);
			
			if(gun == null)
				gun = ep.getGun(msPlayer, false);
			
			if(gun == null)
			{
				grenade.getNMSEntity().die();
				return false;
			}
			
			final String sound = gun.getSoundShooting(msPlayer);
			
			new EffectHandler(grenade, sound).start();
			
			return true;
		}
		
		class EffectHandler implements Runnable
		{
			private static final double PLAY_CHANCE = 0.2;
			private final Grenade grenade;
			private final String sound;
			private Integer soundLoopId, limitLoopId;
			
			public EffectHandler(Grenade grenade, String sound)
			{
				this.grenade = grenade;
				this.sound = sound;
			}
			
			public EffectHandler start()
			{
				soundLoopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, 0, 4);
				limitLoopId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new Runnable()
				{
					public void run()
					{
						limitLoopId = null;
						explode();
					}
				}, 20L * 60);
				
				return this;
			}
			
			public void cancel()
			{
				if(limitLoopId != null)
				{
					Bukkit.getScheduler().cancelTask(limitLoopId);
					
					limitLoopId = null;
				}
				
				if(soundLoopId != null)
				{
					Bukkit.getScheduler().cancelTask(soundLoopId);
					
					soundLoopId = null;
				}
			}
			
			public void explode()
			{
				ThrownPotion potion = grenade.getEntity();
				Location loc = potion.getLocation();
				
				playExplosionEffect(loc, 0.5f);
				cancel();
				grenade.getNMSEntity().die();
			}
			
			@Override
			public void run()
			{
				if(Math.random() > PLAY_CHANCE)
					return;
				
				ThrownPotion entity = grenade.getEntity();
				Location loc = entity.getLocation();
				
				SoundManager.play(sound, loc, Bukkit.getOnlinePlayers());
			}
		}
	},
	SMOKE(ChatColor.GREEN + "Smoke Grenade", 1, new TeamValue<Integer>(300), 245F, 16452, GrenadeExplosionTrigger.STABILIZATION, "smokegrenade", "smokegrenade_draw")
	{
		@Override
		public boolean onExplosion(final Grenade grenade)
		{
			BukkitScheduler scheduler = Bukkit.getScheduler();
			MineStrike mineStrike = MineStrike.getInstance();
			ThrownPotion entity = grenade.getEntity();
			Location loc = entity.getLocation();
			
			SoundManager.play("projectsurvive:counterstrike.weapons.smokegrenade.sg_explode", loc, Bukkit.getOnlinePlayers());
			
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
	FLASH(ChatColor.AQUA + "Flashbang", 2, new TeamValue<Integer>(200), 240, 16419, 40, "flashbang", "flashbang_draw")
	{
		private static final double maxDistance = 32;
		private static final double maxDuration = 6; //seconds
		
		@Override
		public boolean onExplosion(Grenade grenade)
		{
			EntityGrenade entityGrenade = grenade.getNMSEntity();
			ThrownPotion potion = grenade.getEntity();
			Location loc = potion.getLocation();
			
			entityGrenade.die();
			ParticleEffect.FIREWORKS_SPARK.display(loc, 0, 0, 0, 0.75F, 50);
			SoundManager.play("projectsurvive:counterstrike.weapons.flashbang.flashbang_explode", loc, Bukkit.getOnlinePlayers());
			flashPlayers(loc, Bukkit.getOnlinePlayers());
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
	
	private final String name, directoryName, soundDraw;
	private final TeamValue<Integer> price;
	private final float movementSpeed;
	private final int color, maxAmount;
	private final Long ticksUntilExplosion;
	private final GrenadeExplosionTrigger trigger;
	
	private GrenadeType(String name, int maxAmount, TeamValue<Integer> price, float movementSpeed, int color, long ticksUntilExplosion, String directoryName, String soundDraw)
	{
		this.name = name;
		this.maxAmount = maxAmount;
		this.price = price;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.color = color;
		this.ticksUntilExplosion = ticksUntilExplosion;
		this.trigger = GrenadeExplosionTrigger.TIMEOUT;
		this.directoryName = directoryName;
		this.soundDraw = "projectsurvive:counterstrike.weapons." + directoryName + "." + soundDraw;
	}
	
	private GrenadeType(String name, int maxAmount, TeamValue<Integer> price, float movementSpeed, int color, GrenadeExplosionTrigger trigger, String directoryName, String soundDraw)
	{
		this.name = name;
		this.maxAmount = maxAmount;
		this.price = price;
		this.movementSpeed = movementSpeed * MSConstant.CS_UNITS_TO_METERS_PER_TICK_MODIFIER;
		this.color = color;
		ticksUntilExplosion = null;
		this.trigger = trigger;
		this.directoryName = directoryName;
		this.soundDraw = "projectsurvive:counterstrike.weapons." + directoryName + "." + soundDraw;
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
	
	@Override
	public boolean rightClick(MSPlayer msPlayer, org.bukkit.block.Block clickedBlock)
	{
		throwGrenade(msPlayer, 1);
		return true;
	}
	
	@Override
	public boolean leftClick(MSPlayer msPlayer, org.bukkit.block.Block clickedBlock)
	{
		throwGrenade(msPlayer, 0.2);
		return true;
	}
	
	private void throwGrenade(MSPlayer msPlayer, double force)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		int slot = inv.getHeldItemSlot();
		Container hotbarContainer = msPlayer.getHotbarContainer();
		
		Grenade.throwGrenade(this, msPlayer, force);
		player.setItemInHand(null);
		hotbarContainer.setItem(slot, null);
	}
	
	protected static void playExplosionEffect(Location loc, float strength)
	{
		Location effectLoc = loc.clone().add(0, 0.5, 0);
		float strengthSqrt = (float) Math.sqrt(strength);
		
		ParticleEffect.LAVA.display(effectLoc, 0.5F * strengthSqrt, 0.5F * strengthSqrt, 0.5F * strengthSqrt, 1, (int) (100 * strength));
		ParticleEffect.LARGE_SMOKE.display(effectLoc, 0, 0, 0, 1, (int) (25 * strength));
		SoundManager.play("projectsurvive:counterstrike.weapons.hegrenade.explode", loc, strength, Bukkit.getOnlinePlayers());
	}

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
		ItemStack is = new ItemStack(Material.POTION, 1, (short) color);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(getDisplayName());
		is.setItemMeta(im);
		
		return is;
	}
	
	public Integer getPrice(MSPlayer msPlayer)
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
			throw new IllegalArgumentException("The scene must be an instance of game.");
		
		Game<?, ?, ?, ?> game = (Game<?, ?, ?, ?>) scene;
		
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
	
	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Equipment.class;
	}
	
	@Override
	public Equipment getSource()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return getId();
	}
	
	@Override
	public float getArmorDamageRatio()
	{
		return 1;
	}
	
	public String getDirectoryName()
	{
		return directoryName;
	}
	
	@Override
	public String getSoundDraw()
	{
		return soundDraw;
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		return true;
	}
	
	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		return new FilledArrayList<ItemButton>();
	}
}
