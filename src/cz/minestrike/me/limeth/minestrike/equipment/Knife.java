package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;

public class Knife extends SimpleEquipmentType
{
	static
	{
		ItemStack item = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta) item.getItemMeta();
		
		im.setDisplayName("Knife");
		item.setItemMeta(im);
		
		ITEM = item;
		KNIFE = new Knife();
	}
	
	public static final ItemStack ITEM;
	public static final Knife KNIFE;
	
	private Knife()
	{
		super("KNIFE", ITEM, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, Equipment.DESERIALIZER);
	}
}
