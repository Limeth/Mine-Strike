package cz.minestrike.me.limeth.minestrike.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Rank;
import cz.minestrike.me.limeth.minestrike.Translation;

public class TopExecutor implements CommandExecutor
{
	public static final String DATA_LAST_USE = "MineStrike.command.top.lastuse";
	public static final long DELAY = 10000;
	public static final int ENTRIES_PER_PAGE = 10;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			MSPlayer msPlayer = MSPlayer.get(player);
			long lastUse = msPlayer.getCustomData(DATA_LAST_USE, 0L);
			long now = System.currentTimeMillis();
			
			if(lastUse + DELAY > now)
			{
				long remainingMillis = lastUse + DELAY - now;
				int remainingSeconds = (int) Math.ceil(remainingMillis / 1000D);
				
				sender.sendMessage(Translation.COMMAND_TOP_COOLDOWN.getMessage(remainingSeconds));
				return true;
			}
			
			msPlayer.setCustomData(DATA_LAST_USE, now);
		}
		
		int page = 1;
		
		if(args.length > 0)
		{
			try
			{
				page = Integer.parseInt(args[0]);
			}
			catch(Exception e)
			{
				sender.sendMessage(Translation.COMMAND_TOP_INVALIDPAGE.getMessage());
				return true;
			}
			
			if(page < 1)
			{
				sender.sendMessage(Translation.COMMAND_TOP_INVALIDPAGE.getMessage());
				return true;
			}
		}
		
		showPage(sender, page);
		return true;
	}
	
	public static void showPage(final CommandSender sender, final int page)
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					runUnsafe();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					sender.sendMessage(Translation.ERROR.getMessage());
				}
			}
			
			public void runUnsafe() throws Exception
			{
				Class.forName("com.mysql.jdbc.Driver");
				String ip = MSConfig.getMySQLIP();
				int port = MSConfig.getMySQLPort();
				String database = MSConfig.getMySQLDatabase();
				String username = MSConfig.getMySQLUsername();
				String password = MSConfig.getMySQLPassword();
				String table = MSConfig.getMySQLTablePlayers();
				Connection connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);
				String sql = createSQL(page, table);
				ResultSet result = connection.prepareStatement(sql).executeQuery();
				int position = ENTRIES_PER_PAGE * (page - 1);
				
				while(result.next())
				{
					position++;
					String player = result.getString(1);
					long xp = result.getLong(2);
					Rank rank = Rank.getForXP(xp);
					String rankTag = rank.getTag();
					ChatColor nameColor = getColor(position);
					String playerName = nameColor + player;
					
					sender.sendMessage(Translation.COMMAND_TOP_ENTRY.getMessage(position, rankTag, playerName, xp));
				}
			}
		});
		
		thread.start();
	}
	
	private static String createSQL(int page, String table)
	{
		return "SELECT `username`, `xp` FROM `" + table + "` WHERE `xp` >= " + Rank.SILVER_I.getRequiredXP()
				+ " ORDER BY `xp` DESC LIMIT "
				+ ENTRIES_PER_PAGE * (page - 1) + ", " + ENTRIES_PER_PAGE;
	}
	
	private static ChatColor getColor(int position)
	{
		if(position <= 5)
			return ChatColor.LIGHT_PURPLE;
		else if(position <= 25)
			return ChatColor.RED;
		else if(position <= 50)
			return ChatColor.GOLD;
		else if(position <= 100)
			return ChatColor.YELLOW;
		else
			return ChatColor.WHITE;
	}
}
