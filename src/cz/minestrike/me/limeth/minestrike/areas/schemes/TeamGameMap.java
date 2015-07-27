package cz.minestrike.me.limeth.minestrike.areas.schemes;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import java.util.ArrayList;

//TODO Better name, please!
public abstract class TeamGameMap extends GameMap
{
	@Expose private RegionList tSpawn, ctSpawn, shoppingZones, spectatorZones;
	@Expose private Point spectatorSpawn;

	public TeamGameMap(SchemeType type, String id, Region region, String name, RegionList tSpawn, RegionList ctSpawn, RegionList shoppingZones, Point spectatorSpawn)
	{
		super(type, id, region, name);
		
		Validate.notNull(tSpawn, "The TSpawn set cannot be null!");
		Validate.notNull(ctSpawn, "The CTSpawn set cannot be null!");
		Validate.notNull(shoppingZones, "The shopping zones set cannot be null!");
		Validate.notNull(spectatorSpawn, "The spectator cannot be null!");

		this.tSpawn = tSpawn;
		this.ctSpawn = ctSpawn;
		this.shoppingZones = shoppingZones;
		this.spectatorSpawn = spectatorSpawn;
	}
	
	public RegionList getSpawn(Team team)
	{
		switch(team)
		{
		case TERRORISTS: return getTSpawn();
		case COUNTER_TERRORISTS: return getCTSpawn();
		default: return null;
		}
	}
	
	@Override
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		FilledHashMap<String, RegionList> regionList = super.getRegionsLists();
		
		regionList.put("TSpawn", getTSpawn());
		regionList.put("CTSpawn", getCTSpawn());
		regionList.put("shoppingZones", getShoppingZones());
		regionList.put("spectatorZones", getSpectatorZones());
		
		return regionList;
	}
	
	@Override
	public FilledHashMap<String, Point> getPoints()
	{
		FilledHashMap<String, Point> points = super.getPoints();
		
		points.put("spectatorSpawn", spectatorSpawn);
		
		return points;
	}
	
	@Override
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> handlers = super.getCommandHandlers();

		handlers.add(RegionList.COMMAND_HANDLER);
		handlers.add(Point.COMMAND_HANDLER);
		
		return handlers;
	}
	
	@Override
	public boolean isSetUp()
	{
		return super.isSetUp() && !tSpawn.isEmpty() && !ctSpawn.isEmpty();
	}

	public RegionList getTSpawn()
	{
		return tSpawn != null ? tSpawn : (tSpawn = new RegionList());
	}
	
	public void setTSpawn(RegionList tSpawn)
	{
		this.tSpawn = tSpawn;
	}

	public RegionList getCTSpawn()
	{
		return ctSpawn != null ? ctSpawn : (ctSpawn = new RegionList());
	}

	public void setCTSpawn(RegionList ctSpawn)
	{
		this.ctSpawn = ctSpawn;
	}

	public RegionList getShoppingZones()
	{
		return shoppingZones != null ? shoppingZones : (shoppingZones = new RegionList());
	}

	public void setShoppingZones(RegionList shoppingZones)
	{
		this.shoppingZones = shoppingZones;
	}

	public RegionList getSpectatorZones()
	{
		return spectatorZones != null ? spectatorZones : (spectatorZones = new RegionList());
	}

	public void setSpectatorZones(RegionList spectatorZones)
	{
		this.spectatorZones = spectatorZones;
	}

	public Point getSpectatorSpawn()
	{
		return spectatorSpawn;
	}

	public void setSpectatorSpawn(Point spectatorSpawn)
	{
		this.spectatorSpawn = spectatorSpawn;
	}
}
