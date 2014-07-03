package cz.minestrike.me.limeth.minestrike.games;

public class TeamValue<T>
{
	private final T t, ct, none;
	
	public TeamValue(T t, T ct, T none)
	{
		this.t = t;
		this.ct = ct;
		this.none = none;
	}
	
	public TeamValue(T tANDct, T none)
	{
		this(tANDct, tANDct, none);
	}
	
	public TeamValue(T all)
	{
		this(all, all, all);
	}
	
	public T get(Team team)
	{
		switch(team)
		{
		case TERRORISTS: return t;
		case COUNTER_TERRORISTS: return ct;
		default: return none;
		}
	}

	public T getTerroristValue()
	{
		return t;
	}

	public T getCounterTerroristValue()
	{
		return ct;
	}

	public T getNone()
	{
		return none;
	}
}
