package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.scene.games.Game;

//TODO Change to SceneEvent<T extends Scene>
public interface GameEvent
{
	public Game getGame();
}
