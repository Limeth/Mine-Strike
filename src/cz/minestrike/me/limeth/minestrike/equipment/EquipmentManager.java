package cz.minestrike.me.limeth.minestrike.equipment;

import cz.minestrike.me.limeth.minestrike.equipment.cases.Case;
import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.gson.CustomizedEquipmentAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.gson.EquipmentAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.gson.EquipmentCustomizationAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.gson.GunAdapter;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Helmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.equipment.simple.KevlarAndHelmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Knife;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseEquipmentProvider;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EquipmentManager
{
	private static final FilledHashMap<String, Equipment> TYPES;
	private static final Gson       GSON   = new GsonBuilder().registerTypeAdapter(Equipment.class, EquipmentAdapter.INSTANCE).registerTypeAdapter(EquipmentCustomization.class, EquipmentCustomizationAdapter.INSTANCE).registerTypeAdapter(CustomizedEquipment.class, CustomizedEquipmentAdapter.INSTANCE).registerTypeAdapter(Gun.class, GunAdapter.INSTANCE).create();
	private static final JsonParser PARSER = new JsonParser();

	static
	{
		TYPES = new FilledHashMap<>();

		addAll(TYPES, GunType.values());
		addAll(TYPES, GrenadeType.values());

		add(TYPES, Kevlar.KEVLAR);
		add(TYPES, Helmet.HELMET);
		add(TYPES, KevlarAndHelmet.KEVLAR_AND_HELMET);
		add(TYPES, Knife.KNIFE);

		//Defuse
		add(TYPES, DefuseEquipmentProvider.BOMB);
		add(TYPES, DefuseEquipmentProvider.DEFUSE_KIT_DEFAULT);
		add(TYPES, DefuseEquipmentProvider.DEFUSE_KIT_BOUGHT);

		for(Case caze : Case.values())
		{
			add(TYPES, caze);
			add(TYPES, caze.getKey());
		}
	}

	private static void addAll(Map<String, Equipment> collection, Equipment... array)
	{
		for(Equipment object : array)
			add(collection, object);
	}

	private static void add(Map<String, Equipment> collection, Equipment equipment)
	{
		collection.put(equipment.getId(), equipment);
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
