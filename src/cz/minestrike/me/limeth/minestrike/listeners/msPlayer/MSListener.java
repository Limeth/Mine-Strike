package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class MSListener implements MSListenerRedirector
{
	private FilledHashMap<Class<? extends Event>, List<Method>> eventMethods;

	public MSListener()
	{
		findEventMethods();
		MineStrike.getMSListenerManager().registerListeners(this);
	}

	@SuppressWarnings("unchecked")
	private FilledHashMap<Class<? extends Event>, List<Method>> findEventMethods()
	{
		FilledHashMap<Class<? extends Event>, List<Method>> methodMap = new FilledHashMap<>();
		Class<?> currentClass = getClass();

		while(currentClass != MSListener.class)
		{
			for(Method method : currentClass.getDeclaredMethods())
			{
				if(!method.isAnnotationPresent(EventHandler.class))
					continue;

				if(!method.isAccessible())
					try
					{
						method.setAccessible(true);
					}
					catch(Exception e)
					{
						continue;
					}

				Class<?>[] parameterTypes = method.getParameterTypes();

				if(parameterTypes.length != 2)
					continue;

				if(!Event.class.isAssignableFrom(parameterTypes[0]) || !MSPlayer.class.isAssignableFrom(parameterTypes[1]))
					continue;

				Class<? extends Event> eventClass = (Class<? extends Event>) parameterTypes[0];
				List<Method> methods = methodMap.get(eventClass);

				if(methods == null)
				{
					methods = Lists.newArrayList();

					methodMap.put(eventClass, methods);
				}

				methods.add(method);
			}

			currentClass = currentClass.getSuperclass();
		}

		return this.eventMethods = methodMap;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		Class<? extends Event> eventClass = event.getClass();
		List<Method> methods = eventMethods.get(eventClass);
		
		if(methods == null || methods.size() <= 0)
			return;

		for(Method method : methods)
		{
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
	}
	
	public FilledHashMap<Class<? extends Event>, List<Method>> getEventMethods()
	{
		return eventMethods;
	}
}
