package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager.EquipmentDeserializer;

public class SimpleEquipmentType implements EquipmentType
{
	private final String id;
	private final ItemStack item;
	private final int price;
	private final float speed;
	private final EquipmentDeserializer deserializer;
	
	public SimpleEquipmentType(String id, ItemStack item, int price, float speed, EquipmentDeserializer deserializer)
	{
		this.id = id;
		this.item = item;
		this.price = price;
		this.speed = speed;
		this.deserializer = deserializer;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return this.item.clone();
	}

	@Override
	public int getPrice(MSPlayer msPlayer)
	{
		return price;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return speed;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ItemStack)
		{
			return obj.equals(item);
		}
		
		return super.equals(obj);
	}

	@Override
	public String getDisplayName()
	{
		if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
			return item.getType().name().toLowerCase().replace('_', ' ');
		
		return item.getItemMeta().getDisplayName();
	}
	
	public ItemStack getOriginalItemStack()
	{
		return item;
	}

	@Override
	public String getId()
	{
		return id;
	}

	public EquipmentDeserializer getDeserializer()
	{
		return deserializer;
	}
}
