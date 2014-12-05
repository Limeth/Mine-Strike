package cz.minestrike.me.limeth.minestrike.scene.games.team;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Radar;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.projectsurvive.me.limeth.psmaps.AdditiveNoiseFilter;
import cz.projectsurvive.me.limeth.psmaps.DepthFilter;
import cz.projectsurvive.me.limeth.psmaps.MapIcon;
import cz.projectsurvive.me.limeth.psmaps.MapSender;
import cz.projectsurvive.me.limeth.psmaps.MapSurface;
import cz.projectsurvive.me.limeth.psmaps.MapView;
import cz.projectsurvive.me.limeth.psmaps.OverlayMapSurface;
import cz.projectsurvive.me.limeth.psmaps.RegionMapSurface;

public class RadarView extends MapView implements Runnable
{
	public static final long UPDATE_INTERVAL = 20L;
	public static final int ICON_PLAYER_T = 0, ICON_PLAYER_CT = 1, ICON_TEAMMATE_T = 2, ICON_TEAMMATE_CT = 3,
			ICON_BOMBSITE_A = 8, ICON_BOMBSITE_B = 9, ICON_BOMB = 10;
	private final TeamGame game;
	private final RegionMapSurface regionMapSurface;
	private final MapSurface finalSurface;
	private Integer iconLoopId;
	
	public RadarView(TeamGame game)
	{
		super(Radar.getMapId());
		
		this.game = game;
		this.regionMapSurface = new RegionMapSurface(MSConfig.getWorld());
		regionMapSurface.addFilters(new DepthFilter(regionMapSurface, 0.5), new AdditiveNoiseFilter(0.1));
		this.finalSurface = new RadarOverlaySurface(regionMapSurface, this);
	}
	
	@Override
	public MapSurface getSurface()
	{
		return finalSurface;
	}
	
	public RadarView updateSurface()
	{
		Structure<? extends GameMap> structure = game.getMapStructure();
		GameMap scheme = structure.getScheme();
		Point base = scheme.getBase();
		Region schemeRegion = scheme.getRegion();
		Region structureRegion = schemeRegion.clone().subtract(base).add(structure.getBase());
		Point lower = structureRegion.getLower();
		Point higher = structureRegion.getHigher();
		
		regionMapSurface.setRegion(lower.getX(), lower.getY(), lower.getZ(), higher.getX(), higher.getY(), higher.getZ());
		regionMapSurface.render();
		return this;
	}
	
	@Override
	public void run()
	{
		sendIcons();
	}
	
	public void sendSurface()
	{
		for(MSPlayer msPlayer : game.getPlayers())
		{
			Equipment equipment = msPlayer.getEquipmentInHand();
			
			if(equipment == null || !(equipment instanceof Radar))
				continue;
			
			Player player = msPlayer.getPlayer();
			
			sendSurface(player);
		}
	}
	
	public void sendIcons()
	{
		boolean watched = false;
		
		for(MSPlayer msPlayer : game.getPlayers())
		{
			Equipment equipment = msPlayer.getEquipmentInHand();
			
			if(equipment == null || !(equipment instanceof Radar))
				continue;
			
			watched = true;
			break;
		}
		
		if(!watched)
			return;
		
		ArrayList<MapIcon> spectatorIcons = new ArrayList<MapIcon>();
		ArrayList<MapIcon> tIcons = new ArrayList<MapIcon>();
		ArrayList<MapIcon> ctIcons = new ArrayList<MapIcon>();
		Structure<? extends GameMap> structure = game.getMapStructure();
		Point base = structure.getBase();
		GameMap scheme = structure.getScheme();
		Region region = scheme.getRegion();
		int minX = base.getX();
		int minY = base.getZ();
		int width = region.getWidth();
		int height = region.getDepth();
		short mapId = getMapId();
		
		for(MSPlayer msPlayer : game.getPlayingPlayers())
		{
			Player player = msPlayer.getPlayer();
			Location location = player.getLocation();
			Team team = game.getTeam(msPlayer);
			int iconType = team == Team.TERRORISTS ? ICON_TEAMMATE_T : ICON_TEAMMATE_CT;
			MapIcon icon = new MapIcon(iconType);
			
			icon.setLocation(location, minX, minY, width, height);
			spectatorIcons.add(icon);
			(team == Team.TERRORISTS ? tIcons : ctIcons).add(icon);
		}
		
		for(MSPlayer msPlayer : game.getPlayers())
		{
			Equipment equipment = msPlayer.getEquipmentInHand();
			
			if(equipment == null || !(equipment instanceof Radar))
				continue;
			
			Team team = game.getTeam(msPlayer);
			Player player = msPlayer.getPlayer();
			ArrayList<MapIcon> icons;
			
			if(team == Team.TERRORISTS)
				icons = tIcons;
			else if(team == Team.COUNTER_TERRORISTS)
				icons = ctIcons;
			else
				icons = spectatorIcons;
			
			MapSender.sendIcons(player, mapId, icons.toArray(new MapIcon[icons.size()]));
		}
	}
	
	public RadarView startIconLoop()
	{
		if(iconLoopId != null)
			return this;
		
		iconLoopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineStrike.getInstance(), this, UPDATE_INTERVAL, UPDATE_INTERVAL);
		
		return this;
	}
	
	public void stopIconLoop()
	{
		if(iconLoopId == null)
			return;
		
		Bukkit.getScheduler().cancelTask(iconLoopId);
		
		iconLoopId = null;
	}
	
	public TeamGame getGame()
	{
		return game;
	}
	
	public static class RadarOverlaySurface extends OverlayMapSurface
	{
		private final RadarView view;
		
		public RadarOverlaySurface(MapSurface background, RadarView view)
		{
			super(background);
			
			this.view = view;
		}
		
		@Override
		public byte[][] getSurface(byte[][] surface)
		{
			TeamGame game = view.getGame();
			
			for(int x = 0; x < 3; x++)
				for(int y = 0; y < 3; y++)
					surface[y + 3][x + 3] = MapSurface.matchColor(Color.RED);
			
			return surface;
		}
		
		public RadarView getView()
		{
			return view;
		}
	}
}
