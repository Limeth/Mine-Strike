package cz.minestrike.me.limeth.minestrike.equipment;

import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.gson.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.*;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseEquipmentProvider;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.util.Set;
import java.util.stream.Collectors;

public class EquipmentManager
{
	private static final FilledHashMap<String, Equipment> TYPES;
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder().registerTypeAdapter(Equipment.class, EquipmentAdapter.INSTANCE).registerTypeAdapter(EquipmentCustomization.class, EquipmentCustomizationAdapter.INSTANCE).registerTypeAdapter(CustomizedEquipment.class, CustomizedEquipmentAdapter.INSTANCE).registerTypeAdapter(Gun.class, GunAdapter.INSTANCE).registerTypeAdapter(Placeholder.class, PlaceholderAdapter.INSTANCE);
	public static final  Gson        GSON   = GSON_BUILDER.create();
	public static final  Gson        GSON_PRETTY = GSON_BUILDER.setPrettyPrinting().create();
	private static final JsonParser PARSER = new JsonParser();

	static
	{
		TYPES = new FilledHashMap<>();

		registerAll(GunType.getRegisteredTypes().values());
		registerAll(GrenadeType.values());

		register(Kevlar.KEVLAR);
		register(Helmet.HELMET);
		register(KevlarAndHelmet.KEVLAR_AND_HELMET);
		register(Knife.KNIFE);

		//Defuse TODO register dynamically
		register(DefuseEquipmentProvider.BOMB);
		register(DefuseEquipmentProvider.DEFUSE_KIT_DEFAULT);
		register(DefuseEquipmentProvider.DEFUSE_KIT_BOUGHT);
	}

	public static void registerAll(Equipment... array)
	{
		for(Equipment object : array)
			register(object);
	}

	public static void registerAll(Iterable<? extends Equipment> array)
	{
		for(Equipment object : array)
			register(object);
	}

	public static void register(Equipment equipment)
	{
		TYPES.put(equipment.getId(), equipment);
	}

	public static Equipment getEquipment(String id)
	{
		Validate.notNull(id, "The ID cannot be null!");

		return TYPES.get(id);
	}

	public static String toJsonAll(Equipment[] array)
	{
		JsonArray result = new JsonArray();

		for(Equipment equipment : array)
			result.add(toJsonElement(equipment));

		return GSON.toJson(result);
	}

	public static JsonElement toJsonElement(Equipment equipment)
	{
		Class<? extends Equipment> clazz = equipment.getEquipmentClass();

		return GSON.toJsonTree(equipment, clazz);
	}

	public static String toJson(Equipment equipment)
	{
		return GSON.toJson(toJsonElement(equipment));
	}
	
	public static Equipment[] fromJsonArray(String string)
	{
		JsonElement rootElement = PARSER.parse(string);
		
		if(!(rootElement instanceof JsonArray))
			return new CustomizedEquipment[0];
		
		JsonArray rootArray = rootElement.getAsJsonArray();
		Equipment[] result = new Equipment[rootArray.size()];
		int i = 0;
		
		for(JsonElement element : rootArray)
		{
			result[i] = fromJsonElement(element);
			i++;
		}
		
		return result;
	}
	
	public static Equipment fromJsonElement(JsonElement element)
	{
		Class<? extends Equipment> clazz;
		
		if(element instanceof JsonObject)
		{
			JsonObject object = (JsonObject) element;
			String typeId = object.get("id").getAsString();
			Equipment equipment = EquipmentManager.getEquipment(typeId);
			
			clazz = equipment.getEquipmentClass();
			
			if(clazz == Equipment.class)
				clazz = CustomizedEquipment.class;
		}
		else
			clazz = Equipment.class;
		
		return GSON.fromJson(element, clazz);
	}
	
	public static Equipment fromJson(String json)
	{
		return fromJsonElement(PARSER.parse(json));
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Equipment> getEquipment()
	{
		return TYPES.values().stream().collect(Collectors.toSet());
	}
}
