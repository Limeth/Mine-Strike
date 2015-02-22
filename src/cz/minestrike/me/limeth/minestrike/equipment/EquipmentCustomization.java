package cz.minestrike.me.limeth.minestrike.equipment;

import com.google.common.collect.ImmutableList;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EquipmentCustomization
{
	private final String name, skin;
	private final boolean changeSkin;
	private final Color color;
	private final ImmutableList<String> preLore;
	private final ImmutableList<String> postLore;
	
	private EquipmentCustomization(String name, String skin, boolean changeSkin, Color color, ImmutableList<String> preLore, ImmutableList<String> postLore)
	{
		this.name = name;
		this.skin = skin;
		this.changeSkin = changeSkin;
		this.color = color;
		this.preLore = preLore;
		this.postLore = postLore;
	}
	
	public static EquipmentCustomization skin(String name, String skin, Color color)
	{
		return builder().name(Translation.EQUIPMENT_CUSTOMIZATION_NAME.getMessage("{1}", name)).skin(skin).color(color).build();
	}
	
	public static class EquipmentCustomizationBuilder
	{
		private String name, skin;
		private boolean changeSkin;
		private Color color;
		private ImmutableList.Builder<String> preLore = ImmutableList.builder();
		private ImmutableList.Builder<String> postLore = ImmutableList.builder();
		
		private EquipmentCustomizationBuilder() {}
		
		public EquipmentCustomization build()
		{
			return new EquipmentCustomization(name, skin, changeSkin, color, preLore.build(), postLore.build());
		}
		
		public EquipmentCustomizationBuilder name(String name)
		{
			this.name = name;
			return this;
		}
		
		public EquipmentCustomizationBuilder skin(String skin)
		{
			this.skin = skin;
			this.changeSkin = true;
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
	
	public void apply(Equipment equipment, ItemStack itemStack, MSPlayer msPlayer)
	{
		ItemMeta im = itemStack.getItemMeta();
		
		if(name != null)
		{
			String displayName = equipment.getDisplayName();
			String newName = Translation.replaceArguments(name, displayName);

			im.setDisplayName(newName);
		}
		
		if(postLore.size() > 0)
		{
			List<String> newLore;
			
			if(im.hasLore())
				newLore = im.getLore();
			else
				newLore = new ArrayList<>();
			
			newLore.addAll(postLore);
			im.setLore(newLore);
		}
		
		if(preLore.size() > 0)
		{
			List<String> newLore;
			
			if(im.hasLore())
				newLore = im.getLore();
			else
				newLore = new ArrayList<>();
			
			newLore.addAll(preLore);
			im.setLore(newLore);
		}
		
		if(color != null && im instanceof FireworkEffectMeta)
		{
			FireworkEffectMeta fem = (FireworkEffectMeta) im;
			
			fem.setEffect(FireworkEffect.builder().withColor(color).build());
		}
		
		itemStack.setItemMeta(im);

		if(changeSkin)
		{
			String skin = this.skin != null ? this.skin : equipment.getDefaultSkin(msPlayer);

			if(skin != null)
			{
				LoreAttributes.TEMP.clear();
				LoreAttributes.extract(itemStack, LoreAttributes.TEMP);

				if(LoreAttributes.TEMP.containsKey("Type"))
				{
					String type = LoreAttributes.TEMP.get("Type");
					String[] types = type.split(" \\| ");

					LoreAttributes.TEMP.put("Type", types[0] + " | " + skin);
				}
				else
					LoreAttributes.TEMP.put("Skin", skin);

				LoreAttributes.TEMP.apply(itemStack);
			}
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

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		EquipmentCustomization that = (EquipmentCustomization) o;

		if(changeSkin != that.changeSkin)
			return false;
		if(color != null ? !color.equals(that.color) : that.color != null)
			return false;
		if(name != null ? !name.equals(that.name) : that.name != null)
			return false;
		if(!postLore.equals(that.postLore))
			return false;
		if(!preLore.equals(that.preLore))
			return false;
		if(skin != null ? !skin.equals(that.skin) : that.skin != null)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (skin != null ? skin.hashCode() : 0);
		result = 31 * result + (changeSkin ? 1 : 0);
		result = 31 * result + (color != null ? color.hashCode() : 0);
		result = 31 * result + preLore.hashCode();
		result = 31 * result + postLore.hashCode();
		return result;
	}
}
