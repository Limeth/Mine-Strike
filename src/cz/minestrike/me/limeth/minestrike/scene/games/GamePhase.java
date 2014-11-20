package cz.minestrike.me.limeth.minestrike.scene.games;

import org.apache.commons.lang.Validate;

import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;

public abstract class GamePhase<T extends Game> implements MSListenerRedirector
{
	private final T game;
	private final GamePhaseType type;
	
	public GamePhase(T game, GamePhaseType type)
	{
		Validate.notNull(game, "The game must not be null!");
		Validate.notNull(type, "The type of the game phase must not be null!");
		
		this.game = game;
		this.type = type;
	}
	
	public abstract GamePhase<T> start();
	public abstract void cancel();
	
	public T getGame()
	{
		return game;
	}

	public GamePhaseType getType()
	{
		return type;
	}
}
