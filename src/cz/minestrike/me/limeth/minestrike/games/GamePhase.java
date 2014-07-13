package cz.minestrike.me.limeth.minestrike.games;

import org.apache.commons.lang.Validate;

import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;

public abstract class GamePhase<Lo extends GameLobby, Me extends GameMenu, Ma extends GameMap, EM extends EquipmentProvider> implements MSListenerRedirector
{
	private final Game<Lo, Me, Ma, EM> game;
	private final GamePhaseType type;
	
	public GamePhase(Game<Lo, Me, Ma, EM> game, GamePhaseType type)
	{
		Validate.notNull(game, "The game must not be null!");
		Validate.notNull(type, "The type of the game phase must not be null!");
		
		this.game = game;
		this.type = type;
	}
	
	public abstract GamePhase<Lo, Me, Ma, EM> start();
	public abstract void cancel();
	
	public Game<Lo, Me, Ma, EM> getGame()
	{
		return game;
	}

	public GamePhaseType getType()
	{
		return type;
	}
}
