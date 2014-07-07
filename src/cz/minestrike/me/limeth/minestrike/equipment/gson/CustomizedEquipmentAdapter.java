package cz.minestrike.me.limeth.minestrike.equipment.gson;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;

public class CustomizedEquipmentAdapter implements
	JsonSerializer<CustomizedEquipment<? extends Equipment>>,
	JsonDeserializer<CustomizedEquipment<? extends Equipment>>
{
	public static final CustomizedEquipmentAdapter INSTANCE = new CustomizedEquipmentAdapter();
	
	private CustomizedEquipmentAdapter() {}
	
	@Override
	public CustomizedEquipment<? extends Equipment> deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = (JsonObject) target;
		String typeId = object.get("id").getAsString();
		Equipment equipmentType = EquipmentManager.getEquipment(typeId);
		JsonElement customizationElement = object.get("customization");
		EquipmentCustomization customization = customizationElement == null || customizationElement.isJsonNull() ? null : context.deserialize(customizationElement, EquipmentCustomization.class);
		boolean equipped = object.get("equipped").getAsBoolean();
		
		return new CustomizedEquipment<Equipment>(equipmentType, customization, equipped);
	}

	@Override
	public JsonElement serialize(CustomizedEquipment<? extends Equipment> target, Type type, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		
		object.addProperty("id", target.getSource().getId());
		object.add("customization", context.serialize(target.getCustomization()));
		object.addProperty("equipped", target.isEquipped());
		
		return object;
	}
}
