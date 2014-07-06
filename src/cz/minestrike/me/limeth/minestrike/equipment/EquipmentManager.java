package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;

import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
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
	
	public static Equipment<? extends EquipmentType> deserialize(Map<String, Object> map)
	{
		String typeId = (String) map.get("typeId");
		EquipmentType type = EquipmentManager.getType(typeId);
		EquipmentDeserializer deserializer = type.getDeserializer();
		
		return deserializer.deserialize(type, map);
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
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		return gson.toJson(array);
	}
	
	public static Equipment<EquipmentType>[] fromGson(String string)
	{
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		JsonElement tree = gson.toJsonTree(string);
		
		
	}
	
	public static interface EquipmentDeserializer
	{
		public Equipment<? extends EquipmentType> deserialize(EquipmentType type, Map<String, Object> map);
	}
}
