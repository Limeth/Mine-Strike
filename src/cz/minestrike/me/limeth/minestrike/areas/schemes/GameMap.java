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


public abstract class GameMap extends Scheme
{
	public static final String SCHEME_ID_PREFIX = "map_";
	
	@Expose private String name;
	
	public GameMap(SchemeType type, String id, Region region, String name)
	{
		super(type, id, region);
		
		this.name = name;
	}
	
	@Override
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> handlers = super.getCommandHandlers();
		
		handlers.add(SET_NAME);
		
		return handlers;
	}
	
	@Override
	public boolean isSetUp()
	{
		return name != null;
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
