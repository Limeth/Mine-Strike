package cz.minestrike.me.limeth.minestrike;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.commands.JoinExecutor;
import cz.minestrike.me.limeth.minestrike.commands.MSExecutor;
import cz.minestrike.me.limeth.minestrike.commands.QuitExecutor;
import cz.minestrike.me.limeth.minestrike.games.GameManager;
import cz.minestrike.me.limeth.minestrike.listeners.ConnectionListener;
import cz.minestrike.me.limeth.minestrike.listeners.ErrorListener;
import cz.minestrike.me.limeth.minestrike.listeners.InteractionListener;
import cz.minestrike.me.limeth.minestrike.listeners.InventoryListener;
import cz.minestrike.me.limeth.minestrike.listeners.PermissionListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerManager;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.lobby.MSLobbyListener;
import cz.minestrike.me.limeth.storagemanager.mysql.MySQLService;

public class MineStrike extends JavaPlugin
{
	private static MineStrike instance;
	private static MySQLService service;
	private static MSListenerManager msListenerManager;
	private static Logger logger;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		try
		{
			loadData();
			connectService();
			registerBukkitListeners();
			registerMSListeners();
			redirectCommands();
			MSPlayer.loadOnlinePlayers();
			MSPlayer.startMovementLoop();
			logger = Logger.getLogger("minecraft");
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
		SchemeManager.loadSchemes();
		PlotManager.loadPlots();
		GameManager.loadGames();
		Translation.load();
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
		
		pm.registerEvents(new ConnectionListener(), this);
		pm.registerEvents(new InteractionListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new PermissionListener(), this);
	}
	
	private void registerMSListeners()
	{
		msListenerManager = new MSListenerManager();
		MSLobbyListener.construct();
	}
	
	private void redirectCommands()
	{
		Bukkit.getPluginCommand("ms").setExecutor(new MSExecutor());
		Bukkit.getPluginCommand("join").setExecutor(new JoinExecutor());
		Bukkit.getPluginCommand("quit").setExecutor(new QuitExecutor());
	}
	
	public static void debug(Object object)
	{
		if(MSConstant.DEBUG)
			System.out.println("[DEBUG] " + object);
	}
	
	public static void info(String string)
	{
		logger.log(Level.INFO, MSConstant.CONSOLE_PREFIX + string);
		//Bukkit.getConsoleSender().sendMessage(MSConstant.CONSOLE_PREFIX + string);
	}
	
	public static void warn(String string)
	{
		logger.log(Level.WARNING, MSConstant.CONSOLE_PREFIX + string);
		//Bukkit.getConsoleSender().sendMessage(MSConstant.CONSOLE_PREFIX + ChatColor.WHITE + "[!] " + ChatColor.RED + string);
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
