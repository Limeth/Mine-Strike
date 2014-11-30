package cz.minestrike.me.limeth.minestrike.events;

import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

public class EquipmentPickupEvent extends MSPlayerEvent implements GameEvent, Cancellable
{
	private final Game game;
	private final Equipment equipment;
	private final Item item;
	private boolean cancelled;
	
	public EquipmentPickupEvent(MSPlayer who, Game game, Equipment equipment, Item item)
	{
		super(who);
		
		this.game = game;
		this.equipment = equipment;
		this.item = item;
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
	
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	@Override
	public Game getGame()
	{
		return game;
	}
}
