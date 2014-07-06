package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.games.Game;

public abstract class MSGameListener<T extends Game<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider>> extends MSListener
{
	private final T game;
	
	public MSGameListener(T game)
	{
		super();
		this.game = game;
	}

	public T getGame()
	{
		return game;
	}
}
