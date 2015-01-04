package cz.minestrike.me.limeth.minestrike.equipment.gson;

import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Type;

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
		EquipmentCustomization customization = customizationElement == null || customizationElement.isJsonNull() ? null : context.<EquipmentCustomization>deserialize(customizationElement, EquipmentCustomization.class);
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
