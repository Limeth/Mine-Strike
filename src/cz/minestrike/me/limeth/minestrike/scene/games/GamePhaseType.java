package cz.minestrike.me.limeth.minestrike.scene.games;

public enum GamePhaseType
{
	LOBBY(false), WARM_UP, RUNNING, FINISHED;
	
	private final boolean timed;
	
	private GamePhaseType(boolean timed)
	{
		this.timed = timed;
	}
	
	private GamePhaseType()
	{
		this(true);
	}

	public boolean isTimed()
	{
		return timed;
	}
}
