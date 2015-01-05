package cz.minestrike.me.limeth.minestrike;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import cz.minestrike.me.limeth.minestrike.areas.PlotManager;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;
import cz.minestrike.me.limeth.minestrike.commands.JoinExecutor;
import cz.minestrike.me.limeth.minestrike.commands.MSExecutor;
import cz.minestrike.me.limeth.minestrike.commands.QuitExecutor;
import cz.minestrike.me.limeth.minestrike.commands.TopExecutor;
import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerDAO;
import cz.minestrike.me.limeth.minestrike.dbi.MSPlayerData;
import cz.minestrike.me.limeth.minestrike.listeners.*;
import cz.minestrike.me.limeth.minestrike.listeners.clan.ClanListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerManager;
import cz.minestrike.me.limeth.minestrike.listeners.packet.PacketManager;
import cz.minestrike.me.limeth.minestrike.scene.games.GameManager;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.skife.jdbi.v2.DBI;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineStrike extends JavaPlugin
{
	private static MineStrike instance;
	private static DBI dbi;
	private static MSListenerManager msListenerManager;
	private static ProtocolManager protocolManager;
	private static Logger logger;
	
	public static void main(String[] args) throws Exception
	{
		System.out.println(MSPlayerData.class.newInstance());
		System.exit(0);

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
		try
		{
			protocolManager = ProtocolLibrary.getProtocolManager();
			instance = this;
			logger = Logger.getLogger("minecraft");
			
			registerMSListeners();
			registerBukkitListeners();
			PacketManager.registerListeners();
			loadData();
			setupDBI();
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
		PacketManager.unregisterListeners();
		Bukkit.getScheduler().cancelTasks(this);
		info("Mine-Strike successfully disabled!");
		
		instance = null;
	}
	
	private static void setupDBI()
	{
		dbi = new DBI(MSConfig.getMySQLURL(), MSConfig.getMySQLUsername(), MSConfig.getMySQLPassword());
		
		MSPlayerDAO.prepareTables();
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
		
		if(pm.isPluginEnabled("PSClans"))
			pm.registerEvents(new ClanListener(), this);
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
	
	public static DBI getDBI()
	{
		return dbi;
	}

	public static MineStrike getInstance()
	{
		return instance;
	}

	public static MSListenerManager getMSListenerManager()
	{
		return msListenerManager;
	}
	
	public static ProtocolManager getProtocolManager()
	{
		return protocolManager;
	}
}
