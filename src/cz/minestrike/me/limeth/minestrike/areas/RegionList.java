package cz.minestrike.me.limeth.minestrike.areas;

import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class RegionList extends FilledArrayList<Region>
{
	private static final long serialVersionUID = 507796953099601625L;
	
	@SuppressWarnings("deprecation")
	public void highlightAll(Point base, Player player)
	{
		int i = (int) (Math.random() * 16);
		
		for(Region region : this)
		{
			byte data = (byte) (i++ % 16);
			
			region.highlight(base, player, new MaterialData(Material.STAINED_GLASS, data), 20 * 5);
		}
	}
	
	public Point getRandomSpawnablePoint(Point base, Random random)
	{
		int surface = getSurface(base);
		
		if(surface <= 0)
			return null;
		
		int randomSurfaceIndex = random.nextInt(surface);
		int currentSurface = 0;
		
		for(Region region : this)
		{
			currentSurface += region.getSurface(base);
			
			if(currentSurface > randomSurfaceIndex)
				return region.getRandomSpawnablePoint(base, random);
		}
		
		return null;
	}
	
	public int getSurface(Point base)
	{
		int surface = 0;
		
		for(Region region : this)
			surface += region.getSurface(base);
		
		return surface;
	}
	
	public boolean isInside(double x, double y, double z)
	{
		for(Region region : this)
			if(region.isInside(x, y, z))
				return true;
		
		return false;
	}
	
	public boolean isInside(Vector vec)
	{
		for(Region region : this)
			if(region.isInside(vec))
				return true;
		
		return false;
	}
	
	public boolean isInside(Location loc)
	{
		for(Region region : this)
			if(region.isInside(loc))
				return true;
		
		return false;
	}
	
	public boolean isInside(Block block)
	{
		for(Region region : this)
			if(region.isInside(block))
				return true;
		
		return false;
	}
	
	public static final SchemeCommandHandler COMMAND_HANDLER = new SchemeCommandHandler("regionlist", "ms scheme select [Scheme] regionlist ...", "edits the region lists") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			FilledHashMap<String, RegionList> regionLists = scheme.getRegionsLists();
			
			if(args.length < 2)
			{
				String regionsets = "";
				
				for(String key : regionLists.keySet())
				{
					regionsets += key + ", ";
				}
				
				sender.sendMessage(ChatColor.GRAY + "Region lists in scheme '" + scheme.getId() + "':");
				sender.sendMessage(regionsets);
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] list");
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] tp [Index]");
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] highlight (Index)");
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] clear");
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] add");
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] regionlist [RegionList] remove [Index]");
				return;
			}
			
			RegionList regionList = null;
			String regionListName = args[0];
			
			for(Entry<String, RegionList> entry : regionLists.entrySet())
			{
				String key = entry.getKey();
				
				if(key.equalsIgnoreCase(regionListName))
				{
					regionList = entry.getValue();
					regionListName = key;
					break;
				}
			}
			
			if(regionList == null)
			{
				sender.sendMessage(ChatColor.RED + "Region list '" + regionListName + "' in scheme '" + scheme.getId() + "' not found.");
				return;
			}
			
			if(args[1].equalsIgnoreCase("list"))
			{
				if(regionList.size() <= 0)
				{
					sender.sendMessage("Region list '" + regionListName + "' is empty.");
					return;
				}
				
				String output = "Regions in region list '" + regionListName + "':";
				int i = 0;
				
				for(Region region : regionList)
				{
					Point midpoint = region.getMidpoint();
					int x = midpoint.getX();
					int y = midpoint.getY();
					int z = midpoint.getZ();
					
					output += "\n[" + i++ + "] - [" + x + "; " + y + "; " + z + "]";
				}
				
				sender.sendMessage(output);
			}
			else if(args[1].equalsIgnoreCase("tp") && args.length > 2)
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only.");
					return;
				}
				
				int index;
				Region region;
				
				try
				{
					index = Integer.parseInt(args[2]);
					region = regionList.get(index);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
					return;
				}
				
				Point midpoint = region.getMidpoint();
				Location location = midpoint.getLocation(MSConfig.getWorld());
				Player player = (Player) sender;
				
				player.teleport(location);
				player.sendMessage(ChatColor.GREEN + "Teleported to region #" + index + " in region list '" + regionListName + "'.");
			}
			else if(args[1].equalsIgnoreCase("highlight"))
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only.");
					return;
				}
				
				Player player = (Player) sender;
				
				if(args.length < 3)
				{
					regionList.highlightAll(scheme.getBase(), player);
					player.sendMessage(ChatColor.GREEN + "Region list '" + regionListName + "' highlighted.");
				}
				else
				{
					int index;
					Region region;
					
					try
					{
						index = Integer.parseInt(args[2]);
						region = regionList.get(index);
					}
					catch(Exception e)
					{
						sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
						return;
					}
					
					region.highlight(scheme.getBase(), player);
					player.sendMessage(ChatColor.GREEN + "Region #" + index + " in region list '" + regionListName + "' highlighted.");
				}
			}
			else if(args[1].equalsIgnoreCase("clear"))
			{
				if(regionList.size() <= 0)
				{
					sender.sendMessage("Region list '" + regionListName + "' is empty.");
					return;
				}
				
				regionList.clear();
				sender.sendMessage(ChatColor.GREEN + "Region list '" + regionListName + "' cleared.");
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only.");
					return;
				}
				
				Player player = (Player) sender;
				String playerName = player.getName();
				WorldEdit worldEdit = WorldEdit.getInstance();
				SessionManager sessionManager = worldEdit.getSessionManager();
				LocalSession weSession = sessionManager.findByName(playerName);
				
				if(weSession == null)
				{
					player.sendMessage(ChatColor.RED + "Incomplete WorldEdit region!");
					return;
				}
				
				com.sk89q.worldedit.world.World weWorld = weSession.getSelectionWorld();
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
				
				Point schemeBase = scheme.getBase();
				Region region = Region.valueOf(weRegion);
				
				region.subtract(schemeBase);
				regionList.add(region);
				player.sendMessage(ChatColor.GREEN + "Region added to region list '" + regionListName + "'.");
			}
			else if(args[1].equalsIgnoreCase("remove") && args.length > 2)
			{
				int index;
				
				try
				{
					index = Integer.parseInt(args[2]);
					
					regionList.remove(index);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured: " + e.getMessage());
					return;
				}
				
				sender.sendMessage(ChatColor.GREEN + "Region #" + index + " removed from region list '" + regionListName + "'.");
			}
		}
	};
}
