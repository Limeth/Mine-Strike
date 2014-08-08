package cz.minestrike.me.limeth.minestrike;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.commands.JoinExecutor;
import cz.minestrike.me.limeth.minestrike.commands.MSExecutor;
import cz.minestrike.me.limeth.minestrike.commands.QuitExecutor;
import cz.minestrike.me.limeth.minestrike.commands.TopExecutor;
import cz.minestrike.me.limeth.minestrike.listeners.ConnectionListener;
import cz.minestrike.me.limeth.minestrike.listeners.ErrorListener;
import cz.minestrike.me.limeth.minestrike.listeners.InteractionListener;
import cz.minestrike.me.limeth.minestrike.listeners.InventoryListener;
import cz.minestrike.me.limeth.minestrike.listeners.PermissionListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerManager;
import cz.minestrike.me.limeth.minestrike.scene.games.GameManager;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.storagemanager.mysql.MySQLService;

public class MineStrike extends JavaPlugin
{
	private static MineStrike instance;
	private static MySQLService service;
	private static MSListenerManager msListenerManager;
	private static Logger logger;
	
	public static void main(String[] args) throws Exception
	{
		for(Rank rank : Rank.values())
			System.out.println(rank + " " + rank.getRequiredXP());
		
		if(true)
			return;
		
		String soundsPath = "/home/limeth/.minecraft/resourcepacks/Mine-Strike_1.0.5/assets/projectsurvive/sounds";
		String gson = SoundManager.buildSoundsJson(new File(soundsPath), new File(soundsPath, "counterstrike/weapons/cz75a"), "hostile");
		
		System.out.println(gson);
	}
	
	@Override
	public void onEnable()
	{
		instance = this;
		logger = Logger.getLogger("minecraft");
		
		try
		{
			registerMSListeners();
			registerBukkitListeners();
			loadData();
			connectService();
			redirectCommands();
			MSPlayer.loadOnlinePlayers();
			MSPlayer.startMovementLoop();
			disableWeather();
			
			for(Player player : Bukkit.getOnlinePlayers())
				ConnectionListener.getInstance().onPlayerJoin(new PlayerJoinEvent(player, null));
			
			Bukkit.broadcastMessage(Translation.ENABLED.getMessage());
			info("Mine-Strike successfully enabled! (v" + getDescription().getVersion() + ")");
		}
		catch(Exception e)
		{
			instance = null;
			
			Bukkit.getPluginManager().registerEvents(new ErrorListener(), this);
			warn("An error occured while enabling Mine-Strike.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.broadcastMessage(Translation.DISABLING.getMessage());
		
		@SuppressWarnings("unchecked")
		Set<MSPlayer> players = (Set<MSPlayer>) MSPlayer.getOnlinePlayers().clone();
		
		for(MSPlayer msPlayer : players)
		{
			Player player = msPlayer.getPlayer();
			
			ConnectionListener.getInstance().onPlayerQuit(new PlayerQuitEvent(player, null));
		}
		
		if(instance == null)
			return;
		
		try
		{
			saveData();
		}
		catch(Exception e)
		{
			warn("An error occured while saving Mine-Strike data.");
			e.printStackTrace();
		}
		
		MSPlayer.stopMovementLoop();
		MSPlayer.clearOnlinePlayers();
		disconnectService();
		info("Mine-Strike successfully disabled!");
		
		instance = null;
	}
	
	private void connectService() throws SQLException
	{
		service = new MySQLService(MSConfig.getMySQLIP(), MSConfig.getMySQLPort(), MSConfig.getMySQLDatabase(), MSConfig.getMySQLUsername(), MSConfig.getMySQLPassword());
		
		service.connect();
		service.prepareTable(MSPlayer.RECORD_STRUCTURE, MSConfig.getMySQLTablePlayers());
	}
	
	private void disconnectService()
	{
		service.disconnect();
		
		service = null;
	}
	
	private void loadData() throws Exception
	{
		MSConfig.load();
		Translation.load();
		SchemeManager.loadSchemes();
		PlotManager.loadPlots();
		GameManager.loadGames();
	}
	
	private void saveData() throws Exception
	{
		SchemeManager.saveSchemes();
		PlotManager.savePlots();
		GameManager.saveGames();
	}
	
	private void registerBukkitListeners()
	{
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(ConnectionListener.getInstance(), this);
		pm.registerEvents(new InteractionListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new PermissionListener(), this);
	}
	
	private void registerMSListeners()
	{
		msListenerManager = new MSListenerManager();
	}
	
	private void redirectCommands()
	{
		Bukkit.getPluginCommand("ms").setExecutor(new MSExecutor());
		Bukkit.getPluginCommand("join").setExecutor(new JoinExecutor());
		Bukkit.getPluginCommand("quit").setExecutor(new QuitExecutor());
		Bukkit.getPluginCommand("top").setExecutor(new TopExecutor());
	}
	
	public static <T> T debug(String message, T object)
	{
		if(MSConstant.DEBUG)
			System.out.println("[DEBUG] " + String.format(message, object));
		
		return object;
	}
	
	public static void debug(Object object)
	{
		if(MSConstant.DEBUG)
			System.out.println("[DEBUG] " + object);
	}
	
	public static void info(String string)
	{
		logger.log(Level.INFO, MSConstant.CONSOLE_PREFIX + string);
	}
	
	public static void warn(String string)
	{
		logger.log(Level.WARNING, MSConstant.CONSOLE_PREFIX + string);
		
		for(Player player : Bukkit.getOnlinePlayers())
			if(player.hasPermission("MineStrike.warn"))
				player.sendMessage(MSConstant.CONSOLE_PREFIX + ChatColor.RED + "[!] " + ChatColor.RESET + string);
	}
	
	private static void disableWeather()
	{
		for(World world : Bukkit.getWorlds())
		{
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
		}
	}
	
	public static MySQLService getService()
	{
		return service;
	}

	public static MineStrike getInstance()
	{
		return instance;
	}

	public static MSListenerManager getMSListenerManager()
	{
		return msListenerManager;
	}
}
