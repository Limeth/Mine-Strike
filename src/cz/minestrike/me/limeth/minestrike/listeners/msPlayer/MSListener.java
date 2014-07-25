package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public abstract class MSListener implements MSListenerRedirector
{
	private FilledHashMap<Class<? extends Event>, Method> eventMethods;
	
	public MSListener()
	{
		findEventMethods();
		MineStrike.getMSListenerManager().registerListeners(this);
	}
	
	@SuppressWarnings("unchecked")
	private FilledHashMap<Class<? extends Event>, Method> findEventMethods()
	{
		FilledHashMap<Class<? extends Event>, Method> methods = new FilledHashMap<Class<? extends Event>, Method>();
		
		for(Method method : getClass().getDeclaredMethods())
		{
			if(!method.isAnnotationPresent(EventHandler.class))
				continue;
			
			if(!method.isAccessible())
				try
				{
					method.setAccessible(true);
				}
				catch(Exception e) { continue; }
			
			Class<?>[] parameterTypes = method.getParameterTypes();
			
			if(parameterTypes.length != 2)
				continue;
			
			if(!Event.class.isAssignableFrom(parameterTypes[0])
					|| !MSPlayer.class.isAssignableFrom(parameterTypes[1]))
				continue;
			
			methods.put((Class<? extends Event>) parameterTypes[0], method);
		}
		
		return this.eventMethods = methods;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		Class<? extends Event> eventClass = event.getClass();
		Method method = eventMethods.get(eventClass);
		
		if(method == null)
			return;
		
		EventHandler handler = method.getAnnotation(EventHandler.class);
		
		if(handler == null)
			return;
		
		if(event instanceof Cancellable)
		{
			boolean cancelled = ((Cancellable) event).isCancelled();
			
			if(cancelled && handler.ignoreCancelled())
				return;
		}
		
		try
		{
			method.invoke(this, event, msPlayer);
		}
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			MineStrike.warn("Error when invoking an event method: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public FilledHashMap<Class<? extends Event>, Method> getEventMethods()
	{
		return eventMethods;
	}
}
