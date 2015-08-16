package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import cz.minestrike.me.limeth.minestrike.scene.Scene;

public abstract class SceneMSListener<T extends Scene> extends MSListener
{
	private final T scene;
	
	public SceneMSListener(T scene)
	{
		super();
		this.scene = scene;
	}

	public T getScene()
	{
		return scene;
	}
}
