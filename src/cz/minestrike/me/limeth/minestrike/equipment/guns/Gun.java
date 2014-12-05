package cz.minestrike.me.limeth.minestrike.equipment.guns;

import net.minecraft.server.v1_7_R4.EnumMovingObjectType;
import net.minecraft.server.v1_7_R4.MovingObjectPosition;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.guns.extensions.GunExtension;
import cz.minestrike.me.limeth.minestrike.util.BoundUtil;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.RandomString;

public class Gun extends CustomizedEquipment<GunType>
{
	public static final Material GUN_MATERIAL = Material.FIREWORK_CHARGE;
	private static final RandomString RANDOM_STRING = new RandomString(8);
	private String ownerName;
	private Integer kills;
	private Long lastBulletShotAt;
	private int loadedBullets, unusedBullets;
	private boolean reloading, secondaryState;
	private GunExtension lazyExtension;
	
	public Gun(GunType type, EquipmentCustomization customization, Integer kills, int loadedBullets, int unusedBullets, boolean reloading)
	{
		super(type, customization);
		
		Validate.notNull(type, "The type of the gun cannot be null!");
		
		this.kills = kills;
		this.reloading = reloading;
		
		this.setLoadedBullets(loadedBullets);
		this.setUnusedBullets(unusedBullets);
	}
	
	public Gun(GunType type, EquipmentCustomization customization, Integer kills)
	{
		this(type, customization, kills, type.getClipSize(), type.getSpareCapacity(), false);
	}
	
	public Gun(GunType type, EquipmentCustomization customization)
	{
		this(type, customization, null);
	}
	
	public Gun(GunType type, String name, String skin, Color color)
	{
		this(type, EquipmentCustomization.skin(name, skin, color));
	}
	
	public Gun(GunType type, String name, String skin)
	{
		this(type, name, skin, null);
	}
	
	public Gun(GunType type)
	{
		this(type, null);
	}
	
	public Gun clone()
	{
		Gun gun = new Gun(getEquipment(), getCustomization(), kills, loadedBullets, unusedBullets, reloading);
		
		gun.ownerName = ownerName;
		gun.lastBulletShotAt = lastBulletShotAt;
		
		return gun;
	}
	
