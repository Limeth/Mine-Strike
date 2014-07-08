package cz.minestrike.me.limeth.minestrike.util;

import java.util.BitSet;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Block;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;

public class SquareBitSet
{
	private final BitSet set;
	private final int width;
	
	public SquareBitSet(BitSet set, int width)
	{
		Validate.notNull(set, "The set cannot be null!");
		Validate.isTrue(set.length() % width == 0, "Invalid set width '" + width + "'.");
		
		this.set = set;
		this.width = width;
	}
	
	public SquareBitSet(int width, int height)
	{
		this(new BitSet(width * height), width);
	}
	
	public static SquareBitSet getSpawnableSpaces(Point base, Region region)
	{
		int width = region.getWidth();
		int depth = region.getDepth();
		int height = region.getHeight();
		SquareBitSet set = new SquareBitSet(width, depth);
		Point minPoint = region.getLower();
		int minX = minPoint.getX() + base.getX();
		int minY = minPoint.getY() + base.getY();
		int minZ = minPoint.getZ() + base.getZ();
		World world = MSConfig.getWorld();
		
		for(int x = 0; x < width; x++)
			for(int z = 0; z < depth; z++)
				for(int y = 0; y < height - 1; y++)
				{
					int absX = minX + x;
					int absY = minY + y;
					int absZ = minZ + z;
					Block groundBlock = world.getBlockAt(absX, absY - 1, absZ);
					
					if(groundBlock.isEmpty())
						continue;
					
					Block lowerBlock = world.getBlockAt(absX, absY, absZ);
					
					if(!lowerBlock.isEmpty())
						continue;
					
					Block higherBlock = world.getBlockAt(absX, absY + 1, absZ);
					
					if(!higherBlock.isEmpty())
						continue;
					
					set.set(x, z, true);
					
					break;
				}
		
		return set;
	}
	
	public static Integer getSpawnableY(int absX, int minY, int absZ, int height, World world)
	{
		for(int y = 0; y < height - 1; y++)
		{
			int absY = minY + y;
			Block groundBlock = world.getBlockAt(absX, absY - 1, absZ);
			
			if(groundBlock.isEmpty())
				continue;
			
			Block lowerBlock = world.getBlockAt(absX, absY, absZ);
			
			if(!lowerBlock.isEmpty())
				continue;
			
			Block higherBlock = world.getBlockAt(absX, absY + 1, absZ);
			
			if(!higherBlock.isEmpty())
				continue;
			
			return y;
		}
		
		return null;
	}
	
	public int getAmount(boolean value)
	{
		int amount = 0;
		
		for(int i = 0; i < set.length(); i++)
			if(set.get(i) == value)
				amount++;
		
		return amount;
	}
	
	public void set(int index, boolean value)
	{
		set.set(index, value);
	}
	
	public void set(int x, int y, boolean value)
	{
		int index = toIndex(x, y);
		
		set(index, value);
	}
	
	public boolean get(int index)
	{
		return set.get(index);
	}
	
	public boolean get(int x, int y)
	{
		int index = toIndex(x, y);
		
		return get(index);
	}
	
	public int toIndex(int x, int y)
	{
		return x + y * width;
	}
	
	public int[] fromIndex(int index)
	{
		return new int[] {getX(index), getY(index)};
	}
	
	public int getX(int index)
	{
		return index % width;
	}
	
	public int getY(int index)
	{
		return index / width;
	}

	public BitSet getSet()
	{
		return set;
	}
	
	public int getHeight()
	{
		return set.length() / width;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getArea()
	{
		return set.length();
	}
	
	@Override
	public String toString()
	{
		String result = "";
		int width = getWidth();
		int height = getHeight();
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
				result += get(x, y) ? "#" : "_";
			
			result += "\n";
		}
		
		return result;
	}
}
