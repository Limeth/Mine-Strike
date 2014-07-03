package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class GameMenu extends Scheme
{
	@Expose private Point spawnPoint;
	
	public GameMenu(String id, Region region, Point spawnPoint)
	{
		super(SchemeType.MENU, id, region);
		
		Validate.notNull(spawnPoint, "The spawn point cannot be null!");
		
		this.spawnPoint = spawnPoint;
	}
	
	public GameMenu(String id, Region region)
	{
		this(id, region, region.getMidpoint());
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
				
				game.joinArena(msPlayer, team);
			}
			
		};
	}
	
	@Override
	public ArrayList<SchemeCommandHandler> getCommandHandlers()
	{
		ArrayList<SchemeCommandHandler> handlers = super.getCommandHandlers();
		
		handlers.add(Point.COMMAND_HANDLER);
		
		return handlers;
	}
	
	@Override
	public FilledHashMap<String, Point> getPoints()
	{
		FilledHashMap<String, Point> points = super.getPoints();
		
		points.put("spawnPoint", spawnPoint);
		
		return points;
	}
	
	@Override
	public boolean isSetUp()
	{
		return spawnPoint != null;
	}

	public Point getSpawnPoint()
	{
		return spawnPoint;
	}

	public void setSpawnPoint(Point spawnPoint)
	{
		this.spawnPoint = spawnPoint;
	}
}
