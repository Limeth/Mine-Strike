package cz.minestrike.me.limeth.minestrike;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import cz.minestrike.me.limeth.minestrike.areas.DirectedPoint;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.renderers.MapPollRenderer;

public class MSConfig
{
	public static final File FILE = new File("plugins/MineStrike/config.json");
	public static final File SERVER_PROPERTIES = new File("server.properties");
	private static World world;
	private static DirectedPoint spawn;
	private static String languageName, mysqlIP, mysqlDatabase, mysqlUsername, mysqlPassword, mysqlTablePlayers, mysqlTableEquipment;
	private static int mysqlPort;
	private static Location lazySpawnLocation;
	
	public static void load() throws Exception
	{
		if(!FILE.exists())
			createDefault();
		
		if(!MapPollRenderer.DIRECTORY.isDirectory())
		{
			if(MapPollRenderer.DIRECTORY.exists())
				MapPollRenderer.DIRECTORY.delete();
			
			MapPollRenderer.DIRECTORY.mkdirs();
		}
		
		JsonParser parser = new JsonParser();
		FileReader reader = new FileReader(FILE);
		Gson builder = new GsonBuilder().create();
		JsonElement rawRoot = parser.parse(reader);
		JsonObject root = (JsonObject) rawRoot;
		
		languageName = root.get("language").getAsString();
		world = Bukkit.getWorld(root.get("world").getAsString());
		spawn = builder.fromJson(root.get("spawn"), DirectedPoint.class);
		
		JsonObject mysql = root.get("mysql").getAsJsonObject();
		mysqlIP = mysql.get("ip").getAsString();
		mysqlPort = mysql.get("port").getAsInt();
		mysqlDatabase = mysql.get("database").getAsString();
		mysqlUsername = mysql.get("username").getAsString();
		mysqlPassword = mysql.get("password").getAsString();
		
		JsonObject mysqlTables = mysql.get("tables").getAsJsonObject();
		mysqlTablePlayers = mysqlTables.get("players").getAsString();
		mysqlTableEquipment = mysqlTables.get("equipment").getAsString();
		
		//Init
		world.setSpawnLocation(spawn.getX(), spawn.getY(), spawn.getZ());
	}
	
	public static void createDefault() throws IOException
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
		Gson gson = new GsonBuilder()
				.serializeNulls()
				.excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting()
				.create();
		JsonObject root = new JsonObject();

		root.addProperty("language", Translation.DEFAULT_LANGUAGE_NAME);
		root.addProperty("world", getDefaultWorld());
		root.add("spawn", gson.toJsonTree(new DirectedPoint(-1024, 96, -1024, 0f, 0f)));
		
		JsonObject mysql = new JsonObject();
		
		mysql.addProperty("ip", "localhost");
		mysql.addProperty("port", 3306);
		mysql.addProperty("database", "minestrike");
		mysql.addProperty("username", "username");
		mysql.addProperty("password", "password");
		
		JsonObject mysqlTables = new JsonObject();

		mysqlTables.addProperty("players", "minestrike_players");
		mysqlTables.addProperty("equipment", "minestrike_equipment");
		mysql.add("tables", mysqlTables);
		
		root.add("mysql", mysql);
		
		gson.toJson(root, writer);
		writer.close();
	}
	
	private static String getDefaultWorld()
	{
		try
		{
			FileReader reader = new FileReader(SERVER_PROPERTIES);
			Properties properties = new Properties();
			properties.load(reader);
			
			return properties.getProperty("level-name");
		}
		catch(Exception e)
		{
			MineStrike.warn("Couldn't read server.properties, using 'world' as the default world.");
			e.printStackTrace();
			
			return "world";
		}
	}
	
	public static String getLanguageName()
	{
		return languageName;
	}
	
	public static World getWorld()
	{
		return world;
	}
	
	public static Point getSpawnPoint()
	{
		return spawn;
	}
	
	public static Location getSpawnLocation()
	{
		return lazySpawnLocation != null ? lazySpawnLocation : (lazySpawnLocation = spawn.getLocation(world, 0.5, 0, 0.5));
	}
	
	public static String getMySQLIP()
	{
		return mysqlIP;
	}
	
	public static int getMySQLPort()
	{
		return mysqlPort;
	}
	
	public static String getMySQLDatabase()
	{
		return mysqlDatabase;
	}
	
	public static String getMySQLURL()
	{
		return "jdbc:mysql://" + getMySQLIP() + ":" + getMySQLPort() + "/" + getMySQLDatabase();
	}
	
	public static String getMySQLUsername()
	{
		return mysqlUsername;
	}
	
	public static String getMySQLPassword()
	{
		return mysqlPassword;
	}
	
	public static String getMySQLTablePlayers()
	{
		return mysqlTablePlayers;
	}
	
	public static String getMySQLTableEquipment()
	{
		return mysqlTableEquipment;
	}
}
