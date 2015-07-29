package cz.minestrike.me.limeth.minestrike.areas.schemes;

import com.google.common.base.Preconditions;
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
import java.util.Optional;


public abstract class GameMap extends Scheme
{
	public static final String SCHEME_ID_PREFIX = "map_";
	
	@Expose private String name;
	@Expose private RegionList spectatorZones;
	@Expose private Point spectatorSpawn;
	
	public GameMap(SchemeType type, String id, Region region, String name, RegionList spectatorZones, Point spectatorSpawn)
	{
		super(type, id, region);

        Preconditions.checkNotNull(spectatorZones, "The spectator zones must not be null!");
		Preconditions.checkNotNull(spectatorSpawn, "The spectator spawn must not be null!");

		this.name = name;
        this.spectatorZones = spectatorZones;
        this.spectatorSpawn = spectatorSpawn;
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
		return name != null;
	}

    @Override
    public FilledHashMap<String, RegionList> getRegionsLists()
    {
        FilledHashMap<String, RegionList> regionLists = super.getRegionsLists();

        regionLists.put("spectatorZones", spectatorZones);

        return regionLists;
    }

    @Override
    public FilledHashMap<String, Point> getPoints()
    {
        FilledHashMap<String, Point> points = super.getPoints();

        points.put("spectatorSpawn", spectatorSpawn);

        return points;
    }

    public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
