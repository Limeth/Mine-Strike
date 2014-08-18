package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ImmutableList;

import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class EquipmentCustomization
{
	private final String name, skin;
	private final Color color;
	private final ImmutableList<String> preLore;
	private final ImmutableList<String> postLore;
	
	private EquipmentCustomization(String name, String skin, Color color, ImmutableList<String> preLore, ImmutableList<String> postLore)
	{
		this.name = name;
		this.skin = skin;
		this.color = color;
		this.preLore = preLore;
		this.postLore = postLore;
	}
	
	public static EquipmentCustomization skin(String name, String skin, Color color)
	{
		return builder().name(name).skin(skin).color(color).build();
	}
	
	public static EquipmentCustomization skin(String name, String skin)
	{
		return skin(name, skin, null);
	}
	
	public static class EquipmentCustomizationBuilder
	{
		private String name, skin;
		private Color color;
		private ImmutableList.Builder<String> preLore = ImmutableList.builder();
		private ImmutableList.Builder<String> postLore = ImmutableList.builder();
		
		private EquipmentCustomizationBuilder() {}
		
		public EquipmentCustomization build()
		{
			return new EquipmentCustomization(name, skin, color, preLore.build(), postLore.build());
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
		
		public EquipmentCustomizationBuilder addPreLore(String... lines)
		{
			preLore.add(lines);
			return this;
		}
		
		public EquipmentCustomizationBuilder addPreLore(Collection<String> lines)
		{
			preLore.addAll(lines);
			return this;
		}
		
		public EquipmentCustomizationBuilder addPostLore(String... lines)
		{
			postLore.add(lines);
			return this;
		}
		
		public EquipmentCustomizationBuilder addPostLore(Collection<String> lines)
		{
			postLore.addAll(lines);
			return this;
		}
	}
	
	public static EquipmentCustomizationBuilder builder()
	{
		return new EquipmentCustomizationBuilder();
	}
	
	public void apply(Equipment equipment, ItemStack itemStack)
	{
		ItemMeta im = itemStack.getItemMeta();
		
		if(name != null)
		{
			String currentName = im.hasDisplayName() ? im.getDisplayName() : "";
			String newName = name.replaceAll("%NAME%", currentName);
			
			im.setDisplayName(newName);
		}
		
		if(postLore.size() > 0)
		{
			List<String> newLore;
			
			if(im.hasLore())
				newLore = im.getLore();
			else
				newLore = new ArrayList<String>();
			
			newLore.addAll(postLore);
			im.setLore(newLore);
		}
		
		if(preLore.size() > 0)
		{
			List<String> newLore;
			
			if(im.hasLore())
				newLore = im.getLore();
			else
				newLore = new ArrayList<String>();
			
			newLore.addAll(preLore);
			im.setLore(newLore);
		}
		
		if(color != null && im instanceof FireworkEffectMeta)
		{
			FireworkEffectMeta fem = (FireworkEffectMeta) im;
			
			fem.setEffect(FireworkEffect.builder().withColor(color).build());
		}
		
		itemStack.setItemMeta(im);
		
		if(skin != null)
		{
			LoreAttributes.TEMP.clear();
			LoreAttributes.TEMP.put("Skin", skin);
			LoreAttributes.TEMP.apply(itemStack);
		}
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

	public ImmutableList<String> getPostLore()
	{
		return postLore;
	}

	public ImmutableList<String> getPreLore()
	{
		return preLore;
	}

	@Override
	public String toString()
	{
		return "EquipmentCustomization [name=" + name + ", skin=" + skin + ", color=" + color + ", preLore=" + preLore + ", postLore=" + postLore + "]";
	}
}
