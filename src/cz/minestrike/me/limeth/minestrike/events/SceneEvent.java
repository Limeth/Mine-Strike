package cz.minestrike.me.limeth.minestrike.events;

import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public interface SceneEvent<T extends Scene>
{
	public T getScene();
}
