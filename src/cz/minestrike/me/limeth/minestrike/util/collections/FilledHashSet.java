package cz.minestrike.me.limeth.minestrike.util.collections;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.Validate;

public class FilledHashSet<T> extends HashSet<T>
{
	private static final long serialVersionUID = -8259476755692745283L;
	
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
}
