package cz.minestrike.me.limeth.minestrike.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;

public class ShopOpenEvent extends MSPlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private EquipmentCategory category;
	
	public ShopOpenEvent(MSPlayer who, EquipmentCategory category)
	{
		super(who);
		
		Validate.notNull(category, "The category must not be null!");
		
		this.category = category;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	public EquipmentCategory getCategory()
	{
		return category;
	}

	public void setCategory(EquipmentCategory category)
	{
		Validate.notNull(category, "The category must not be null!");
		
		this.category = category;
	}
}
