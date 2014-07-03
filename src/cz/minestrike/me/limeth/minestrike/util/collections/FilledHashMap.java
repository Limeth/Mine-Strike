package cz.minestrike.me.limeth.minestrike.util.collections;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

public class FilledHashMap<K, V> extends HashMap<K, V>
{
	private static final long serialVersionUID = 8970585512020620665L;
	
	@Override
	public V put(K paramK, V paramV)
	{
		Validate.notNull(paramV);
		
		return super.put(paramK, paramV);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> paramMap)
	{
		for(V value : paramMap.values())
			Validate.notNull(value);
		
		super.putAll(paramMap);
	}
}
