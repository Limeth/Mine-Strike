package cz.minestrike.me.limeth.minestrike.events;

import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public class GameEquipEvent extends MSPlayerEvent implements GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game game;
	private boolean forced;
	
	public GameEquipEvent(Game game, MSPlayer who, boolean forced)
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
	public Game getGame()
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
