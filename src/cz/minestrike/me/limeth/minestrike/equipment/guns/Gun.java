package cz.minestrike.me.limeth.minestrike.equipment.guns;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
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
	private boolean reloading;
	
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
	
	public Gun(GunType type)
	{
		this(type, null);
	}
	
	public LoreAttributes createAttributes()
	{
		LoreAttributes attributes = new LoreAttributes();
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
		
		return attributes;
	}
	
	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = new ItemStack(GUN_MATERIAL);
		
		apply(is);
		
		return is;
	}
	
	public void apply(ItemStack is)
	{
		Material material = is.getType();
		
		Validate.isTrue(material == GUN_MATERIAL, "The ItemStack is not of type " + GUN_MATERIAL + "!");
		
		EquipmentCustomization customization = getCustomization();
		GunType type = getEquipment();
		
		if(customization != null)
			customization.apply(type, is);
		
		createAttributes().apply(is);
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
		double cycleTime = type.getCycleTime() * 1000;
		
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
}
