package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager.EquipmentDeserializer;

public class Equipment<T extends EquipmentType> implements ConfigurationSerializable
{
	private T type;
	private final EquipmentCustomization customization;
	
	public Equipment(T type, EquipmentCustomization customization)
	{
		this.type = type;
		this.customization = customization;
	}
	
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = type.newItemStack(msPlayer);
		
		customization.apply(type, is);
		
		return is;
	}

	public T getType()
	{
		return type;
	}

	public EquipmentCustomization getCustomization()
	{
		return customization;
	}
	
	@SuppressWarnings("unchecked")
	public static final EquipmentDeserializer DESERIALIZER = (EquipmentType type, Map<String, Object> map) ->
	{
		EquipmentCustomization customization = EquipmentCustomization.deserialize((Map<String, Object>) map.get("customization"));
		
		return new Equipment<EquipmentType>(type, customization);
	};

	@Override
	public Map<String, Object> serialize()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("typeId", type.getId());
		map.put("customization", customization);
		
		return map;
	}
}
