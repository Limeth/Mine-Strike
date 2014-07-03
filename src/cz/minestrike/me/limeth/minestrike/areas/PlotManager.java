package cz.minestrike.me.limeth.minestrike.areas;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;

public class PlotManager
{
	public static final HashSet<Plot<? extends Scheme>> PLOTS = new HashSet<Plot<? extends Scheme>>();
	public static final File FILE = new File("plugins/MineStrike/plots.json");
	
	public static void loadPlots() throws Exception
	{
		if(!FILE.isFile())
			return;
		
		JsonParser parser = new JsonParser();
		FileReader reader = new FileReader(FILE);
		JsonElement root = parser.parse(reader);
		Gson gson = new GsonBuilder().create();

		PLOTS.clear();
		
		if(!root.isJsonArray())
			throw new Exception("Not an array!");
		
		JsonArray array = root.getAsJsonArray();
		
		for(JsonElement element : array)
			try
			{
				@SuppressWarnings("unchecked")
				Plot<? extends Scheme> plot = gson.fromJson(element, Plot.class);
				
				plot.setup();
				PLOTS.add(plot);
			}
			catch(Exception e)
			{
				MineStrike.warn("An error occured while loading a plot: " + e.getMessage());
				
				if(!(e instanceof IllegalArgumentException))
					e.printStackTrace();
			}
	}
	
	public static void savePlots() throws IOException
	{
		if(!FILE.isFile())
		{
			if(FILE.exists())
				FILE.delete();
			else
				FILE.getParentFile().mkdirs();
			
			FILE.createNewFile();
		}
		
		FileWriter writer = new FileWriter(FILE);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.serializeNulls().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		JsonArray root = new JsonArray();
		
		for(Plot<? extends Scheme> plot : PLOTS)
		{
			JsonElement object = gson.toJsonTree(plot);
			
			root.add(object);
		}
		
		gson.toJson(root, writer);
		writer.close();
	}
	
	public static boolean unregisterStructure(Structure<? extends Scheme> structure)
	{
		Plot<? extends Scheme> plot = structure.getPlot();
		
		if(!plot.hasStructure())
			return false;
		
		plot.setStructure(null);
		
		return true;
	}
	
	public static <T extends Scheme> Structure<T> registerStructure(T scheme)
	{
		Plot<T> plot = getFreePlot(scheme);
		Structure<T> structure = new Structure<T>(plot, scheme);
		
		plot.setStructure(structure);
		plot.build();
		
		return structure;
	}
	
	public HashSet<Structure<? extends Scheme>> getStructures()
	{
		HashSet<Structure<? extends Scheme>> set = new HashSet<Structure<? extends Scheme>>();
		
		for(Plot<? extends Scheme> plot : PLOTS)
			if(plot.hasStructure())
				set.add(plot.getStructure());
		
		return set;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Scheme> Plot<T> getFreePlot(T scheme)
	{
		for(Plot<? extends Scheme> plot : PLOTS)
			if(plot.getScheme().equals(scheme) && !plot.hasStructure())
				return (Plot<T>) plot;
		
		return addPlot(scheme);
	}
	
	private static <T extends Scheme> Plot<T> addPlot(T scheme)
	{
		int i = 0;
		
		do
		{
			Plot<? extends Scheme> plot = getPlot(i);
			
			if(plot == null)
			{
				Plot<T> newPlot = new Plot<T>(i, scheme);
				
				PLOTS.add(newPlot);
				
				return newPlot;
			}
			
			i++;
		}
		while(true);
	}
	
	public static Plot<? extends Scheme> getPlot(int id)
	{
		for(Plot<? extends Scheme> plot : PLOTS)
			if(plot.getId() == id)
				return plot;
		
		return null;
	}
}
