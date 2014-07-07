package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.gson.EquipmentAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.gson.EquipmentCustomizationAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.gson.GunAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.games.team.defuse.DefuseEquipmentManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;

public class EquipmentManager
{
	private static final FilledHashSet<EquipmentType> TYPE_DESERIALIZERS;
	
	static
	{
		TYPE_DESERIALIZERS = new FilledHashSet<EquipmentType>();
		
		addAll(TYPE_DESERIALIZERS, GunType.values());
		addAll(TYPE_DESERIALIZERS, GrenadeType.values());
		
		TYPE_DESERIALIZERS.add(Knife.KNIFE);
		
		//Defuse
		TYPE_DESERIALIZERS.add(DefuseEquipmentManager.BOMB);
		TYPE_DESERIALIZERS.add(DefuseEquipmentManager.DEFUSE_KIT_DEFAULT);
		TYPE_DESERIALIZERS.add(DefuseEquipmentManager.DEFUSE_KIT_BOUGHT);
	}
	
	private static void addAll(Collection<EquipmentType> collection, EquipmentType... array)
	{
		for(EquipmentType object : array)
			collection.add(object);
	}
	
	public static EquipmentType getType(String id)
	{
		Validate.notNull(id, "The ID cannot be null!");
		
		for(EquipmentType type : TYPE_DESERIALIZERS)
			if(id.equals(type.getId()))
				return type;
		
		return null;
	}
	
	public static String toGson(Equipment<EquipmentType>[] array)
	{
		GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(EquipmentCustomization.class, EquipmentCustomizationAdapter.INSTANCE)
			.registerTypeAdapter(Equipment.class, EquipmentAdapter.INSTANCE)
			.registerTypeAdapter(Gun.class, GunAdapter.INSTANCE);
		Gson gson = builder.create();
		
		return gson.toJson(array);
	}
	
	@SuppressWarnings("unchecked")
	public static Equipment<EquipmentType>[] fromGson(String string)
	{
		JsonParser parser = new JsonParser();
		GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(EquipmentCustomization.class, EquipmentCustomizationAdapter.INSTANCE)
			.registerTypeAdapter(Equipment.class, EquipmentAdapter.INSTANCE)
			.registerTypeAdapter(Gun.class, GunAdapter.INSTANCE);
		Gson gson = builder.create();
		JsonElement rootElement = parser.parse(string);
		
		if(!(rootElement instanceof JsonArray))
			return new Equipment[0];
		
		JsonArray rootArray = rootElement.getAsJsonArray();
		int size = rootArray.size();
		Equipment<EquipmentType>[] result = new Equipment[size];
		int i = 0;
		
		for(JsonElement element : rootArray)
		{
			String typeId = element.getAsJsonObject().get("typeId").getAsString();
			EquipmentType type = getType(typeId);
			@SuppressWarnings("rawtypes")
			Class<? extends Equipment> clazz = type.getEquipmentClass();
			
			result[i] = gson.fromJson(element, clazz);
			
			i++;
		}
		
		return result;
	}
	
	public static interface EquipmentDeserializer
	{
		public Equipment<? extends EquipmentType> deserialize(EquipmentType type, Map<String, Object> map);
	}
}
