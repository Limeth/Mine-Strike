package cz.minestrike.me.limeth.minestrike.areas;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.MSStructureListener;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;

public class Structure<T extends Scheme> implements MSListenerRedirector
{
	private Plot<T> plot;
	private T scheme;
	private MSStructureListener<T> msListener;
	
	public Structure(Plot<T> plot, T scheme)
	{
		Validate.notNull(plot, "The plot cannot be null!");
		Validate.notNull(scheme, "The scheme cannot be null!");
		
		this.plot = plot;
		this.scheme = scheme;
		msListener = scheme.newStructureListener(this);
	}

	public Plot<T> getPlot()
	{
		return plot;
	}

	public void setPlot(Plot<T> plot)
	{
		Validate.notNull(plot, "The plot cannot be null!");
		
		this.plot = plot;
	}
	
	public Point getBase()
	{
		return Point.valueOf(plot.getLocation());
	}
	
	public Location getAbsoluteLocation(Location relative)
	{
		return plot.getAbsoluteLocation(relative);
	}
	
	public Point getAbsolutePoint(Point relative)
	{
		return plot.getAbsolutePoint(relative);
	}
	
	public Location getRelativeLocation(Location absolute)
	{
		return plot.getRelativeLocation(absolute);
	}
	
	public Point getRelativePoint(Point absolute)
	{
		return plot.getRelativePoint(absolute);
	}

	public T getScheme()
	{
		return scheme;
	}

	public void setScheme(T scheme)
	{
		Validate.notNull(scheme, "The scheme cannot be null!");
		
		this.scheme = scheme;
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		if(msListener != null)
			msListener.redirect(event, msPlayer);
	}
}
