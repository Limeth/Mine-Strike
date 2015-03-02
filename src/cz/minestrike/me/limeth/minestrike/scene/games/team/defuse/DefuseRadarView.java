package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RadarView;
import cz.projectsurvive.me.limeth.psmaps.MapIcon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class DefuseRadarView extends RadarView
{
	private static final int ICON_BOMBSITE_A = 8, ICON_BOMBSITE_B = 9;
	private static final int[] BOMBSITE_ICONS = new int[] { ICON_BOMBSITE_A, ICON_BOMBSITE_B };
	private static final int ICON_BOMB = 10;
	
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
		Region region = map.getRegion();
		int width = region.getWidth();
		int height = region.getDepth();
		Location bombLocation = game.getBombLocation();

		if(bombLocation != null)
		{
			MSPlayer msPlayer = MSPlayer.get(player);
			Team team = game.getTeam(msPlayer);

			if(team != Team.COUNTER_TERRORISTS)
			{
				MapIcon bombIcon = new MapIcon(ICON_BOMB);
				Point structureBase = mapStructure.getBase();

				bombLocation.setYaw(0);
				bombIcon.setLocation(bombLocation, structureBase.getX(), structureBase.getZ(), width, height);
				icons.add(bombIcon);
			}
		}

		int bombSiteIndex = 0;
		
		for(Region bombSite : map.getBombSites())
		{
			int iconType = BOMBSITE_ICONS[bombSiteIndex % BOMBSITE_ICONS.length];
			MapIcon icon = new MapIcon(iconType);
			Point iconPoint = bombSite.getMidpoint();
			Location iconLocation = iconPoint.getLocation(MSConfig.getWorld());
			
			icon.setLocation(iconLocation, 0, 0, width, height);
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