	public void shoot(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		Location location = player.getEyeLocation();
		World world = location.getWorld();
		GunType gunType = getEquipment();
		int range = gunType.getRange();
		Vector direction = location.getDirection();
		Vector inaccuracyDirection = msPlayer.getInaccuracyVector(this);
		msPlayer.modifyByRecoil(direction, this);
		direction.add(inaccuracyDirection);
		direction.multiply(range / direction.length()); //Normalize to range
		msPlayer.increaseRecoil(gunType.getRecoilMagnitude());
		
		MovingObjectPosition[] obstacles = BoundUtil.findObstaclesByMotion(player, location, direction);
		Location endLocation = null;
		
		if(obstacles.length > 0)
		{
			MovingObjectPosition lastObstacle = obstacles[obstacles.length - 1];
			
			GunManager.onBulletHit(obstacles, player);
			
			if(lastObstacle.type == EnumMovingObjectType.BLOCK)
				endLocation = new Location(world, lastObstacle.pos.a, lastObstacle.pos.b, lastObstacle.pos.c);
		}
		
		if(endLocation == null)
			endLocation = location.clone().add(direction);
		
		GunManager.showTrace(location, endLocation);
	}
	
	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return getExtension().getMovementSpeed(msPlayer);
	}
	
	public String getSoundShooting(MSPlayer msPlayer)
	{
		return getExtension().getSoundShooting(msPlayer);
	}
	
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return getExtension().onLeftClick(msPlayer);
	}
	
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return getExtension().onRightClick(msPlayer);
	}
	
	@Override
	public void onSelect(MSPlayer msPlayer)
	{
		getEquipment().onSelect(msPlayer);
		getExtension().onSelect(msPlayer);
	}
	
	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
		if(msPlayer.hasGunTask())
			msPlayer.getGunTask().cancel();
		
		getEquipment().onDeselect(msPlayer);
		getExtension().onDeselect(msPlayer);
	}
	
	public void applyAttributes(LoreAttributes attributes)
	{
		GunType type = getEquipment();
		
		if(reloading)
			attributes.put("Reloading", Boolean.toString(true));
		
		if(kills != null)
			attributes.put("StatTrak", Integer.toString(kills));
		
		if(ownerName != null)
			attributes.put("Owner", ownerName);
		
		attributes.put("Type", type.name());
		attributes.put("Loaded bullets", Integer.toString(loadedBullets));
		attributes.put("Unused bullets", Integer.toString(unusedBullets));
		attributes.put("Seed", RANDOM_STRING.nextString());
	}
	
	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = new ItemStack(GUN_MATERIAL);
		
		apply(is, msPlayer);
		
		return is;
	}
	
	public void apply(ItemStack is, MSPlayer msPlayer)
	{
		Material material = is.getType();
		
		Validate.isTrue(material == GUN_MATERIAL, "The ItemStack is not of type " + GUN_MATERIAL + "!");
		
		EquipmentCustomization customization = getCustomization();
		GunType type = getEquipment();
		LoreAttributes.TEMP.clear();
		
		applyAttributes(LoreAttributes.TEMP);
		LoreAttributes.TEMP.apply(is);
		
		if(customization != null)
			customization.apply(type, is, msPlayer);
		
		String displayName = buildDisplayName(true);
		FireworkEffectMeta fem = (FireworkEffectMeta) is.getItemMeta();
		
		fem.setDisplayName(displayName);
		is.setItemMeta(fem);
	}
	
	public String buildDisplayName(boolean showBulletAmount)
	{
		EquipmentCustomization customization = getCustomization();
		String customName = customization != null ? customization.getName() : null;
		GunType type = getEquipment();
		String result = ChatColor.RESET + (customName == null ? type.getName() : customName + ChatColor.GRAY + " (" + type.getName() + ChatColor.GRAY + ")");
		
		if(kills != null)
			result += ChatColor.GOLD + " ×" + ChatColor.RESET + kills;
		
		if(showBulletAmount)
		{
			int clipSize = type.getClipSize();
			ChatColor loadedColor;
			
			if(loadedBullets >= clipSize / 2.0)
				loadedColor = ChatColor.WHITE;
			else if(loadedBullets >= clipSize / 4.0)
				loadedColor = ChatColor.YELLOW;
			else if(loadedBullets >= clipSize / 8.0)
				loadedColor = ChatColor.GOLD;
			else if(loadedBullets <= 0)
				loadedColor = ChatColor.DARK_RED;
			else
				loadedColor = ChatColor.RED;
			
			String ammoLabel;
			
			if(reloading)
				ammoLabel = loadedColor + "●●●";
			else if(loadedBullets > 0 || unusedBullets <= 0)
				ammoLabel = loadedColor + "" + loadedBullets + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + unusedBullets;
			else
				ammoLabel = loadedColor + Translation.RELOAD.getMessage();
			
			result += ChatColor.DARK_GRAY + "   < " + ammoLabel + ChatColor.DARK_GRAY + " >";
		}
		
		return result;
	}
	
	public void refresh()
	{
		GunType type = getEquipment();
		
		loadedBullets = type.getClipSize();
		unusedBullets = type.getSpareCapacity();
	}
	
	public boolean isLoaded()
	{
		return loadedBullets > 0;
	}
	
	public boolean canBeReloaded()
	{
		GunType type = getEquipment();
		
		return loadedBullets < type.getClipSize() && unusedBullets > 0;
	}

	public int getLoadedBullets()
	{
		return loadedBullets;
	}

	public final void setLoadedBullets(int loadedBullets)
	{
		GunType type = getEquipment();
		
		if(loadedBullets > type.getClipSize())
			throw new IllegalArgumentException("Gun " + type + " can't have more than " + type.getClipSize() + " loaded bullets.");
		
		this.loadedBullets = loadedBullets;
	}
	
	public int decreaseLoadedBullets()
	{
		return --loadedBullets;
	}

	public int getUnusedBullets()
	{
		return unusedBullets;
	}

	public final void setUnusedBullets(int unusedBullets)
	{
		GunType type = getEquipment();
		
		if(unusedBullets > type.getSpareCapacity())
			throw new IllegalArgumentException("Gun " + type + " can't have more than " + type.getSpareCapacity() + " unused bullets.");
		
		this.unusedBullets = unusedBullets;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
	
	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	public Integer getKills()
	{
		return kills;
	}

	public void setKills(Integer kills)
	{
		this.kills = kills;
	}

	public boolean isReloading()
	{
		return reloading;
	}

	public void setReloading(boolean reloading)
	{
		this.reloading = reloading;
	}
	
	@Override
	public String getDisplayName()
	{
		return buildDisplayName(false);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends CustomizedEquipment> getEquipmentClass()
	{
		return Gun.class;
	}

	@Override
	public String toString()
	{
		return "Gun [gunType=" + getSource() + ", shooterName=" + ownerName + ", kills=" + kills + ", loadedBullets=" + loadedBullets + ", unusedBullets=" + unusedBullets + ", reloading=" + reloading + ", customization=" + getCustomization() + "]";
	}
	
	public boolean isShotDelaySatisfied()
	{
		if(lastBulletShotAt == null)
			return true;
		
		GunType type = getEquipment();
		double cycleTime = type.getCycleTime() * 50;
		
		return lastBulletShotAt < System.currentTimeMillis() - cycleTime;
	}
	
	public GunExtension getExtension()
	{
		return lazyExtension != null ? lazyExtension : (lazyExtension = getEquipment().newExtension(this));
	}

	public Long getLastBulletShotAt()
	{
		return lastBulletShotAt;
	}

	public void setLastBulletShotAt(Long lastBulletShotAt)
	{
		this.lastBulletShotAt = lastBulletShotAt;
	}
	
	public void setLastBulletShotAt()
	{
		this.lastBulletShotAt = System.currentTimeMillis();
	}

	public boolean isSecondaryState()
	{
		return secondaryState;
	}

	public void setSecondaryState(boolean secondaryState)
	{
		Validate.isTrue(getEquipment().isSecondMode(), "GunType " + getEquipment() + " doesn't have a secondary state.");
		
		this.secondaryState = secondaryState;
	}
	
	public boolean isAutomatic()
	{
		return secondaryState ? getEquipment().isAutomaticAlt() : getEquipment().isAutomatic();
	}
	
	public float getSpread()
	{
		return secondaryState ? getEquipment().getSpreadAlt() : getEquipment().getSpread();
	}
	
	public float getInaccuracySneak()
	{
		return secondaryState ? getEquipment().getInaccuracySneakAlt() : getEquipment().getInaccuracySneak();
	}
	
	public float getInaccuracyStand()
	{
		return secondaryState ? getEquipment().getInaccuracyStandAlt() : getEquipment().getInaccuracyStand();
	}
	
	public float getInaccuracyFire()
	{
		return secondaryState ? getEquipment().getInaccuracyFireAlt() : getEquipment().getInaccuracyFire();
	}
	
	public float getInaccuracyMove()
	{
		return secondaryState ? getEquipment().getInaccuracyMoveAlt() : getEquipment().getInaccuracyMove();
	}
	
	public float getInaccuracyJump()
	{
		return secondaryState ? getEquipment().getInaccuracyJumpAlt() : getEquipment().getInaccuracyJump();
	}
	
	public float getInaccuracyLand()
	{
		return secondaryState ? getEquipment().getInaccuracyLandAlt() : getEquipment().getInaccuracyLand();
	}
	
	public float getInaccuracyLadder()
	{
		return secondaryState ? getEquipment().getInaccuracyLadderAlt() : getEquipment().getInaccuracyLadder();
	}
	
	public int getShootingBullets()
	{
		return secondaryState ? getEquipment().getBulletsAlt() : getEquipment().getBullets();
	}
	
	@Override
	public boolean isDroppedOnDeath()
	{
		return true;
	}
	
	@Override
	public boolean isDroppableManually()
	{
		return true;
	}
}
