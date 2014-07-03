package cz.minestrike.me.limeth.minestrike.games;

import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;

public abstract class MSGameListener<T extends Game<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentManager>> extends MSListener
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
