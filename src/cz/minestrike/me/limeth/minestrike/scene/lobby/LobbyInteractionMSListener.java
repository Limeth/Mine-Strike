package cz.minestrike.me.limeth.minestrike.scene.lobby;

import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class LobbyInteractionMSListener extends SceneMSListener<Lobby>
{
	public LobbyInteractionMSListener(Lobby lobby)
	{
		super(lobby);
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
				
				if(!(targetState instanceof Sign))
					continue;
				
				Sign sign = (Sign) targetState;
				String[] lines = sign.getLines();
				String command = lines[0];
				
				for(int lineIndex = 1; lineIndex < lines.length; lineIndex++)
					command += lines[lineIndex];
				
				if(command.length() > 0)
					Bukkit.dispatchCommand(player, command);
			}
		}
	}
}
