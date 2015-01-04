package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RadarView;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.projectsurvive.me.limeth.psmaps.MapAllocator;
import cz.projectsurvive.me.limeth.psmaps.MapSender;

public class Radar extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.MAP, 1, MapAllocator.allocate());
		ItemMeta im = item.getItemMeta();
		
		im.setDisplayName(Translation.EQUIPMENT_RADAR.getMessage());
		item.setItemMeta(im);
		
		ITEM = item;
		RADAR = new Radar();
		
		MapSender.blockPackets(getMapId());
	}
	
	public static final ItemStack ITEM;
	public static final Radar RADAR;
	
	public Radar()
	{
		super("radar", ITEM, null, MSConstant.MOVEMENT_SPEED_DEFAULT, "projectsurvive:counterstrike.weapons.movement", false, false);
	}
	
	@Override
	public void onSelect(MSPlayer msPlayer)
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof TeamGame))
			return;
		
		TeamGame game = (TeamGame) scene;
		RadarView view = game.getRadarView();
		
		view.sendIcons();
	}
	
	public static short getMapId()
	{
		return ITEM.getDurability();
	}
}
