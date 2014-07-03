package cz.minestrike.me.limeth.minestrike;

public abstract class ParameterisedRunnable implements Runnable
{
	private Object[] parameters;
	
	public ParameterisedRunnable(Object... parameters)
	{
		this.parameters = parameters;
	}
	
	public abstract void run(Object... parameters);
	
	@Override
	public void run()
	{
		run(parameters);
	}

	public Object[] getParameters()
	{
		return parameters;
	}

	public void setParameters(Object[] parameters)
	{
		this.parameters = parameters;
	}
}
