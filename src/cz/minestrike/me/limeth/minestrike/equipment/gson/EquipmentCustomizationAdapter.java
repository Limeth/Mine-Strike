package cz.minestrike.me.limeth.minestrike.equipment.gson;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization.EquipmentCustomizationBuilder;
import org.bukkit.Color;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EquipmentCustomizationAdapter implements
	JsonSerializer<EquipmentCustomization>,
	JsonDeserializer<EquipmentCustomization>
{
	public static final EquipmentCustomizationAdapter INSTANCE = new EquipmentCustomizationAdapter();
	
	private EquipmentCustomizationAdapter() {}
	
	@Override
	public EquipmentCustomization deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		EquipmentCustomizationBuilder builder = EquipmentCustomization.builder();
		JsonObject object = (JsonObject) target;
		
		if(object.has("name"))
			builder.name(object.get("name").getAsString());
		
		if(object.has("skin"))
			builder.skin(object.get("skin").getAsString());
		
		if(object.has("color"))
			if(object.getAsJsonPrimitive("color").isNumber())
				builder.color(Color.fromRGB(object.get("color").getAsInt()));
			else
				builder.color(stringToColor(object.get("color").getAsString()));
		
		if(object.has("preLore"))
		{
			JsonArray lore = object.get("preLore").getAsJsonArray();
			List<String> list = new ArrayList<>();
			
			for(JsonElement element : lore)
				list.add(element.getAsString());
			
			builder.addPreLore(list);
		}
		
		if(object.has("postLore"))
		{
			JsonArray lore = object.get("postLore").getAsJsonArray();
			List<String> list = new ArrayList<>();
			
			for(JsonElement element : lore)
				list.add(element.getAsString());
			
			builder.addPostLore(list);
		}
		
		return builder.build();
	}

	@Override
	public JsonElement serialize(EquipmentCustomization target, Type type, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		
		if(target.getName() != null)
			object.addProperty("name", target.getName());
		
		if(target.getSkin() != null)
			object.addProperty("skin", target.getSkin());
		
		if(target.getColor() != null)
			object.addProperty("color", colorToString(target.getColor()));
		
		if(target.getPreLore().size() > 0)
			object.add("preLore", context.serialize(target.getPreLore()));
		
		if(target.getPostLore().size() > 0)
			object.add("postLore", context.serialize(target.getPostLore()));
		
		return object;
	}

	private static String colorToString(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private static Color stringToColor(String string)
	{
		int value = Integer.decode(string);
		int red = (value >> 16) & 0xFF;
		int green = (value >> 8) & 0xFF;
		int blue = value & 0xFF;

		return Color.fromRGB(red, green, blue);
	}
}
