package cz.minestrike.me.limeth.minestrike.equipment.gson;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentType;

public class EquipmentAdapter implements
	JsonSerializer<Equipment<? extends EquipmentType>>,
	JsonDeserializer<Equipment<? extends EquipmentType>>
{
	public static final EquipmentAdapter INSTANCE = new EquipmentAdapter();
	
	private EquipmentAdapter() {}
	
	@Override
	public Equipment<? extends EquipmentType> deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = (JsonObject) target;
		String typeId = object.get("typeId").getAsString();
		EquipmentType equipmentType = EquipmentManager.getType(typeId);
		EquipmentCustomization customization = context.deserialize(object.get("customization"), EquipmentCustomization.class);
		
		return new Equipment<EquipmentType>(equipmentType, customization);
	}

	@Override
	public JsonElement serialize(Equipment<? extends EquipmentType> target, Type type, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		
		object.addProperty("typeId", target.getType().getId());
		object.add("customization", context.serialize(target.getCustomization()));
		
		return object;
	}
}
