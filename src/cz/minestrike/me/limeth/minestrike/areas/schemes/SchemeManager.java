package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

public class SchemeManager
{
	public static final Set<Scheme> SCHEMES = Sets.newHashSet();
	public static final File FILE = new File("plugins/MineStrike/schemes.json");
	
	public static void loadSchemes() throws Exception
	{
		if(!FILE.isFile())
			return;
		
		JsonParser parser = new JsonParser();
		FileReader reader = new FileReader(FILE);
		
		JsonElement root = parser.parse(reader);

		SCHEMES.clear();
		
		if(!root.isJsonArray())
			throw new Exception("Not an array!");
		
		JsonArray array = root.getAsJsonArray();
		Gson gson = new GsonBuilder().create();
		
		for(JsonElement element : array)
		{
			if(!(element instanceof JsonObject))
				continue;
			
			JsonObject object = (JsonObject) element;
			String rawType = object.get("type").getAsString();
			SchemeType type = SchemeType.valueOf(rawType);
			Class<? extends Scheme> clazz = type.getCorrespondingClass();
			
			Scheme scheme = gson.fromJson(object, clazz);
			
			SCHEMES.add(scheme);
		}
	}
	
	public static void saveSchemes() throws IOException
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
		
		for(Scheme scheme : SCHEMES)
		{
			JsonElement element = gson.toJsonTree(scheme);
			
			root.add(element);
		}
		
		gson.toJson(root, writer);
		writer.close();
	}
	
	public static Scheme getScheme(String id)
	{
		for(Scheme scheme : SCHEMES)
			if(scheme.getId().equals(id))
				return scheme;
		
		return null;
	}
}
