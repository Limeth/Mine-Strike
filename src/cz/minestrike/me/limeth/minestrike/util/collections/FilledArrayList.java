package cz.minestrike.me.limeth.minestrike.util.collections;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.Validate;

public class FilledArrayList<T> extends ArrayList<T>
{
	private static final long serialVersionUID = -3203237289992758926L;
	
	@Override
	public void add(int paramInt, T paramE)
	{
		Validate.notNull(paramE);
		
		super.add(paramInt, paramE);
	}
	
	@Override
	public boolean add(T paramE)
	{
		Validate.notNull(paramE);
		
		return super.add(paramE);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> paramCollection)
	{
		for(T value : paramCollection)
			Validate.notNull(value);
		
		return super.addAll(paramCollection);
	}
	
	@Override
	public boolean addAll(int paramInt, Collection<? extends T> paramCollection)
	{
		for(T value : paramCollection)
			Validate.notNull(value);
		
		return super.addAll(paramInt, paramCollection);
	}
	
	@Override
	public T set(int paramInt, T paramE)
	{
		Validate.notNull(paramE);
		
		return super.set(paramInt, paramE);
	}
}
