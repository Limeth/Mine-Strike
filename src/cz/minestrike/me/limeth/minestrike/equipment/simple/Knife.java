package cz.minestrike.me.limeth.minestrike.equipment.simple;

import cz.minestrike.me.limeth.minestrike.*;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.BoundUtil;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import darkBlade12.ParticleEffect;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EnumMovingObjectType;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.util.Vector;

public class Knife extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta) item.getItemMeta();
		
		im.setDisplayName(Translation.EQUIPMENT_KNIFE.getMessage());
		item.setItemMeta(im);
		
		ITEM = item;
		KNIFE = new Knife();
	}
	
	public static final ItemStack ITEM;
	public static final Knife KNIFE;
	public static final String SOUND_DRAW = "projectsurvive:counterstrike.weapons.knife.knife_deploy",
			SOUND_SWING = "projectsurvive:counterstrike.weapons.knife.knife_slash",
			SOUND_SLASH = "projectsurvive:counterstrike.weapons.knife.knife_hit",
			SOUND_STAB = "projectsurvive:counterstrike.weapons.knife.knife_stab",
			SOUND_WALL = "projectsurvive:counterstrike.weapons.knife.knife_hitwall",
			DATA_BLOCKED_UNTIL = "MineStrike.equipment.knife.cooldown";
	public static final double DAMAGE_STAB = 13, DAMAGE_SLASH = 5, RANGE = 1;
	public static final long DELAY_STAB = 1000, DELAY_SLASH = 400;
	
	private Knife()
	{
		super("KNIFE", ITEM, EquipmentCategory.KNIFES, false, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, SOUND_DRAW, false, false);
	}
	
	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		swing(msPlayer, DAMAGE_STAB, DELAY_STAB, SOUND_STAB);
		return true;
	}
	
	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		swing(msPlayer, DAMAGE_SLASH, DELAY_SLASH, SOUND_SLASH);
		return true;
	}
	
	public void swing(MSPlayer msPlayer, double damage, long delay, String damageSound)
	{
		long now = System.currentTimeMillis();
		
		if(hasCooldown(msPlayer, now))
			return;
		
		setCooldown(msPlayer, now, delay);
		
		Player player = msPlayer.getPlayer();
		World world = player.getWorld();
		Location from = player.getEyeLocation();
		Vector motion = from.getDirection().multiply(RANGE);
		MovingObjectPosition[] obstacles = BoundUtil.findObstaclesByMotion(player, from, motion);
		
		for(MovingObjectPosition obstacle : obstacles)
			if(obstacle != null && obstacle.type != EnumMovingObjectType.MISS)
				if(obstacle.type == EnumMovingObjectType.ENTITY && obstacle.entity instanceof EntityPlayer)
				{
					EntityPlayer nmsVictim = (EntityPlayer) obstacle.entity;
					Location hitLocation = new Location(world, obstacle.pos.a, obstacle.pos.b, obstacle.pos.c);
					Player victim = nmsVictim.getBukkitEntity();
					MSPlayer msVictim = MSPlayer.get(victim);
					double hitY = obstacle.pos.b;
					double victimY = obstacle.entity.locY;
					double relHitY = hitY - victimY;
					BodyPart bodyPart = BodyPart.getByY(relHitY);
					Equipment customizedKnife = msPlayer.getEquipmentInHand();
					DamageRecord damageRecord = new DamageRecord(msPlayer, customizedKnife, bodyPart, false, damage);

					msVictim.damage(damageRecord);
					SoundManager.play(damageSound, hitLocation, Bukkit.getOnlinePlayers());
					return;
				}
				else if(obstacle.type == EnumMovingObjectType.BLOCK)
				{
					Location hitLocation = new Location(world, obstacle.pos.a, obstacle.pos.b, obstacle.pos.c);
					Block block = world.getBlockAt(obstacle.b, obstacle.c, obstacle.d);
					Material type = block.getType();
					@SuppressWarnings("deprecation")
					int id = type.getId();
					@SuppressWarnings("deprecation")
					byte data = block.getData();
					
					ParticleEffect.displayBlockDust(hitLocation, id, data, 0, 0, 0, 0.1F, 25);
					SoundManager.play(SOUND_WALL, hitLocation, Bukkit.getOnlinePlayers());
					return;
				}
		
		SoundManager.play(SOUND_SWING, from, Bukkit.getOnlinePlayers());
	}
	
	private boolean hasCooldown(MSPlayer msPlayer, long now)
	{
		long blockedUntil = msPlayer.getCustomData(DATA_BLOCKED_UNTIL, 0L);
		
		return now < blockedUntil;
	}
	
	private void setCooldown(MSPlayer msPlayer, long now, long cooldown)
	{
		msPlayer.setCustomData(DATA_BLOCKED_UNTIL, now + cooldown);
	}
	
	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = super.newItemStack(msPlayer);
		Scene scene = msPlayer.getScene();
		Team team = Team.COUNTER_TERRORISTS;
		
		if(scene instanceof TeamGame)
		{
			Team playerTeam = ((TeamGame) scene).getTeam(msPlayer);
			
			if(playerTeam != null)
				team = playerTeam;
		}
		
		LoreAttributes.TEMP.clear();
		LoreAttributes.extract(is, LoreAttributes.TEMP);
		LoreAttributes.TEMP.put("Type", getId() + " | DEFAULT_" + team.getAbbreviation());
		LoreAttributes.TEMP.apply(is);
		
		return is;
	}
}
