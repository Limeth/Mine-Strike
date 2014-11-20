package cz.minestrike.me.limeth.minestrike.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GameManager;

public class JoinExecutor implements CommandExecutor
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
		
		if(args.length <= 0)
		{
			player.sendMessage("Usage: /" + label + " [Game]");
		}
		else
		{
			MSPlayer msPlayer = MSPlayer.get(player);
			Scene scene = msPlayer.getScene();
			
			if(scene instanceof Game)
			{
				player.sendMessage(ChatColor.RED + "You are already playing in game " + ChatColor.YELLOW + ((Game) scene).getName() + ChatColor.RED + ".");
				player.sendMessage(ChatColor.RED + "To leave the game, type " + ChatColor.YELLOW + ChatColor.ITALIC + "/leave" + ChatColor.RED + ".");
				return true;
			}
			
			String id = args[0];
			Game game = GameManager.getGame(id);
			
			if(game == null)
			{
				player.sendMessage(ChatColor.RED + "Game '" + id + "' not found.");
				return true;
			}
			
			msPlayer.joinScene(game, SceneQuitReason.LEAVE, true);
		}
		
		return true;
	}
}
