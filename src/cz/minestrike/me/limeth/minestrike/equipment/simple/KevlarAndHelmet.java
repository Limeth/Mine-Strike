package cz.minestrike.me.limeth.minestrike.equipment.simple;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class KevlarAndHelmet extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		
		im.setDisplayName("Kevlar + Helmet");
		item.setItemMeta(im);
		
		LoreAttributes.TEMP.clear();
		LoreAttributes.TEMP.put("Type", "KEVLAR_AND_HELMET");
		LoreAttributes.TEMP.apply(item);
		
		ITEM = item;
		KEVLAR_AND_HELMET = new KevlarAndHelmet();
	}
	
	public static final ItemStack ITEM;
	public static final KevlarAndHelmet KEVLAR_AND_HELMET;
	
	private KevlarAndHelmet()
	{
		super("KEVLAR_AND_HELMET", ITEM, EquipmentCategory.ARMOR, false, 1000, MSConstant.MOVEMENT_SPEED_DEFAULT, null, false, false);
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
			throw new EquipmentPurchaseException(this, "The scene " + scene + " is not an instance of game.");
		
		Game game = (Game) scene;
		EquipmentProvider ep = game.getEquipmentProvider();
		
		ep.setHelmet(msPlayer, true);
		ep.setKevlar(msPlayer, true);
		
		return false;
	}
}
