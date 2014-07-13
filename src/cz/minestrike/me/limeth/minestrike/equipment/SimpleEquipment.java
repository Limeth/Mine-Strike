package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class SimpleEquipment implements Equipment
{
	private final String id;
	private final ItemStack item;
	private final Integer price;
	private final float speed;
	private final String soundDraw;
	
	public SimpleEquipment(String id, ItemStack item, Integer price, float speed, String soundDraw)
	{
		this.id = id;
		this.item = item;
		this.price = price;
		this.speed = speed;
		this.soundDraw = soundDraw;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return this.item.clone();
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return price;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return speed;
	}
	
	@Override
	public String getSoundDraw()
	{
		return soundDraw;
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
	
	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Equipment.class;
	}
	
	@Override
	public Equipment getSource()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return getId();
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		return true;
	}
}
