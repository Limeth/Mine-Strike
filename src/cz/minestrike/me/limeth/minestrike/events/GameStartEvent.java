package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event implements GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game game;

	public GameStartEvent(Game game)
	{
		Validate.notNull(game, "The game cannot be null!");
		
		this.game = game;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public Game getGame()
	{
		return game;
	}
}
