package cz.minestrike.me.limeth.minestrike.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreAttributes
{
	public static final LoreAttributes TEMP_ATTRIBUTES = new LoreAttributes();
	private final List<LoreAttribute> attributes;
	
	public LoreAttributes(List<LoreAttribute> attributes)
	{
		this.attributes = attributes;
	}
	
	public LoreAttributes()
	{
		this(new LinkedList<LoreAttribute>());
	}
	
	public static LoreAttributes deserialize(List<String> lore)
	{
		Validate.notNull(lore, "The lore cannot be null!");
		
		LinkedList<LoreAttribute> attributes = new LinkedList<LoreAttribute>();
		
		for(String serialized : lore)
		{
			LoreAttribute attribute = LoreAttribute.tryDeserialize(serialized);
			
			if(attribute != null)
				attributes.add(attribute);
		}
		
		return new LoreAttributes(attributes);
	}
	
	public static LoreAttributes extract(ItemStack itemStack)
	{
		Validate.notNull(itemStack, "The ItemStack must not be null!");
		
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
			return new LoreAttributes();
		
		List<String> lore = im.getLore();
		LinkedList<String> attributesOnly = new LinkedList<String>();
		
		for(String raw : lore)
		{
			if(!raw.startsWith(LoreAttribute.PREFIX))
				continue;
			
			String trimmed = raw.substring(LoreAttribute.PREFIX.length());
			
			attributesOnly.add(trimmed);
		}
		
		return deserialize(attributesOnly);
	}
	
	@Override
	public String toString()
	{
		return "LoreAttributes [attributes=" + attributes + "]";
	}

	public List<String> serialize()
	{
		LinkedList<String> lore = new LinkedList<String>();
		
		if(attributes == null)
			return lore;
		
		for(LoreAttribute attribute : attributes)
			if(attribute != null)
				lore.add(attribute.serialize());
		
		return lore;
	}
	
	public void apply(ItemStack itemStack)
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
		
		LoreAttributes past = extract(itemStack);
		
		for(LoreAttribute attribute : past.attributes)
			lore.remove(LoreAttribute.PREFIX + attribute.serialize());
		
		List<String> addition = serialize();
		
		for(int i = 0; i < addition.size(); i++)
			addition.set(i, LoreAttribute.PREFIX + addition.get(i));
		
		lore.addAll(addition);
		im.setLore(lore);
		itemStack.setItemMeta(im);
	}
	
	public void clear()
	{
		attributes.clear();
	}
	
	public LoreAttribute get(String key)
	{
		if(key == null)
			return null;
		
		for(LoreAttribute attribute : attributes)
			if(attribute != null && key.equals(attribute.getKey()))
				return attribute;
		
		return null;
	}
	
	public LoreAttribute put(String key, Object value)
	{
		LoreAttribute current = get(key);
		
		if(current != null)
			current.setValue(value);
		else
		{
			current = new LoreAttribute(key, value);
			
			attributes.add(current);
		}
		
		return current;
	}
	
	public boolean containsKey(String key)
	{
		if(key == null)
			return false;
		
		for(LoreAttribute attribute : attributes)
			if(attribute != null && key.equals(attribute.getKey()))
				return true;
		
		return false;
	}
	
	public boolean contains(LoreAttribute attribute)
	{
		return attributes.contains(attribute);
	}
	
	public boolean remove(String key)
	{
		if(key == null)
			return false;
		
		for(int i = 0; i < attributes.size(); i++)
		{
			LoreAttribute attribute = attributes.get(i);
			
			if(attribute != null && key.equals(attribute.getKey()))
			{
				attributes.remove(attribute);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean remove(LoreAttribute attribute)
	{
		return attributes.remove(attribute);
	}
	
	public List<LoreAttribute> getAttributes()
	{
		return attributes;
	}
}
