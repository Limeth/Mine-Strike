package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class GameMenu extends Scheme
{
	@Expose private Point spawnPoint;
	
	public GameMenu(SchemeType schemeType, String id, Region region, Point spawnPoint)
	{
		super(schemeType, id, region);
		
		Validate.notNull(spawnPoint, "The spawn point cannot be null!");
		
		this.spawnPoint = spawnPoint;
	}
	
	public GameMenu(SchemeType schemeType, String id, Region region)
	{
		this(schemeType, id, region, region.getMidpoint());
	}
	
	@Override
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> handlers = super.getCommandHandlers();
		
		handlers.add(Point.COMMAND_HANDLER);
		
		return handlers;
	}
	
	@Override
	public FilledHashMap<String, Point> getPoints()
	{
		FilledHashMap<String, Point> points = super.getPoints();
		
		points.put("spawnPoint", spawnPoint);
		
		return points;
	}
	
	@Override
	public boolean isSetUp()
	{
		return spawnPoint != null;
	}

	public Point getSpawnPoint()
	{
		return spawnPoint;
	}

	public void setSpawnPoint(Point spawnPoint)
	{
		this.spawnPoint = spawnPoint;
	}
}
