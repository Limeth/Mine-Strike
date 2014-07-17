package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import cz.minestrike.me.limeth.minestrike.scene.Scene;

public abstract class MSSceneListener<T extends Scene> extends MSListener
{
	private final T scene;
	
	public MSSceneListener(T scene)
	{
		super();
		this.scene = scene;
	}

	public T getScene()
	{
		return scene;
	}
}
