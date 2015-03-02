package cz.minestrike.me.limeth.minestrike.equipment.gson;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.lang.reflect.Type;

public class GunAdapter implements JsonSerializer<Gun>, JsonDeserializer<Gun>
{
	public static final GunAdapter INSTANCE = new GunAdapter();
	
	private GunAdapter() {}
	
	@Override
	public Gun deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = (JsonObject) target;
		String typeId = object.get("id").getAsString();
		GunType equipmentType = (GunType) EquipmentManager.getEquipment(typeId);
		EquipmentCustomization customization = context.deserialize(object.get("customization"), EquipmentCustomization.class);
		boolean equipped = object.get("equipped").getAsBoolean();
		JsonElement killsElement = object.get("kills");
		Integer kills = killsElement == null || killsElement.isJsonNull() ? null : killsElement.getAsInt();
		Gun gun = new Gun(equipmentType, customization, kills);
		
		gun.setEquipped(equipped);
		
		return gun;
	}

	@Override
	public JsonElement serialize(Gun target, Type type, JsonSerializationContext context)
	{
		JsonObject object = new JsonObject();
		
		object.addProperty("id", target.getSource().getId());
		object.add("customization", context.serialize(target.getCustomization()));
		object.addProperty("equipped", target.isEquipped());
		object.addProperty("kills", target.getKills());
		
		return object;
	}
}
