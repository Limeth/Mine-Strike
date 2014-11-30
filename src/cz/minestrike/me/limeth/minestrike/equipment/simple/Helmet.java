package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class Helmet extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		
		im.setDisplayName("Helmet");
		item.setItemMeta(im);
		
		LoreAttributes.TEMP.clear();
		LoreAttributes.TEMP.put("Type", "HELMET");
		LoreAttributes.TEMP.apply(item);
		
		ITEM = item;
		HELMET = new Helmet();
	}
	
	public static final ItemStack ITEM;
	public static final Helmet HELMET;
	
	private Helmet()
	{
		super("HELMET", ITEM, null, MSConstant.MOVEMENT_SPEED_DEFAULT, null, false, false);
	}
}
