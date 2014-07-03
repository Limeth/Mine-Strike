package cz.minestrike.me.limeth.minestrike.util;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

import com.google.common.base.Preconditions;

public class LoreAttribute
{
	public static final String DIVIDING_SEQUENCE = ": ", PREFIX = ChatColor.DARK_GRAY + "»";
	private final String key;
	private Object value;
	
	public LoreAttribute(@Nonnull String key, @Nonnull Object value)
	{
		Preconditions.checkNotNull(key, "Key cannot be null.");
		Preconditions.checkNotNull(value, "Value cannot be null.");
		
		if(key.contains(DIVIDING_SEQUENCE))
			throw new IllegalArgumentException("The key must not contain '" + DIVIDING_SEQUENCE + "'.");
		
		this.key = key;
		this.value = value;
	}
	
	public static LoreAttribute deserialize(String serialized)
	{
		String[] args = serialized.split(DIVIDING_SEQUENCE, 2);
		
		if(args.length != 2)
			throw new IllegalArgumentException("Incorrect format, missing \": \".");
		
		return new LoreAttribute(args[0], args[1]);
	}
	
	public static LoreAttribute tryDeserialize(String serialized)
	{
		String[] args = serialized.split(DIVIDING_SEQUENCE, 2);
		
		if(args.length != 2)
			return null;
		
		return new LoreAttribute(args[0], args[1]);
	}
	
	public String serialize()
	{
		return key + ": " + value;
	}
	
	@Override
	public String toString()
	{
		return serialize();
	}
	
	public String getKey()
	{
		return key;
	}

	@SuppressWarnings("unchecked")
	public <T> T getKey(Class<T> clazz)
	{
		if(clazz == String.class)
			return (T) key;
		else if(clazz == Byte.class)
			return (T) keyToByte();
		else if(clazz == Short.class)
			return (T) keyToShort();
		else if(clazz == Integer.class)
			return (T) keyToInteger();
		else if(clazz == Long.class)
			return (T) keyToLong();
		else if(clazz == Float.class)
			return (T) keyToFloat();
		else if(clazz == Double.class)
			return (T) keyToDouble();
		else if(clazz == Boolean.class)
			return (T) keyToBoolean();
		else
			return (T) key;
	}
	
	public Byte keyToByte()
	{
		return Byte.parseByte(key);
	}
	
	public Short keyToShort()
	{
		return Short.parseShort(key);
	}
	
	public Integer keyToInteger()
	{
		return Integer.parseInt(key);
	}
	
	public Long keyToLong()
	{
		return Long.parseLong(key);
	}
	
	public Float keyToFloat()
	{
		return Float.parseFloat(key);
	}
	
	public Double keyToDouble()
	{
		return Double.parseDouble(key);
	}
	
	public Boolean keyToBoolean()
	{
		return Boolean.parseBoolean(key);
	}

	public Object getValue()
	{
		return value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> clazz)
	{
		if(clazz == String.class)
			return (T) valueToString();
		else if(clazz == Byte.class)
			return (T) valueToByte();
		else if(clazz == Short.class)
			return (T) valueToShort();
		else if(clazz == Integer.class)
			return (T) valueToInteger();
		else if(clazz == Long.class)
			return (T) valueToLong();
		else if(clazz == Float.class)
			return (T) valueToFloat();
		else if(clazz == Double.class)
			return (T) valueToDouble();
		else if(clazz == Boolean.class)
			return (T) valueToBoolean();
		else
			return (T) value;
	}
	
	public String valueToString()
	{
		if(value instanceof String)
			return (String) value;
		
		return value.toString();
	}
	
	public Byte valueToByte()
	{
		if(value instanceof Byte)
			return (Byte) value;
		
		return Byte.parseByte(value.toString());
	}
	
	public Short valueToShort()
	{
		if(value instanceof Short)
			return (Short) value;
		
		return Short.parseShort(value.toString());
	}
	
	public Integer valueToInteger()
	{
		if(value instanceof Integer)
			return (Integer) value;
		
		return Integer.parseInt(value.toString());
	}
	
	public Long valueToLong()
	{
		if(value instanceof Long)
			return (Long) value;
		
		return Long.parseLong(value.toString());
	}
	
	public Float valueToFloat()
	{
		if(value instanceof Float)
			return (Float) value;
		
		return Float.parseFloat(value.toString());
	}
	
	public Double valueToDouble()
	{
		if(value instanceof Double)
			return (Double) value;
		
		return Double.parseDouble(value.toString());
	}
	
	public Boolean valueToBoolean()
	{
		if(value instanceof Boolean)
			return (Boolean) value;
		
		return Boolean.parseBoolean(value.toString());
	}

	public void setValue(@Nonnull Object value)
	{
		Preconditions.checkNotNull(value, "Value cannot be null.");
		
		this.value = value;
	}
}
