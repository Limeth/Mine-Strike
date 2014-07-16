package cz.minestrike.me.limeth.minestrike.equipment;

import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GameType;

@SuppressWarnings("rawtypes")
public class EquipmentManagerInitializationException extends RuntimeException
{
	private static final long serialVersionUID = 6999206210727241015L;
	private final GameType gameType;
	private final Class<? extends Game> gameClass;
	
	public EquipmentManagerInitializationException(Throwable t, Class<? extends Game> class1, GameType gameType)
	{
		super(t);
		
		this.gameClass = class1;
		this.gameType = gameType;
	}
	
	@Override
	public String getMessage()
	{
		return "[" + gameClass + "; " + gameType + "] " + super.getMessage();
	}

	public Class<? extends Game> getGameClass()
	{
		return gameClass;
	}

	public GameType getGameType()
	{
		return gameType;
	}
}
