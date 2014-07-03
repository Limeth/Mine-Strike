package cz.minestrike.me.limeth.minestrike.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RendererUtil
{
	public static final int MAP_SIZE = 128;
	
	public static void setRenderer(MapView view, MapRenderer renderer)
	{
		List<MapRenderer> renderers = view.getRenderers();
		
		renderers.clear();
		renderers.add(renderer);
	}
	
	@SuppressWarnings("deprecation")
	public static void setRenderer(World world, short id, MapRenderer renderer)
	{
		MapView view;
		
		while((view = Bukkit.getMap(id)) == null)
			Bukkit.createMap(world);
		
		setRenderer(view, renderer);
	}
}
