package cz.minestrike.me.limeth.minestrike.scene.games.team;

import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import cz.minestrike.me.limeth.minestrike.MSConstant;
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
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.projectsurvive.me.limeth.Title;

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
				Scene scene = msPlayer.getScene();
				
				if(!(scene instanceof TeamGame))
				{
					msPlayer.sendMessage(Translation.ERROR.getMessage());
					MineStrike.warn(scene + " isn't an instance of TeamGame and thus cannot be used by TeamGameMenu.");
					return;
				}
				
				Player player = msPlayer.getPlayer();
				TeamGame teamGame = (TeamGame) scene;
				Set<MSPlayer> playingPlayers = teamGame.getPlayingPlayers();
				int tPlayers = 0, ctPlayers = 0;
				
				for(MSPlayer playingPlayer : playingPlayers)
				{
					Team playingTeam = teamGame.getTeam(playingPlayer);
					
					if(playingTeam == Team.TERRORISTS)
						tPlayers++;
					else if(playingTeam == Team.COUNTER_TERRORISTS)
						ctPlayers++;
				}
				
				if(!player.hasPermission(MSConstant.PERMISSION_ADVANCED_TEAM_JOIN) && ((team == Team.TERRORISTS && tPlayers > ctPlayers)
						|| (team == Team.COUNTER_TERRORISTS && ctPlayers > tPlayers)))
				{
					Title.send(player, Translation.GAME_TEAMSELECT_FULL_TITLE.getMessage(),
							Translation.GAME_TEAMSELECT_FULL_SUBTITLE.getMessage());
					return;
				}
				
				teamGame.joinArena(msPlayer, team);
			}
		};
	}
}
