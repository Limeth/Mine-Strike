package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.games.Game;

public interface GameEvent
{
	public Game<?, ?, ?, ?> getGame();
}
