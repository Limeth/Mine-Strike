package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class SimpleEquipment implements Equipment
{
	private final String id;
	private final ItemStack item;
	private final EquipmentCategory category;
	private final boolean tradable;
	private final Integer price;
	private final float speed;
	private final String soundDraw;
	private final boolean droppableManually;
	private final boolean droppedOnDeath;
	
	public SimpleEquipment(String id, ItemStack item, EquipmentCategory category, boolean tradable, Integer price, float speed, String soundDraw, boolean droppableManually, boolean droppedOnDeath)
	{
		this.id = id;
		this.item = item;
		this.category = category;
		this.tradable = tradable;
		this.price = price;
		this.speed = speed;
		this.soundDraw = soundDraw;
		this.droppableManually = droppableManually;
		this.droppedOnDeath = droppedOnDeath;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		return this.item.clone();
	}
	
	@Override
	public String getDefaultSkin(MSPlayer msPlayer)
	{
		return "DEFAULT";
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
	
/*	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ItemStack)
		{
			return obj.equals(item);
		}
		
		return super.equals(obj);
	}*/

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		SimpleEquipment that = (SimpleEquipment) o;

		return id.equals(that.id);

	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
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
	public boolean isDroppableManually()
	{
		return droppableManually;
	}
	
	@Override
	public boolean isDroppedOnDeath()
	{
		return droppedOnDeath;
	}
	
	@Override
	public void onSelect(MSPlayer msPlayer)
	{
	}
	
	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		return true;
	}
	
	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		return new FilledArrayList<ItemButton>();
	}
	
	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}
	
	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}

	@Override
	public EquipmentCategory getCategory()
	{
		return category;
	}

	@Override
	public boolean isTradable()
	{
		return tradable;
	}
}
