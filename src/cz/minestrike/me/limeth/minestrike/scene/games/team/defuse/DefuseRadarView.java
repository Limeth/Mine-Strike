package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RadarView;
import cz.projectsurvive.me.limeth.psmaps.MapIcon;

public class DefuseRadarView extends RadarView
{
	private static final int[] BOMBSITE_ICONS = new int[] { RadarView.ICON_BOMBSITE_A, RadarView.ICON_BOMBSITE_B };
	
	public DefuseRadarView(DefuseGame game)
	{
		super(game);
	}
	
	@Override
	public List<MapIcon> getIcons(Player player)
	{
		List<MapIcon> icons = super.getIcons(player);
		DefuseGame game = getGame();
		Structure<? extends DefuseGameMap> mapStructure = game.getMapStructure();
		DefuseGameMap map = mapStructure.getScheme();
		Point base = map.getBase();
		Region region = map.getRegion();
		int minX = base.getX();
		int minY = base.getZ();
		int width = region.getWidth();
		int height = region.getDepth();
		int bombSiteIndex = 0;
		
		for(Region bombSite : map.getBombSites())
		{
			int iconType = BOMBSITE_ICONS[bombSiteIndex % BOMBSITE_ICONS.length];
			MapIcon icon = new MapIcon(iconType);
			Point iconPoint = bombSite.getMidpoint();
			Location iconLocation = iconPoint.getLocation(MSConfig.getWorld());
			
			icon.setLocation(iconLocation, minX, minY, width, height);
			icons.add(icon);
			
			bombSiteIndex++;
		}
		
		return icons;
	}
	
	@Override
	public DefuseGame getGame()
	{
		return (DefuseGame) super.getGame();
	}
}
