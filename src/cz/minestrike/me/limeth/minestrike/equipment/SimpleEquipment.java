package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class SimpleEquipment implements Equipment
{
	private final ItemStack item;
	private final int price;
	private final float speed;
	
	public SimpleEquipment(ItemStack item, int price, float speed)
	{
		this.item = item;
		this.price = price;
		this.speed = speed;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer, EquipmentCustomization customization)
	{
		ItemStack item = this.item.clone();
		
		if(customization != null)
			customization.apply(item);
		
		return item;
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
}
