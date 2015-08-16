package cz.minestrike.me.limeth.minestrike.events;

import org.bukkit.event.Cancellable;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;

//TODO Change to SceneEvent<Scene>
public class EquipmentDropEvent extends MSPlayerEvent implements SceneEvent<Game>, Cancellable
{
	private final Game game;
	private final Equipment equipment;
	private boolean cancelled;
	
	public EquipmentDropEvent(MSPlayer who, Game game, Equipment equipment)
	{
		super(who);
		
		this.game = game;
		this.equipment = equipment;
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
	
	@Override
	public Game getScene()
	{
		return game;
	}
}
