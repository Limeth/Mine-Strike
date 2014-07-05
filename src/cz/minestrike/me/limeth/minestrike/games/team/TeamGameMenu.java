package cz.minestrike.me.limeth.minestrike.games.team;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.areas.schemes.MSStructureListener;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeType;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.Team;

public class TeamGameMenu extends GameMenu
{

	public TeamGameMenu(String id, Region region, Point spawnPoint)
	{
		super(SchemeType.MENU_TEAM, id, region, spawnPoint);
	}

	public TeamGameMenu(String id, Region region)
	{
		super(SchemeType.MENU_TEAM, id, region);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Scheme> MSStructureListener<T> newStructureListener(Structure<T> structure)
	{
		if(!(structure.getScheme() instanceof GameMenu))
			throw new IllegalArgumentException("The structure's scheme is not an instance of GameMenu.");
		
		return (MSStructureListener<T>) new MSStructureListener<GameMenu>((Structure<GameMenu>) structure) {
			
			@EventHandler(ignoreCancelled = true)
			public void onPlayerInteract(PlayerInteractEvent event, MSPlayer msPlayer)
			{
				Block block = event.getClickedBlock();
				
				if(block == null)
					return;
				
				BlockState state = block.getState();
				
				if(state == null)
					return;
				
				MaterialData blockData = state.getData();
				
				if(!(blockData instanceof Button))
					return;
				
				Button button = (Button) blockData;
				BlockFace attachedFace = button.getAttachedFace();
				Block holdingBlock = block.getRelative(attachedFace);
				MaterialData holdingBlockData = holdingBlock.getState().getData();
				
				if(!(holdingBlockData instanceof Colorable))
					return;
				
				Colorable colorable = (Colorable) holdingBlockData;
				DyeColor color = colorable.getColor();
				Team team = Team.getByItemColor(color);
				Game<?, ?, ?, ?> game = msPlayer.getGame();
				
				if(!(game instanceof TeamGame))
				{
					msPlayer.sendMessage(Translation.ERROR.getMessage());
					MineStrike.warn(game + " isn't an instance of TeamGame and thus cannot be used by TeamGameMenu.");
					return;
				}
				
				TeamGame<?, ?, ?, ?> teamGame = (TeamGame<?, ?, ?, ?>) game;
				
				teamGame.joinArena(msPlayer, team);
			}
		};
	}
}
