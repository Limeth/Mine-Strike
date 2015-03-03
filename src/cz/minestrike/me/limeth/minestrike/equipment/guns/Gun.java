package cz.minestrike.me.limeth.minestrike.equipment.guns;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.RandomString;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.util.Map;

public class Gun extends CustomizedEquipment<GunType>
{
	public static final Material GUN_MATERIAL = Material.FIREWORK_CHARGE;
	private static final RandomString RANDOM_STRING = new RandomString(8);
	private String ownerName;
	private Integer kills;
	private Long lastBulletShotAt;
	private int loadedBullets, unusedBullets;
	private boolean reloading;
	private Map<String, Object> customData = Maps.newHashMap();

	public Gun(GunType type, EquipmentCustomization customization, Integer kills, int loadedBullets, int unusedBullets, boolean reloading)
	{
		super(type, customization);

		Validate.notNull(type, "The type of the gun cannot be null!");

		this.kills = kills;
		this.reloading = reloading;

		this.setLoadedBullets(loadedBullets);
		this.setUnusedBullets(unusedBullets);

		type.initialize(this);
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

	@Override
	public Gun clone()
	{
		Gun gun = new Gun(getEquipment(), getCustomization(), kills, loadedBullets, unusedBullets, reloading);

		gun.ownerName = ownerName;
		gun.lastBulletShotAt = lastBulletShotAt;

		return gun;
	}

	public void pressTrigger(MSPlayer msPlayer)
	{
		getEquipment().pressTrigger(msPlayer);
	}
	
	@Override
	public void onSelect(MSPlayer msPlayer)
	{
		getEquipment().onSelect(msPlayer);
	}
	
	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
		if(msPlayer.hasGunTask())
			msPlayer.getGunTask().cancel();
		
		getEquipment().onDeselect(msPlayer);
	}
	
	public void applyAttributes(LoreAttributes attributes)
	{
		GunType type = getEquipment();

		for(Map.Entry<String, Object> entry : customData.entrySet())
			attributes.put(entry.getKey(), String.valueOf(entry.getValue()));
		
		if(reloading)
			attributes.put("Reloading", Boolean.toString(true));
		
		if(kills != null)
			attributes.put("StatTrak", Integer.toString(kills));
		
		if(ownerName != null)
			attributes.put("Owner", ownerName);
		
		attributes.put("Type", type.getGunId());
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
		getEquipment().apply(is, msPlayer, this);
	}
	
	public String buildDisplayName(boolean showBulletAmount)
	{
		GunType type = getEquipment();
		String result = super.getDisplayName();
		
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

	public String getSoundShooting(MSPlayer msPlayer)
	{
		return getEquipment().getSoundShooting(msPlayer);
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Gun gun = (Gun) o;

		if(getEquipment() != gun.getEquipment())
			return false;
		if(loadedBullets != gun.loadedBullets)
			return false;
		if(reloading != gun.reloading)
			return false;
		if(unusedBullets != gun.unusedBullets)
			return false;
		if(kills != null ? !kills.equals(gun.kills) : gun.kills != null)
			return false;
		if(lastBulletShotAt != null ? !lastBulletShotAt.equals(gun.lastBulletShotAt) : gun.lastBulletShotAt != null)
			return false;
		if(ownerName != null ? !ownerName.equals(gun.ownerName) : gun.ownerName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = ownerName != null ? ownerName.hashCode() : 0;
		result = 31 * result + getClass().hashCode();
		result = 31 * result + getEquipment().hashCode();
		result = 31 * result + (kills != null ? kills.hashCode() : 0);
		result = 31 * result + (lastBulletShotAt != null ? lastBulletShotAt.hashCode() : 0);
		result = 31 * result + loadedBullets;
		result = 31 * result + unusedBullets;
		result = 31 * result + (reloading ? 1 : 0);
		return result;
	}

	public boolean isShotDelaySatisfied(MSPlayer msPlayer)
	{
		if(lastBulletShotAt == null)
			return true;
		
		GunType type = getEquipment();
		double cycleTime = type.getCycleTime(msPlayer) * 50;
		
		return lastBulletShotAt < System.currentTimeMillis() - cycleTime;
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

	public Map<String, Object> getCustomData()
	{
		return customData;
	}
	
	public boolean isAutomatic()
	{
		return getEquipment().isAutomatic(this);
	}
	
	public float getSpread()
	{
		return getEquipment().getSpread(this);
	}
	
	public float getInaccuracySneak()
	{
		return getEquipment().getInaccuracySneak(this);
	}
	
	public float getInaccuracyStand()
	{
		return getEquipment().getInaccuracyStand(this);
	}
	
	public float getInaccuracyFire()
	{
		return getEquipment().getInaccuracyFire(this);
	}
	
	public float getInaccuracyMove()
	{
		return getEquipment().getInaccuracyMove(this);
	}
	
	public float getInaccuracyJump()
	{
		return getEquipment().getInaccuracyJump(this);
	}
	
	public float getInaccuracyLand()
	{
		return getEquipment().getInaccuracyLand(this);
	}
	
	public float getInaccuracyLadder()
	{
		return getEquipment().getInaccuracyLadder(this);
	}
	
	public int getShootingBullets()
	{
		return getEquipment().getBullets(this);
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

	@Override
	public boolean isTradable()
	{
		return getCustomization() != null;
	}
}
