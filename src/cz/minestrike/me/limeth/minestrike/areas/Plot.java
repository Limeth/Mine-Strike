package cz.minestrike.me.limeth.minestrike.areas;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeManager;

public class Plot<T extends Scheme>
{
	public static final int PLOT_SIZE = 256;
	public static final int PLOT_Y = 64;
	
	@Expose private final int id;
	private Location lazyLoc;
	@Expose String schemeId;
	private T scheme;
	private Structure<T> structure;
	
	public Plot(int id, T scheme)
	{
		Validate.notNull(scheme, "The scheme cannot be null!");
		
		this.id = id;
		this.scheme = scheme;
		this.schemeId = scheme.getId();
	}
	
	public void setup()
	{
		setScheme(schemeId);
	}
	
	public void build()
	{
		scheme.build(getLocation());
	}
	
	private Location initLocation()
	{
		return lazyLoc = new Location(MSConfig.getWorld(), PLOT_SIZE * id, PLOT_Y, 0);
	}

	public Location getLocation()
	{
		return lazyLoc != null ? lazyLoc : initLocation();
	}
	
	public Location getAbsoluteLocation(Location relative)
	{
		return relative == null ? null : getLocation().clone().add(relative);
	}
	
	public Point getAbsolutePoint(Point relative)
	{
		return relative == null ? null : Point.valueOf(getLocation()).add(relative);
	}
	
	public Location getRelativeLocation(Location absolute)
	{
		return absolute == null ? null : absolute.clone().subtract(getLocation());
	}
	
	public Point getRelativePoint(Point absolute)
	{
		return absolute == null ? null : absolute.clone().subtract(Point.valueOf(getLocation()));
	}
	
	public Scheme getScheme()
	{
		return scheme;
	}
	
	@SuppressWarnings("unchecked")
	private void setScheme(String schemeId)
	{
		Scheme scheme = SchemeManager.getScheme(schemeId);
		
		Validate.notNull(scheme, "Scheme '" + schemeId + "' not found");
		
		setScheme((T) scheme);
	}

	private void setScheme(T scheme)
	{
		Validate.notNull(scheme, "The scheme cannot be null!");
		
		this.scheme = scheme;
		this.schemeId = scheme.getId();
	}
	
	public int getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return "Plot [id=" + id + ", loc=" + getLocation() + ", scheme=" + scheme
				+ ", structure=" + structure + "]";
	}
	
	public boolean hasStructure()
	{
		return structure != null;
	}

	public Structure<T> getStructure()
	{
		return structure;
	}

	public void setStructure(Structure<T> structure)
	{
		this.structure = structure;
	}
}
