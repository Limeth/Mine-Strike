package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.games.Game;

public class KevlarAndHelmet extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		
		im.setDisplayName("Kevlar + Helmet");
		item.setItemMeta(im);
		
		ITEM = item;
		KEVLAR_AND_HELMET = new KevlarAndHelmet();
	}
	
	public static final ItemStack ITEM;
	public static final KevlarAndHelmet KEVLAR_AND_HELMET;
	
	private KevlarAndHelmet()
	{
		super("KEVLAR_AND_HELMET", ITEM, 1000, MSConstant.MOVEMENT_SPEED_DEFAULT, null);
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		Game<?, ?, ?, ? extends EquipmentProvider> game = msPlayer.getGame();
		EquipmentProvider ep = game.getEquipmentProvider();
		
		ep.setHelmet(msPlayer, true);
		ep.setKevlar(msPlayer, true);
		
		return false;
	}
}
