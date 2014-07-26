package cz.minestrike.me.limeth.minestrike.equipment.simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.SimpleEquipment;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class Knife extends SimpleEquipment
{
	static
	{
		ItemStack item = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta im = (FireworkEffectMeta) item.getItemMeta();
		
		im.setDisplayName(ChatColor.WHITE + "Knife");
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
	
	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = super.newItemStack(msPlayer);
		Scene scene = msPlayer.getScene();
		String skin = "DEFAULT_CT";
		
		if(scene instanceof TeamGame)
		{
			Team team = ((TeamGame<?, ?, ?, ?>) scene).getTeam(msPlayer);
			
			if(team == Team.TERRORISTS)
				skin = "DEFAULT_T";
		}
		
		LoreAttributes.TEMP.clear();
		LoreAttributes.extract(is, LoreAttributes.TEMP);
		LoreAttributes.TEMP.put("Type", getId());
		LoreAttributes.TEMP.put("Skin", skin);
		LoreAttributes.TEMP.apply(is);
		
		return is;
	}
}
