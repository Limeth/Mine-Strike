package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.RegionSelector;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.Plot;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public abstract class Scheme
{
	@Expose private final SchemeType type;
	@Expose private String id;
	@Expose private Region region;
	
	public Scheme(SchemeType type, String id, Region region)
	{
		Validate.notNull(type, "The scheme type cannot be null!");
		Validate.notNull(id, "The ID cannot be null!");
		Validate.notNull(region, "The region cannot be null!");
		
		this.type = type;
		this.id = id;
		this.region = region;
	}
	
	public abstract boolean isSetUp();
	
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		return new FilledHashMap<String, RegionList>();
	}
	
	public FilledHashMap<String, Point> getPoints()
	{
		return new FilledHashMap<String, Point>();
	}
	
	public SchemeCommandHandler getCommandHandler(String command)
	{
		for(SchemeCommandHandler handler : getCommandHandlers())
			if(handler.getCommand().equalsIgnoreCase(command))
				return handler;
		
		return null;
	}
	
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> commands = new ArrayList<SchemeCommandHandler>();
		
		commands.add(COMMAND_MOVE);
		commands.add(COMMAND_REGION_SET);
		commands.add(COMMAND_HIGHLIGHT);
		
		return commands;
	}
	
	private static final SchemeCommandHandler COMMAND_MOVE = new SchemeCommandHandler("move", "ms scheme select [Scheme] move", "moves the selected scheme") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			Location loc = player.getLocation();
			Point playerPoint = Point.valueOf(loc);
			Region region = scheme.getRegion();
			String id = scheme.getId();
			
			region.moveTo(playerPoint);
			player.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' moved.");
		}
	};
	
	private static final SchemeCommandHandler COMMAND_HIGHLIGHT = new SchemeCommandHandler("highlight", "ms scheme select [Scheme] highlight", "highlights the selected scheme") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			Region region = scheme.getRegion();
			String id = scheme.getId();
			
			region.highlight(null, player);
			player.sendMessage(ChatColor.GREEN + "Scheme '" + id + "' highlighted.");
		}
	};
	
	private static final SchemeCommandHandler COMMAND_REGION_SET = new SchemeCommandHandler("setRegion", "ms scheme select [Scheme] setRegion", "sets the new scheme region") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage("Players only.");
				return;
			}
			
			Player player = (Player) sender;
			String playerName = player.getName();
			WorldEdit worldEdit = WorldEdit.getInstance();
			LocalSession weSession = worldEdit.getSession(playerName);
			
			if(weSession == null)
			{
				player.sendMessage(ChatColor.RED + "Incomplete WorldEdit region!");
				return;
			}
			
			LocalWorld weWorld = weSession.getSelectionWorld();
			RegionSelector selector = weSession.getRegionSelector(weWorld);
			com.sk89q.worldedit.regions.Region weRegion;
			
			try
			{
				weRegion = selector.getRegion();
			}
			catch(IncompleteRegionException e)
			{
				player.sendMessage(ChatColor.RED + "Incomplete WorldEdit region!");
				return;
			}
			
			Region region = Region.valueOf(weRegion);
			
			if(region.getWidth() > Plot.PLOT_SIZE)
			{
				player.sendMessage("Region is too long [x]! (" + region.getWidth() + "; max " + Plot.PLOT_SIZE + ")");
				return;
			}
			else if(region.getDepth() > Plot.PLOT_SIZE)
			{
				player.sendMessage("Region is too deep [z]! (" + region.getDepth() + "; max " + Plot.PLOT_SIZE + ")");
				return;
			}
			
			scheme.setRegion(region);
			
			player.sendMessage("Region of scheme '" + scheme.getId() + "' changed!");
		}
	};
	
	@SuppressWarnings("deprecation")
	public void build(World destWorld, Point destPoint)
	{
		World srcWorld = MSConfig.getWorld();
		
		for(int x = 0; x < region.getWidth(); x++)
			for(int y = 0; y < region.getHeight(); y++)
				for(int z = 0; z < region.getDepth(); z++)
				{
					Block srcBlock = srcWorld.getBlockAt(region.getLower().getX() + x, region.getLower().getY() + y, region.getLower().getZ() + z);
					Block destBlock = destWorld.getBlockAt(destPoint.getX() + x, destPoint.getY() + y, destPoint.getZ() + z);
					int srcId = srcBlock.getTypeId();
					int destId = destBlock.getTypeId();
					byte srcData = srcBlock.getData();
					byte destData = destBlock.getData();
					
					if(srcId != destId || srcData != destData)
						destBlock.setTypeIdAndData(srcId, srcData, false);
				}
	}
	
	public void build(Location loc)
	{
		build(loc.getWorld(), Point.valueOf(loc));
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		Validate.notNull(id, "The ID cannot be null!");
		
		this.id = id;
	}
	
	public Point getBase()
	{
		return region.getLower();
	}

	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		Validate.notNull(region, "The region cannot be null!");
		
		this.region = region;
	}
	
	public <T extends Scheme> MSStructureListener<T> newStructureListener(Structure<T> structure)
	{
		return null;
	}
}
