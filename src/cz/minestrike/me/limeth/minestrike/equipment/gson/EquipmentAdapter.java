package cz.minestrike.me.limeth.minestrike.equipment.gson;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Type;

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
