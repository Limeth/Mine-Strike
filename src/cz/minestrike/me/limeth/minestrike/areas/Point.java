package cz.minestrike.me.limeth.minestrike.areas;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class Point implements Cloneable
{
	@Expose private int x, y, z;
	
	public Point(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Point valueOf(Location loc)
	{
		return new Point(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public static Point valueOf(Block block)
	{
		return new Point(block.getX(), block.getY(), block.getZ());
	}
	
	public static Point valueOf(Vector vec)
	{
		return new Point(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}
	
	public static Point valueOf(com.sk89q.worldedit.Vector vec)
	{
		return new Point(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}
	
	public static Point max(Point a, Point b)
	{
		int x = a.getX() > b.getX() ? a.getX() : b.getX();
		int y = a.getY() > b.getY() ? a.getY() : b.getY();
		int z = a.getZ() > b.getZ() ? a.getZ() : b.getZ();
		
		return new Point(x, y, z);
	}
	
	public static Point min(Point a, Point b)
	{
		int x = a.getX() < b.getX() ? a.getX() : b.getX();
		int y = a.getY() < b.getY() ? a.getY() : b.getY();
		int z = a.getZ() < b.getZ() ? a.getZ() : b.getZ();
		
		return new Point(x, y, z);
	}
	
	public Point clone()
	{
		return new Point(x, y, z);
	}
	
	public Location getLocation(World world)
	{
		return new Location(world, x, y, z);
	}
	
	public Location getLocation(World world, double offsetX, double offsetY, double offsetZ)
	{
		return getLocation(world).add(offsetX, offsetY, offsetZ);
	}
	
	public Block getBlock(World world)
	{
		return world.getBlockAt(x, y, z);
	}
	
	public Vector getVector()
	{
		return new Vector(x, y, z);
	}
	
	public Point add(int x, int y, int z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		
		return this;
	}
	
	public Point add(Point point)
	{
		return add(point.getX(), point.getY(), point.getZ());
	}
	
	public Point subtract(int x, int y, int z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		
		return this;
	}
	
	public Point subtract(Point point)
	{
		return subtract(point.getX(), point.getY(), point.getZ());
	}
	
	public Point multiply(double n)
	{
		this.x *= n;
		this.y *= n;
		this.z *= n;
		
		return this;
	}
	
	public Point divide(double n)
	{
		this.x /= n;
		this.y /= n;
		this.z /= n;
		
		return this;
	}
	
	public void set(Location loc)
	{
		setX(loc.getBlockX());
		setY(loc.getBlockY());
		setZ(loc.getBlockZ());
	}
	
	public void set(Vector vec)
	{
		setX(vec.getBlockX());
		setY(vec.getBlockY());
		setZ(vec.getBlockZ());
	}
	
	public void set(Block block)
	{
		setX(block.getX());
		setY(block.getY());
		setZ(block.getZ());
	}
	
	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}
	
	@Override
	public String toString()
	{
		return "[" + x + "; " + y + "; " + z + "]";
	}
	
	public static final SchemeCommandHandler COMMAND_HANDLER = new SchemeCommandHandler("point", "ms scheme select [Scheme] point ...", "sets a point of the scheme") {
		@Override
		public void execute(CommandSender sender, Scheme scheme, String[] args)
		{
			FilledHashMap<String, Point> map = scheme.getPoints();
			
			if(args.length < 1)
			{
				String points = "";
				
				for(String key : map.keySet())
				{
					points += key + ", ";
				}
				
				sender.sendMessage(ChatColor.GRAY + "Points in scheme '" + scheme.getId() + "':");
				sender.sendMessage(points);
				sender.sendMessage(ChatColor.ITALIC + "/ms scheme select [Scheme] point [Point]");
				return;
			}
			else
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only.");
					return;
				}
				
				String pointName = args[0];
				Point point = null;
				
				for(Entry<String, Point> entry : map.entrySet())
					if(entry.getKey().equalsIgnoreCase(pointName))
					{
						point = entry.getValue();
						pointName = entry.getKey();
						break;
					}
				
				if(point == null)
				{
					sender.sendMessage(ChatColor.RED + "Unknown point '" + pointName + "'.");
					return;
				}
				
				Player player = (Player) sender;
				Location loc = player.getLocation();
				Point regionBase = scheme.getBase();
				
				point.set(loc);
				point.subtract(regionBase);
				player.sendMessage(ChatColor.GREEN + "Point '" + pointName + "' set.");
			}
		}
	};
}
