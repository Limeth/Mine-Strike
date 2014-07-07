package cz.minestrike.me.limeth.minestrike.equipment.gson;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;

public class EquipmentAdapter implements JsonSerializer<Equipment>, JsonDeserializer<Equipment>
{
	public static final EquipmentAdapter INSTANCE = new EquipmentAdapter();
	
	private EquipmentAdapter() {}
	
	@Override
	public Equipment deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		return EquipmentManager.getEquipment(target.getAsString());
	}

	@Override
	public JsonElement serialize(Equipment target, Type type, JsonSerializationContext context)
	{
		return context.serialize(target.getId());
	}
}
