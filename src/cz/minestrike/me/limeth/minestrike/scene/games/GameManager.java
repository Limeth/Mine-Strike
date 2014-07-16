package cz.minestrike.me.limeth.minestrike.scene.games;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

public class GameManager
{
	public static final HashSet<Game<?, ?, ?, ?>> GAMES = new HashSet<Game<?, ?, ?, ?>>();
	public static final File FILE = new File("plugins/MineStrike/games.json");
	
	public static void loadGames() throws Exception
	{
		if(!FILE.isFile())
			return;
		
		JsonParser parser = new JsonParser();
		FileReader reader = new FileReader(FILE);
		
		JsonElement root = parser.parse(reader);

		GAMES.clear();
		
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
			GameType type = GameType.valueOf(rawType);
			Class<? extends Game<?, ?, ?, ?>> clazz = type.getCorrespondingClass();
			
			Game<?, ?, ?, ?> game = gson.fromJson(object, clazz);
			
			game.setOpen(true);
			game.register();
			
			if(game.isSetUp())
				game.setup();
		}
	}
	
	public static void saveGames() throws IOException
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
		
		for(Game<?, ?, ?, ?> game : GAMES)
		{
			if(!game.isOpen())
				continue;
			
			JsonElement element = gson.toJsonTree(game);
			
			root.add(element);
		}
		
		gson.toJson(root, writer);
		writer.close();
	}
	
	public static Game<?, ?, ?, ?> getGame(String id)
	{
		for(Game<?, ?, ?, ?> game : GAMES)
			if(game.getId().equals(id))
				return game;
		
		return null;
	}
	public static boolean register(Game<?, ?, ?, ?> game)
	{
		String id = game.getId();
		Game<?, ?, ?, ?> currentGame = getGame(id);
		
		if(currentGame != null)
			throw new IllegalArgumentException("A game with id '" + id + "' already exists.");
		
		return GAMES.add(game);
	}
	
	public static boolean unregister(String id)
	{
		Iterator<Game<?, ?, ?, ?>> iterator = GAMES.iterator();
		
		while(iterator.hasNext())
		{
			Game<?, ?, ?, ?> game = iterator.next();
			
			if(game.getId().equals(id))
			{
				iterator.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean unregister(Game<?, ?, ?, ?> game)
	{
		return GAMES.remove(game);
	}
}
