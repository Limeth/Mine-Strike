package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class Kevlar extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		
		im.setDisplayName("Kevlar");
		item.setItemMeta(im);
		
		LoreAttributes.TEMP.clear();
		LoreAttributes.TEMP.put("Type", "KEVLAR");
		LoreAttributes.TEMP.apply(item);
		
		ITEM = item;
		KEVLAR = new Kevlar();
	}
	
	public static final ItemStack ITEM;
	public static final Kevlar KEVLAR;
	
	private Kevlar()
	{
		super("KEVLAR", ITEM, 650, MSConstant.MOVEMENT_SPEED_DEFAULT, null);
	}
}
