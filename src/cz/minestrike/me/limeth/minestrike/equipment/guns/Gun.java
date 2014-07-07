package cz.minestrike.me.limeth.minestrike.equipment.guns;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import cz.minestrike.me.limeth.minestrike.util.RandomString;

public class Gun extends Equipment<GunType>
{
	public static final Material GUN_MATERIAL = Material.FIREWORK_CHARGE;
	private static final RandomString RANDOM_STRING = new RandomString(8);
	private final String ownerName;
	private String name;
	private Integer kills;
	private int loadedBullets, unusedBullets;
	private boolean reloading;
	
	public Gun(String ownerName, GunType type, String name, EquipmentCustomization customization, Integer kills, int loadedBullets, int unusedBullets, boolean reloading)
	{
		super(type, customization);
		
		Validate.notNull(ownerName, "The name of the owner cannot be null!");
		Validate.notNull(type, "The type of the gun cannot be null!");
		
		this.ownerName = ownerName;
		this.name = name;
		this.kills = kills;
		this.reloading = reloading;
		
		this.setLoadedBullets(loadedBullets);
		this.setUnusedBullets(unusedBullets);
	}
	
	public Gun(String ownerName, GunType type, EquipmentCustomization customization)
	{
		this(ownerName, type, null, customization, null, type.getClipSize(), type.getSpareCapacity(), false);
	}
	
	public Gun(String ownerName, GunType type)
	{
		this(ownerName, type, null);
	}
	
	public Gun(MSPlayer msPlayer, GunType type)
	{
		this(msPlayer.getName(), type);
	}
	
/*	public static Gun parse(ItemStack is)
	{
		Validate.notNull(is, "The gun ItemStack cannot be null!");
		
		Material type = is.getType();
		
		if(type != GUN_MATERIAL)
			throw new IllegalArgumentException("The ItemStack's type isn't " + GUN_MATERIAL);
		
		LoreAttributes attributes = LoreAttributes.extract(is);
		String ownerName = attributes.get("Owner").valueToString();
		GunType gunType = GunType.valueOf(attributes.get("Type").valueToString());
		int loadedBullets = attributes.get("Loaded bullets").valueToInteger();
		int unusedBullets = attributes.get("Unused bullets").valueToInteger();
		String name = null;
		Integer kills = null;
		
		LoreAttribute nameAttribute = attributes.get("Name");
		LoreAttribute killsAttribute = attributes.get("Kills");
		LoreAttribute reloadingAttribute = attributes.get("Reloading");
		
		if(nameAttribute != null)
			name = nameAttribute.valueToString();
		
		if(killsAttribute != null)
			kills = killsAttribute.valueToInteger();
		
		boolean reloading = reloadingAttribute == null ? false : reloadingAttribute.valueToBoolean();
		EquipmentCustomization customization = null;//TODO EquipmentCustomization.parse(is);
		
		return new Gun(ownerName, gunType, name, customization, kills, loadedBullets, unusedBullets, reloading);
	}
	
	public static Gun tryParse(ItemStack is)
	{
		try
		{
			return parse(is);
		}
		catch(Exception e)
		{
			return null;
		}
	}*/
	
	public LoreAttributes createAttributes()
	{
		LoreAttributes attributes = new LoreAttributes();
		GunType type = getType();
		
		if(reloading)
			attributes.put("Reloading", true);
		
		if(name != null)
			attributes.put("Name", name);
		
		if(kills != null)
			attributes.put("StatTrak", kills);
		
		attributes.put("Owner", ownerName);
		attributes.put("Type", type.name());
		attributes.put("Loaded bullets", loadedBullets);
		attributes.put("Unused bullets", unusedBullets);
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
		
		createAttributes().apply(is);
		
		FireworkEffectMeta fem = (FireworkEffectMeta) is.getItemMeta();
		String displayName = buildDisplayName(true);
		EquipmentCustomization customization = getCustomization();
		GunType type = getType();
		
		fem.setDisplayName(displayName);
		is.setItemMeta(fem);
		customization.apply(type, is);
	}
	
	public String buildDisplayName(boolean showBulletAmount)
	{
		GunType type = getType();
		String result = ChatColor.RESET + (name == null ? type.getName() : name + ChatColor.GRAY + " (" + type.getName() + ChatColor.GRAY + ")");
		
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
			else
				ammoLabel = loadedColor + "" + loadedBullets + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + unusedBullets;
			
			result += ChatColor.DARK_GRAY + "   < " + ammoLabel + ChatColor.DARK_GRAY + " >";
		}
		
		return result;
	}
	
	public void refresh()
	{
		GunType type = getType();
		
		loadedBullets = type.getClipSize();
		unusedBullets = type.getSpareCapacity();
	}
	
	public boolean isLoaded()
	{
		return loadedBullets > 0;
	}
	
	public boolean canBeReloaded()
	{
		GunType type = getType();
		
		return loadedBullets < type.getClipSize() && unusedBullets > 0;
	}

	public int getLoadedBullets()
	{
		return loadedBullets;
	}

	public final void setLoadedBullets(int loadedBullets)
	{
		GunType type = getType();
		
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
		GunType type = getType();
		
		if(unusedBullets > type.getSpareCapacity())
			throw new IllegalArgumentException("Gun " + type + " can't have more than " + type.getSpareCapacity() + " unused bullets.");
		
		this.unusedBullets = unusedBullets;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
}
