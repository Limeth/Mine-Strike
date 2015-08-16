package cz.minestrike.me.limeth.minestrike.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

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
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
		{
			player.sendMessage(ChatColor.RED + "You are not in a game.");
			return true;
		}
		
		msPlayer.quitScene(SceneQuitReason.LEAVE, true);
		return true;
	}
}
