package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;


public abstract class GameMap extends Scheme
{
	public static final String SCHEME_ID_PREFIX = "map_";
	
	@Expose private String name;
	@Expose private RegionList tSpawn, ctSpawn, shoppingZones;
	@Expose private Point spectatorSpawn;
	
	public GameMap(SchemeType type, String id, Region region, String name, RegionList tSpawn, RegionList ctSpawn, RegionList shoppingZones, Point spectatorSpawn)
	{
		super(type, id, region);
		
		Validate.notNull(tSpawn, "The TSpawn set cannot be null!");
		Validate.notNull(ctSpawn, "The CTSpawn set cannot be null!");
		Validate.notNull(shoppingZones, "The shopping zones set cannot be null!");
		Validate.notNull(spectatorSpawn, "The spectator cannot be null!");
		
		this.name = name;
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
		
		regionList.put("TSpawn", tSpawn);
		regionList.put("CTSpawn", ctSpawn);
		regionList.put("shoppingZones", shoppingZones);
		
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
		
		handlers.add(SET_NAME);
		handlers.add(RegionList.COMMAND_HANDLER);
		handlers.add(Point.COMMAND_HANDLER);
		
		return handlers;
	}
	
	@Override
	public boolean isSetUp()
	{
		return name != null && !tSpawn.isEmpty() && !ctSpawn.isEmpty();
	}

	public RegionList getTSpawn()
	{
		return tSpawn;
	}
	
	public void setTSpawn(RegionList tSpawn)
	{
		this.tSpawn = tSpawn;
	}

	public RegionList getCTSpawn()
	{
		return ctSpawn;
	}

	public void setCTSpawn(RegionList ctSpawn)
	{
		this.ctSpawn = ctSpawn;
	}

	public RegionList getShoppingZones()
	{
		return shoppingZones;
	}

	public void setShoppingZones(RegionList shoppingZones)
	{
		this.shoppingZones = shoppingZones;
	}

	public Point getSpectatorSpawn()
	{
		return spectatorSpawn;
	}

	public void setSpectatorSpawn(Point spectatorSpawn)
	{
		this.spectatorSpawn = spectatorSpawn;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	private static final SchemeCommandHandler SET_NAME = new SchemeCommandHandler("setName", "ms scheme select [Scheme] setName [Name]", "Sets the name of this map")
	{
		
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(scheme instanceof GameMap))
			{
				sender.sendMessage("This scheme isn't an instance of GameMap.");
				return;
			}
			
			if(args.length < 1)
			{
				sender.sendMessage(ChatColor.RED + "Missing the name!");
				return;
			}
			
			GameMap map = (GameMap) scheme;
			String name = args[0];
			
			for(int i = 1; i < args.length; i++)
				name += " " + args[i];
			
			map.setName(name);
			sender.sendMessage(ChatColor.GREEN + "Map name set to '" + name + "'.");
		}
	};
}
