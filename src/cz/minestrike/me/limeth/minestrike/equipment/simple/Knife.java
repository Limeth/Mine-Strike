package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;

public class Knife extends SimpleEquipment
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
	public static final String SOUND_DRAW = "projectsurvive:counterstrike.weapons.knife.knife_deploy";
	
	private Knife()
	{
		super("KNIFE", ITEM, 0, MSConstant.MOVEMENT_SPEED_DEFAULT, SOUND_DRAW);
	}
}
