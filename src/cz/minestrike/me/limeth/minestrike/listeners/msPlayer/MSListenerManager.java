package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class MSListenerManager implements Listener
{
	public final HashSet<Class<? extends Event>> EVENTS = new HashSet<Class<? extends Event>>();
	
	public void registerListeners(MSListener listener)
	{
		FilledHashMap<Class<? extends Event>, Method> eventMethods = listener.getEventMethods();
		PluginManager pm = Bukkit.getPluginManager();
		
		for(Entry<Class<? extends Event>, Method> entry : eventMethods.entrySet())
		{
			Class<? extends Event> eventClass = entry.getKey();
			EventExecutor executor = getEventExecutor(eventClass);
			
			if(executor == null)
				continue;
			
			boolean added = EVENTS.add(eventClass);
			
			if(added)
				pm.registerEvent(eventClass, this, EventPriority.NORMAL, executor, MineStrike.getInstance(), false);
		}
	}
	
	private EventExecutor getEventExecutor(Class<? extends Event> eventClass)
	{
		if(PlayerEvent.class.isAssignableFrom(eventClass))
		{
			return playerEventExecutor;
		}
		else if(EntityEvent.class.isAssignableFrom(eventClass))
		{
			return entityEventExecutor;
		}
		else if(InventoryEvent.class.isAssignableFrom(eventClass))
		{
			return inventoryEventExecutor;
		}
		else
			return null;
	}
	
	private final EventExecutor playerEventExecutor = new MSPlayerEventExecutor<PlayerEvent>() {
		
		@Override
		protected Player getPlayer(PlayerEvent event)
		{
			PlayerEvent playerEvent = (PlayerEvent) event;
			
			return playerEvent.getPlayer();
		}
		
	};
	
	private final EventExecutor entityEventExecutor = new MSPlayerEventExecutor<EntityEvent>() {
		
		@Override
		protected Player getPlayer(EntityEvent event)
		{
			EntityEvent entityEvent = (EntityEvent) event;
			Entity entity = entityEvent.getEntity();
			
			if(!(entity instanceof Player))
				return null;
			
			return (Player) entity;
		}
		
	};
	
	private final EventExecutor inventoryEventExecutor = new MSPlayerEventExecutor<InventoryEvent>() {
		
		@Override
		protected Player getPlayer(InventoryEvent event)
		{
			InventoryEvent invEvent = (InventoryEvent) event;
			InventoryView view = invEvent.getView();
			HumanEntity human = view.getPlayer();
			
			return (Player) human;
		}
		
	};
	
	public static abstract class MSPlayerEventExecutor<T extends Event> implements EventExecutor
	{
		protected abstract Player getPlayer(T event);
		
		@Override
		public void execute(Listener listener, Event event) throws EventException
		{
			@SuppressWarnings("unchecked")
			Player player = getPlayer((T) event);
			
			if(player == null)
				return;
			
			MSPlayer msPlayer = MSPlayer.get(player);
			
			if(msPlayer == null)
				return;
			
			msPlayer.redirectEvent(event);
		}
	}
}
