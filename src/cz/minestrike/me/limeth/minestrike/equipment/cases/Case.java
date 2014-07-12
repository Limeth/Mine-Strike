package cz.minestrike.me.limeth.minestrike.equipment.cases;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;

public enum Case implements Equipment
{
	ALPHA(ChatColor.GOLD + "Alpha",
			new CaseContent(new Gun(GunType.P2000), CaseContentRarity.COMMON)
	);
	
	private final String name;
	private final CaseContent[] contents;
	
	private Case(String name, CaseContent... contents)
	{
		this.name = name;
		this.contents = contents;
	}
	
	public String getName()
	{
		return name;
	}

	public CaseContent[] getContents()
	{
		return contents;
	}
	
	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = new ItemStack(Material.CHEST);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(name);
		is.setItemMeta(im);
		
		return is;
	}
	
	@Override
	public String getDisplayName()
	{
		return name;
	}
	
	@Override
	public String getId()
	{
		return "CASE_" + name();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends CustomizedEquipment> getEquipmentClass()
	{
		return CustomizedEquipment.class;
	}
	
	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return null;
	}
	
	@Override
	public Equipment getSource()
	{
		return this;
	}
	
	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	@Override
	public String getSoundDraw()
	{
		return null;
	}
}
