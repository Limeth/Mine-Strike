package cz.minestrike.me.limeth.minestrike.util;

import java.util.Collection;
import java.util.HashSet;

import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class MapAllocator
{
	private static final HashSet<Short> USED_IDS = new HashSet<Short>();
	
	private MapAllocator() {}
	
	public static void reset()
	{
		USED_IDS.clear();
	}
	
	public static FilledArrayList<Short> allocate(int amount)
	{
		FilledArrayList<Short> result = new FilledArrayList<Short>();
		
		for(short i = 0; i < amount; i++)
		{
			short id = allocate();
			
			result.add(id);
		}
		
		return result;
	}
	
	public static short allocate()
	{
		short id = 0;
		
		do
		{
			if(!USED_IDS.contains(id))
			{
				USED_IDS.add(id);
				return id;
			}
			
			id++;
		}
		while(true);
	}
	
	public static boolean free(short id)
	{
		return USED_IDS.remove(id);
	}
	
	public static void free(Collection<Short> ids)
	{
		for(Short id : ids)
			free(id);
	}
	
	@SuppressWarnings("unchecked")
	public static HashSet<Short> getUsedIds()
	{
		return (HashSet<Short>) USED_IDS.clone();
	}
}
