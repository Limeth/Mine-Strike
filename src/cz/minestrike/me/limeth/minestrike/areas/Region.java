package cz.minestrike.me.limeth.minestrike.areas;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.SquareBitSet;

public class Region implements Iterable<Point>
{
	@Expose private Point lower, higher;
	private SquareBitSet lazySpawnableArea;
	
	public Region(Region superRegion, Vector first, Vector second)
	{
		this(Point.valueOf(first).subtract(superRegion.getLower()), Point.valueOf(second).subtract(superRegion.getLower()));
	}
	
	public Region(Point first, Point second)
	{
		set(first, second);
	}
	
	public static Region valueOf(com.sk89q.worldedit.regions.Region weRegion)
	{
		Point pos1 = Point.valueOf(weRegion.getMinimumPoint());
		Point pos2 = Point.valueOf(weRegion.getMaximumPoint());
		
		return new Region(pos1, pos2);
	}
	
	public final void set(Point first, Point second)
	{
		Validate.notNull(first, "The first point cannot be null!");
		Validate.notNull(second, "The second point cannot be null!");
		
		this.lower = Point.min(first, second);
		this.higher = Point.max(first, second);
	}
	
	public boolean isInside(double x, double y, double z)
	{
		return x >= lower.getX() && x < higher.getX() + 1
				&& y >= lower.getY() && y < higher.getY() + 1
				&& z >= lower.getZ() && z < higher.getZ() + 1;
	}
	
	public Point getRandomSpawnablePoint(Point base, Random random)
	{
		SquareBitSet set = getSpawnableArea(base);
		int spawnableSurface = set.getAmount(true);
		
		if(spawnableSurface <= 0)
			return null;
		
		int randomIndex = random.nextInt(spawnableSurface);
		int absRandomIndex = 0;
		int found = 0;
		
		while(absRandomIndex < set.getArea())
		{
			if(set.get(absRandomIndex))
			{
				found++;
				
				if(found > randomIndex)
					break;
			}
			
			absRandomIndex++;
		}
		
		int absX = set.getX(absRandomIndex) + lower.getX();
		int absZ = set.getY(absRandomIndex) + lower.getZ();
		
		int minY = lower.getY();
		int height = getHeight();
		Integer y = SquareBitSet.getSpawnableY(absX + base.getX(), minY + base.getY(), absZ + base.getZ(), height, MSConfig.getWorld());
		
		if(y == null)
			return null;
		
		int absY = minY + y;
		
		return new Point(absX, absY, absZ);
	}
	
	public int getSurface(Point base)
	{
		return getSpawnableArea(base).getAmount(true);
	}
	
	public SquareBitSet getSpawnableArea(Point base)
	{
		return lazySpawnableArea != null ? lazySpawnableArea : initSpawnableArea(base);
	}
	
	private SquareBitSet initSpawnableArea(Point base)
	{
		return lazySpawnableArea = SquareBitSet.getSpawnableSpaces(base, this);
	}

	@SuppressWarnings("deprecation")
	public void highlight(Point base, final Player player, MaterialData materialData, long duration)
	{
		final HashSet<Location> changedLocations = new HashSet<Location>();
		World world = MSConfig.getWorld();
		Material type = materialData.getItemType();
		byte data = materialData.getData();
		Point minPoint = getLower();
		
		if(base != null)
			minPoint.add(base);
		
		int minX = minPoint.getX();
		int minY = minPoint.getY();
		int minZ = minPoint.getZ();
		
		for(int x = 0; x < getWidth(); x++)
			for(int y = 0; y < getHeight(); y++)
				for(int z = 0; z < 2; z++)
				{
					Location loc = new Location(world, minX + x, minY + y, minZ + z * (getDepth() - 1));
					
					player.sendBlockChange(loc, type, data);
					changedLocations.add(loc);
				}
		
		for(int x = 0; x < 2; x++)
			for(int y = 0; y < getHeight(); y++)
				for(int z = 1; z < getDepth() - 1; z++)
				{
					Location loc = new Location(world, minX + x * (getWidth() - 1), minY + y, minZ + z);
					
					player.sendBlockChange(loc, type, data);
					changedLocations.add(loc);
				}
		
		for(int x = 1; x < getWidth() - 1; x++)
			for(int y = 0; y < 2; y++)
				for(int z = 1; z < getDepth() - 1; z++)
				{
					Location loc = new Location(world, minX + x, minY + y * (getHeight() - 1), minZ + z);
					
					player.sendBlockChange(loc, type, data);
					changedLocations.add(loc);
				}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				if(!player.isValid())
					return;
				
				for(Location loc : changedLocations)
				{
					Block block = loc.getBlock();
					Material type = block.getType();
					byte data = block.getData();
					
					player.sendBlockChange(loc, type, data);
				}
			}
		}, duration);
	}
	
	@SuppressWarnings("deprecation")
	public void highlight(Point base, Player player)
	{
		highlight(base, player, new MaterialData(Material.STAINED_GLASS, (byte) 0), 20 * 5);
	}
	
	public Point getMidpoint()
	{
		return lower.clone().add(higher.clone().subtract(lower).divide(2));
	}
	
	public boolean isInside(Vector vec)
	{
		return isInside(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public boolean isInside(Location loc)
	{
		return isInside(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public boolean isInside(Block block)
	{
		return isInside(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
	}
	
	public int getVolume()
	{
		return getWidth() * getHeight() * getDepth();
	}
	
	public int getWidth()
	{
		return higher.getX() - lower.getX() + 1;
	}
	
	public int getHeight()
	{
		return higher.getY() - lower.getY() + 1;
	}
	
	public int getDepth()
	{
		return higher.getZ() - lower.getZ() + 1;
	}
	
	public Region moveTo(Point point)
	{
		higher.subtract(lower).add(point);
		lower = point;
		return this;
	}
	
	public Region add(Point point)
	{
		lower.add(point);
		higher.add(point);
		return this;
	}
	
	public Region subtract(Point point)
	{
		lower.subtract(point);
		higher.subtract(point);
		return this;
	}
	
	public final Point getLower()
	{
		return lower.clone();
	}

	public final Region setLower(Point lower)
	{
		Validate.notNull(lower, "The lower point cannot be null!");
		
		set(lower, higher);
		return this;
	}

	public final Point getHigher()
	{
		return higher.clone();
	}

	public final Region setHigher(Point higher)
	{
		Validate.notNull(higher, "The higher point cannot be null!");
		
		set(lower, higher);
		return this;
	}
	
	@Override
	public Iterator<Point> iterator()
	{
		return new Iterator<Point>()
		{
			private final Point lower = Region.this.lower.clone();
			private final Point higher = Region.this.higher.clone();
			private Integer x, y, z;
			
			@Override
			public Point next()
			{
				if(!hasNext())
					throw new NoSuchElementException();
				
				if(x == null || y == null || z == null)
				{
					x = lower.getX();
					y = lower.getY();
					z = lower.getZ();
				}
				else
				{
					x++;
					
					if(x >= higher.getX() + 1)
					{
						x = lower.getX();
						y++;
					}
					
					if(y >= higher.getY() + 1)
					{
						y = lower.getY();
						z++;
					}
				}
				
				return new Point(x, y, z);
			}
			
			@Override
			public boolean hasNext()
			{
				return x == null || y == null || z == null || x < higher.getX() || y < higher.getY() || z < higher.getZ();
			}
		};
	}
	
	@Override
	public Region clone()
	{
		return new Region(lower, higher); 
	}
}
