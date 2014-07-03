package cz.minestrike.me.limeth.minestrike.listeners.msPlayer.lobby;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;

public class MSLobbyListener extends MSListener
{
	private static MSLobbyListener instance;
	
	public static MSLobbyListener construct()
	{
		return instance = new MSLobbyListener();
	}
	
	public static MSLobbyListener getInstance()
	{
		return instance;
	}
	
	private MSLobbyListener()
	{
		super();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event, MSPlayer msPlayer)
	{
		Block block = event.getClickedBlock();
		
		if(block == null)
			return;
		
		BlockState state = block.getState();
		MaterialData data = state.getData();
		
		if(data instanceof Attachable && data instanceof Redstone)
		{
			Attachable attachable = (Attachable) data;
			Player player = msPlayer.getPlayer();
			BlockFace attachedFace = attachable.getAttachedFace();
			Block targetBlock = block;
			
			for(int i = 0; i < 2; i++)
			{
				targetBlock = targetBlock.getRelative(attachedFace);
				BlockState targetState = targetBlock.getState();
				String command = null;
				
				if(targetState instanceof Sign)
				{
					Sign sign = (Sign) targetState;
					String[] lines = sign.getLines();
					
					for(String line : lines)
					{
						line = line.trim();
						
						if(line.length() > 0)
							if(command == null)
								command = line;
							else
								command += " " + line;
					}
				}
				else if(targetState instanceof CommandBlock)
				{
					CommandBlock cmd = (CommandBlock) targetState;
					command = cmd.getCommand();
				}
				else
					continue;
				
				if(command.length() > 0)
					Bukkit.dispatchCommand(player, command);
			}
		}
	}
}
