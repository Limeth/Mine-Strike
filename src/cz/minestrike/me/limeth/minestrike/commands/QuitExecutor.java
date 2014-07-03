package cz.minestrike.me.limeth.minestrike.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.games.Game;

public class QuitExecutor implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Players only.");
			return true;
		}
		
		Player player = (Player) sender;
		MSPlayer msPlayer = MSPlayer.get(player);
		Game<?, ?, ?, ?> game = msPlayer.getGame();
		
		if(game == null)
		{
			player.sendMessage(ChatColor.RED + "You are not in a game.");
			return true;
		}
		
		game.quit(msPlayer, GameQuitReason.LEAVE, true);
		return true;
	}

}
