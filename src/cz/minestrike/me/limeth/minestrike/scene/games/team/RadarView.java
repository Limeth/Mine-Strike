package cz.minestrike.me.limeth.minestrike.scene.games.team;

import com.google.common.collect.Lists;
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
import cz.projectsurvive.me.limeth.psmaps.MapIcon;
import cz.projectsurvive.me.limeth.psmaps.filters.AdditiveNoiseFilter;
import cz.projectsurvive.me.limeth.psmaps.filters.DepthFilter;
import cz.projectsurvive.me.limeth.psmaps.surfaces.MapSurface;
import cz.projectsurvive.me.limeth.psmaps.surfaces.OverlayMapSurface;
import cz.projectsurvive.me.limeth.psmaps.surfaces.RegionMapSurface;
import cz.projectsurvive.me.limeth.psmaps.views.SingleMapView;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.List;

public class RadarView extends SingleMapView implements Runnable
{
	public static final long UPDATE_INTERVAL = 20L;
	public static final int ICON_PLAYER_T = 0, ICON_PLAYER_CT = 1, ICON_TEAMMATE_T = 2, ICON_TEAMMATE_CT = 3;
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
	public MapSurface getSurface(Player player)
	{
		return finalSurface;
	}
	
	public RadarView updateSurface()
	{
		Structure<? extends GameMap> structure = game.getMapStructure();
		GameMap scheme = structure.getScheme();
		Point schemeBase = scheme.getBase();
		Point structureBase = structure.getBase();
		Region schemeRegion = scheme.getRegion();
		Region structureRegion = schemeRegion.clone().subtract(schemeBase).add(structureBase);
		Point lower = structureRegion.getLower();
		Point higher = structureRegion.getHigher();
		
		regionMapSurface.setRegion(lower.getX(), lower.getY(), lower.getZ(), higher.getX(), higher.getY(), higher.getZ());
		finalSurface.render();
		return this;
	}
	
	@Override
	public void run()
	{
		sendIcons();
	}
	
	@Override
	public List<MapIcon> getIcons(Player player)
	{
		Structure<? extends GameMap> structure = game.getMapStructure();
		Point base = structure.getBase();
		GameMap scheme = structure.getScheme();
		Region region = scheme.getRegion();
		int minX = base.getX();
		int minY = base.getZ();
		int width = region.getWidth();
		int height = region.getDepth();
		MSPlayer msPlayer = MSPlayer.get(player);
		Equipment equipment = msPlayer.getEquipmentInHand();
		
		if(equipment == null || !(equipment instanceof Radar))
			return Lists.newArrayList();
		
		Team team = game.getTeam(msPlayer);
		List<MapIcon> icons = Lists.newArrayList();
		Integer iconIndex = null;
		
		if(team == Team.TERRORISTS)
		{
			MapIcon playerIcon = new MapIcon(ICON_PLAYER_T);

			playerIcon.setLocation(player.getLocation(), minX, minY, width, height);
			icons.add(playerIcon);
		}
		else if(team == Team.COUNTER_TERRORISTS)
		{
			MapIcon playerIcon = new MapIcon(ICON_PLAYER_CT);

			playerIcon.setLocation(player.getLocation(), minX, minY, width, height);
			icons.add(playerIcon);
		}
		
		return icons;
	}
	
	public void sendIcons()
	{
		sendIcons(game.getBukkitPlayers());
	}
	
	public void sendSurface()
	{
		sendSurface(game.getBukkitPlayers());
	}
	
	public void send()
	{
		send(game.getBukkitPlayers());
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
		public void render(byte[][] surface)
		{
			for(int x = 0; x < 3; x++)
				for(int y = 0; y < 3; y++)
					surface[y + 3][x + 3] = MapSurface.matchColor(Color.RED);
		}
		
		public RadarView getView()
		{
			return view;
		}
	}
}
