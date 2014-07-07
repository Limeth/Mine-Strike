package cz.minestrike.me.limeth.minestrike.equipment.gson;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;

public class GunAdapter implements JsonSerializer<Gun>, JsonDeserializer<Gun>
{
	public static final GunAdapter INSTANCE = new GunAdapter();
	
	private GunAdapter() {}
	
	@Override
	public Gun deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = (JsonObject) target;
		String owner = object.get("owner").getAsString();
		String typeId = object.get("typeId").getAsString();
		GunType equipmentType = (GunType) EquipmentManager.getType(typeId);
		EquipmentCustomization customization = context.deserialize(object.get("customization"), EquipmentCustomization.class);
		
		return new Gun(owner, equipmentType, customization);
	}

	@Override
	public JsonElement serialize(Gun target, Type type, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		
		object.addProperty("typeId", target.getType().getId());
		object.addProperty("owner", target.getOwnerName());
		object.add("customization", context.serialize(target.getCustomization()));
		
		return object;
	}
}
