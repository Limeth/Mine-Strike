package cz.minestrike.me.limeth.minestrike.events;

import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

//TODO Change to SceneEvent<Scene>
public class SceneEquipEvent extends MSPlayerEvent implements SceneEvent<Game>
{
	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private boolean forced;
	
	public SceneEquipEvent(Game game, MSPlayer who, boolean forced)
	{
		super(who);
		
		this.game = game;
		this.forced = forced;
	}
	
	public boolean isForced()
	{
		return forced;
	}

	@Override
	public Game getScene()
	{
		return game;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
