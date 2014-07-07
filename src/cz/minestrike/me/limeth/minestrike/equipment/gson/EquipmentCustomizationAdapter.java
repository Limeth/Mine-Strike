package cz.minestrike.me.limeth.minestrike.equipment.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization.EquipmentCustomizationBuilder;

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
			builder.color(Color.fromRGB(object.get("color").getAsInt()));
		
		if(object.has("lore"))
		{
			JsonArray lore = object.get("lore").getAsJsonArray();
			List<String> list = new ArrayList<String>();
			
			for(JsonElement element : lore)
				list.add(element.getAsString());
			
			builder.addLore(list);
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
			object.addProperty("color", target.getColor().asRGB());
		
		if(target.getLore().size() > 0)
			object.add("lore", context.serialize(target.getLore()));
		
		return object;
	}
}
