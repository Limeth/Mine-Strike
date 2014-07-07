package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;

import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class EquipmentCustomization implements ConfigurationSerializable
{
	private final String name, skin;
	private final Color color;
	private final ImmutableList<String> lore;
	
	private EquipmentCustomization(String name, String skin, Color color, ImmutableList<String> lore)
	{
		this.name = name;
		this.skin = skin;
		this.color = color;
		this.lore = lore;
	}
	
	public static class EquipmentCustomizationBuilder
	{
		private String name, skin;
		private Color color;
		private ImmutableList.Builder<String> lore = ImmutableList.builder();
		
		private EquipmentCustomizationBuilder() {}
		
		public EquipmentCustomization build()
		{
			return new EquipmentCustomization(name, skin, color, lore.build());
		}
		
		public EquipmentCustomizationBuilder name(String name)
		{
			this.name = name;
			return this;
		}
		
		public EquipmentCustomizationBuilder skin(String skin)
		{
			this.skin = skin;
			return this;
		}
		
		public EquipmentCustomizationBuilder color(Color color)
		{
			this.color = color;
			return this;
		}
		
		public EquipmentCustomizationBuilder addLore(String... lines)
		{
			lore.add(lines);
			return this;
		}
		
		public EquipmentCustomizationBuilder addLore(Collection<String> lines)
		{
			lore.addAll(lines);
			return this;
		}
	}
	
	public static EquipmentCustomizationBuilder builder()
	{
		return new EquipmentCustomizationBuilder();
	}
	
	public void apply(EquipmentType equipment, ItemStack itemStack)
	{
		ItemMeta im = itemStack.getItemMeta();
		
		if(name != null)
			im.setDisplayName(name);
		
		if(lore.size() > 0)
		{
			List<String> newLore;
			
			if(im.hasLore())
				newLore = im.getLore();
			else
				newLore = new ArrayList<String>();
			
			newLore.addAll(lore);
			im.setLore(lore);
		}
		
		if(color != null && im instanceof FireworkEffectMeta)
		{
			FireworkEffectMeta fem = (FireworkEffectMeta) im;
			
			fem.setEffect(FireworkEffect.builder().withColor(color).build());
		}
		
		if(skin != null)
		{
			LoreAttributes.TEMP_ATTRIBUTES.clear();
			LoreAttributes.TEMP_ATTRIBUTES.put("skin", skin);
			LoreAttributes.TEMP_ATTRIBUTES.apply(itemStack);
		}
		
		itemStack.setItemMeta(im);
	}
	
	@SuppressWarnings("unchecked")
	public static EquipmentCustomization deserialize(Map<String, Object> map)
	{
		EquipmentCustomizationBuilder builder = builder();
		
		if(map.containsKey("name"))
			builder.name((String) map.get("name"));
		
		if(map.containsKey("skin"))
			builder.skin((String) map.get("skin"));
		
		if(map.containsKey("color"))
			builder.color(Color.deserialize((Map<String, Object>) map.get("color")));
		
		if(map.containsKey("lore"))
		{
			List<String> list = (List<String>) map.get("lore");
			
			builder.addLore(list);
		}
		
		return builder.build();
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(name != null)
			map.put("name", name);
		
		if(skin != null)
			map.put("skin", skin);
		
		if(color != null)
			map.put("color", color.serialize());
		
		if(lore.size() > 0)
			map.put("lore", lore);
		
		return map;
	}

	public String getName()
	{
		return name;
	}

	public String getSkin()
	{
		return skin;
	}

	public Color getColor()
	{
		return color;
	}

	public ImmutableList<String> getLore()
	{
		return lore;
	}

	@Override
	public String toString()
	{
		return "EquipmentCustomization [name=" + name + ", skin=" + skin + ", color=" + color + ", lore=" + lore + "]";
	}
}
