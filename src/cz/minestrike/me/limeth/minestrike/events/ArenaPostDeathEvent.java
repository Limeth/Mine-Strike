package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

/**
 * @author Limeth
 */
public class ArenaPostDeathEvent extends MSPlayerEvent implements GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game    game;

	public ArenaPostDeathEvent(Game game, MSPlayer msPlayer)
	{
		super(msPlayer);

		Validate.notNull(game, "The game cannot be null!");
		Validate.notNull(msPlayer, "The player cannot be null!");

		this.game = game;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public Game getGame()
	{
		return game;
	}
}
