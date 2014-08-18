package cz.minestrike.me.limeth.minestrike.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class LoreAttributes extends FilledHashMap<String, String>
{
	private static final long serialVersionUID = 1213430168558558623L;
	public static final String DIVIDING_SEQUENCE = ": ", PREFIX = ChatColor.DARK_GRAY + "»";
	public static final LoreAttributes TEMP = new LoreAttributes();
	private static final LoreAttributes PRIVATE_TEMP = new LoreAttributes();
	
	public void apply(ItemStack itemStack, boolean removePrevious)
	{
		List<String> lore = null;
		ItemMeta im = null;
		
		if(itemStack.hasItemMeta())
		{
			im = itemStack.getItemMeta();
			
			if(im.hasLore())
				lore = im.getLore();
		}
		
		if(im == null)
			im = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		
		if(lore == null)
			lore = new LinkedList<String>();
		
		LoreAttributes previousAttributes = this == TEMP ? PRIVATE_TEMP : TEMP;
		previousAttributes.clear();
		extract(itemStack, previousAttributes);
		
		Iterator<Entry<String, String>> previousEntryIterator = previousAttributes.entrySet().iterator();
		
		while(previousEntryIterator.hasNext())
		{
			Entry<String, String> entry = previousEntryIterator.next();
			String key = entry.getKey();
			
			if(removePrevious || containsKey(key))
			{
				String value = entry.getValue();
				String serialized = serialize(key, value);
				
				lore.remove(serialized);
				previousEntryIterator.remove();
			}
		}
		
		List<String> addition = serialize();
		
		lore.addAll(addition);
		im.setLore(lore);
		itemStack.setItemMeta(im);
	}
	
	public void apply(ItemStack itemStack)
	{
		apply(itemStack, true);
	}
	
	public static void extract(ItemStack itemStack, LoreAttributes result)
	{
		Validate.notNull(itemStack, "The ItemStack must not be null!");
		Validate.notNull(result, "The result cannot be null!");
		
		boolean loreMissing = false;
		ItemMeta im = null;
		
		if(itemStack.hasItemMeta())
		{
			im = itemStack.getItemMeta();
			
			if(!im.hasLore())
				loreMissing = true;
		}
		else
			loreMissing = true;
		
		if(loreMissing)
			return;
		
		List<String> lore = im.getLore();
		
		result.deserialize(lore);
	}
	
	private void deserialize(List<String> lore)
	{
		Validate.notNull(lore, "The lore cannot be null!");
		
		for(String serialized : lore)
			tryDeserialize(serialized);
	}
	
	private void deserialize(String serialized)
	{
		if(!serialized.startsWith(PREFIX))
			throw new DeserializationException("Incorrect format, missing \"" + PREFIX + "\" at the start. (" + serialized + ")");
		
		String[] args = serialized.substring(PREFIX.length()).split(DIVIDING_SEQUENCE, 2);
		
		if(args.length != 2)
			throw new DeserializationException("Incorrect format, missing \": \". (" + serialized + ")");
		
		put(args[0], args[1]);
	}
	
	private void tryDeserialize(String serialized)
	{
		try
		{
			deserialize(serialized);
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	private String serialize(String key, String value)
	{
		return PREFIX + key + DIVIDING_SEQUENCE + value;
	}

	private List<String> serialize()
	{
		FilledArrayList<String> lore = new FilledArrayList<String>();
		
		for(Entry<String, String> attribute : this.entrySet())
		{
			String key = attribute.getKey();
			String value = attribute.getValue();
			String serialized = serialize(key, value);
			
			lore.add(serialized);
		}
		
		return lore;
	}
	
	public Boolean getAsBoolean(String key)
	{
		return Boolean.parseBoolean(get(key));
	}
	
	public Byte getAsByte(String key)
	{
		return Byte.parseByte(get(key));
	}
	
	public Short getAsShort(String key)
	{
		return Short.parseShort(get(key));
	}
	
	public Integer getAsInt(String key)
	{
		return Integer.parseInt(get(key));
	}
	
	public Long getAsLong(String key)
	{
		return Long.parseLong(get(key));
	}
	
	public Float getAsFloat(String key)
	{
		return Float.parseFloat(get(key));
	}
	
	public Double getAsDouble(String key)
	{
		return Double.parseDouble(get(key));
	}
}
