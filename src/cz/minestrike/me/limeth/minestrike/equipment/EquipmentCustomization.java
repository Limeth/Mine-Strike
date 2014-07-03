package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.util.LoreAttribute;
import cz.minestrike.me.limeth.minestrike.util.LoreAttributes;

public class EquipmentCustomization
{ 
	private final Color color;
	private final String skin;
	
	public EquipmentCustomization(Color color, String skin)
	{
		this.color = color;
		this.skin = skin;
	}
	
	public static EquipmentCustomization parse(ItemStack is)
	{
		LoreAttributes attr = LoreAttributes.extract(is);
		LoreAttribute skinAttr = attr.get("skin");
		String skin = skinAttr == null ? null : skinAttr.getValue(String.class);
		Color color = null;
		
		if(is.hasItemMeta())
		{
			ItemMeta im = is.getItemMeta();
			
			if(im instanceof FireworkEffectMeta)
			{
				FireworkEffectMeta fem = (FireworkEffectMeta) im;
				
				if(fem.hasEffect())
				{
					FireworkEffect effect = fem.getEffect();
					List<Color> colors = effect.getColors();
					
					for(Color curColor : colors)
						if(color == null)
							color = curColor;
						else
							color = color.mixColors(curColor);
				}
			}
		}
		
		return new EquipmentCustomization(color, skin);
	}
	
	public void apply(ItemStack is)
	{
		if(skin != null)
		{
			LoreAttributes.TEMP_ATTRIBUTES.clear();
			LoreAttributes.TEMP_ATTRIBUTES.put("skin", skin);
			LoreAttributes.TEMP_ATTRIBUTES.apply(is);
		}
		
		if(color != null && is instanceof FireworkEffectMeta)
		{
			FireworkEffectMeta fem = (FireworkEffectMeta) is.getItemMeta();
			
			fem.setEffect(FireworkEffect.builder().withColor(color).build());
		}
	}
	
	public boolean hasColor()
	{
		return color != null;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public boolean hasSkin()
	{
		return skin != null;
	}
	
	public String getSkin()
	{
		return skin;
	}
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static class Builder
	{
		private Color color;
		private String skin;
		
		private Builder() {}
		
		public Builder withColor(Color color)
		{
			this.color = color;
			return this;
		}
		
		public Builder withSkin(String skin)
		{
			this.skin = skin;
			return this;
		}
		
		public EquipmentCustomization build()
		{
			return new EquipmentCustomization(color, skin);
		}
	}
}
