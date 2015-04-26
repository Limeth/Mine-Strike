package cz.minestrike.me.limeth.minestrike;

import cz.minestrike.me.limeth.minestrike.areas.DirectedPoint;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.equipment.rewards.RewardManager;
import cz.minestrike.me.limeth.minestrike.renderers.MapPollRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class MSConfig
{
	public static final File FILE = new File("plugins/MineStrike/config.json");
	public static final File SERVER_PROPERTIES = new File("server.properties");
	private static final Gson GSON = new GsonBuilder()
			.serializeNulls()
			.excludeFieldsWithoutExposeAnnotation()
			.setPrettyPrinting()
			.create();
	private static boolean debug;
	private static World world;
	private static DirectedPoint spawn;
	private static String languageName, mysqlIP, mysqlDatabase, mysqlUsername, mysqlPassword, mysqlTablePlayers, mysqlTableEquipment;
	private static int mysqlPort;
	private static Location lazySpawnLocation;
	private static String mysqlTableRewardRecords;
	private static long rewardPeriod;
	private static int rewardAmount;
	private static int maxRewardGenerosity;

	public static void load() throws Exception
	{
		JsonObject root = prepare();

		debug = root.get("debug").getAsBoolean();
		languageName = root.get("language").getAsString();
		world = Bukkit.getWorld(root.get("world").getAsString());
		spawn = GSON.fromJson(root.get("spawn"), DirectedPoint.class);
		rewardPeriod = root.get("rewardPeriod").getAsLong();
		rewardAmount = root.get("rewardAmount").getAsInt();
		maxRewardGenerosity = root.get("maxRewardGenerosity").getAsInt();
		
		JsonObject mysql = root.get("mysql").getAsJsonObject();
		mysqlIP = mysql.get("ip").getAsString();
		mysqlPort = mysql.get("port").getAsInt();
		mysqlDatabase = mysql.get("database").getAsString();
		mysqlUsername = mysql.get("username").getAsString();
		mysqlPassword = mysql.get("password").getAsString();
		
		JsonObject mysqlTables = mysql.get("tables").getAsJsonObject();
		mysqlTablePlayers = mysqlTables.get("players").getAsString();
		mysqlTableEquipment = mysqlTables.get("equipment").getAsString();
		mysqlTableRewardRecords = mysqlTables.get("rewardRecords").getAsString();
		
		//Init
		world.setSpawnLocation(spawn.getX(), spawn.getY(), spawn.getZ());
	}
	
	private static JsonObject prepare() throws IOException
	{
		prepareOthers();

		JsonParser parser = new JsonParser();
		FileReader reader = new FileReader(FILE);
		JsonElement rawRoot = parser.parse(reader);
		JsonObject root = (JsonObject) rawRoot;
		FileWriter writer = new FileWriter(FILE);

		if(!FILE.isFile())
		{
			if(FILE.exists())
				if(!FILE.delete())
					throw new IOException("Could not delete directory blocking the config file.");
			else
				if(!FILE.getParentFile().mkdirs())
					throw new IOException("Could not create parent directories for the config file.");
			
			if(!FILE.createNewFile())
				throw new IOException("Could not create the config file.");
		}

		if(!root.has("debug"))
			root.addProperty("debug", false);

		if(!root.has("language"))
			root.addProperty("language", Translation.DEFAULT_LANGUAGE_NAME);

		if(!root.has("world"))
			root.addProperty("world", getDefaultWorld());

		if(!root.has("spawn"))
			root.add("spawn", GSON.toJsonTree(new DirectedPoint(-1024, 96, -1024, 0f, 0f)));

		if(!root.has("rewardPeriod"))
			root.addProperty("rewardPeriod", RewardManager.REWARD_PERIOD_DEFAULT);

		if(!root.has("rewardAmount"))
			root.addProperty("rewardAmount", RewardManager.REWARD_AMOUNT_DEFAULT);

		if(!root.has("maxRewardGenerosity"))
			root.addProperty("maxRewardGenerosity", RewardManager.REWARD_GENEROSITY_MAX_DEFAULT);
		
		JsonObject mysql = root.has("mysql") ? root.get("mysql").getAsJsonObject() : new JsonObject();

		if(!mysql.has("ip"))
			mysql.addProperty("ip", "localhost");

		if(!mysql.has("port"))
			mysql.addProperty("port", 3306);

		if(!mysql.has("database"))
			mysql.addProperty("database", "minestrike");

		if(!mysql.has("username"))
			mysql.addProperty("username", "username");

		if(!mysql.has("password"))
			mysql.addProperty("password", "password");
		
		JsonObject mysqlTables = mysql.has("tables") ? mysql.get("tables").getAsJsonObject() : new JsonObject();

		if(!mysqlTables.has("players"))
			mysqlTables.addProperty("players", "minestrike_players");

		if(!mysqlTables.has("equipment"))
			mysqlTables.addProperty("equipment", "minestrike_equipment");

		if(!mysqlTables.has("rewardRecords"))
			mysqlTables.addProperty("rewardRecords", "minestrike_reward_records");

		mysql.add("tables", mysqlTables);
		root.add("mysql", mysql);

/*		JsonObject mysqlTables = mysql.has("tables") ? mysql.get("tables").getAsJsonObject() : new JsonObject();

		root.add(penetrationPercentage);*/

		GSON.toJson(root, writer);
		writer.close();

		return root;
	}

	private static void prepareOthers() throws IOException
	{
		if(!MapPollRenderer.DIRECTORY.isDirectory())
		{
			if(MapPollRenderer.DIRECTORY.exists())
				if(!MapPollRenderer.DIRECTORY.delete())
					throw new IOException("Could not delete a file blocking the map data directory path.");

			if(!MapPollRenderer.DIRECTORY.mkdirs())
				throw new IOException("Could not create the map data directory.");
		}
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

	public static boolean isDebug()
	{
		return debug;
	}

	public static String getLanguageName()
	{
		return languageName;
	}
	
	public static World getWorld()
	{
		return world;
	}

	@Deprecated
	@SuppressWarnings("unused")
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

	public static String getMySQLTableRewardRecords() {
		return mysqlTableRewardRecords;
	}

	public static long getRewardPeriod() {
		return rewardPeriod;
	}

	public static int getRewardAmount() {
		return rewardAmount;
	}

	public static int getMaxRewardGenerosity()
	{
		return maxRewardGenerosity;
	}
}
