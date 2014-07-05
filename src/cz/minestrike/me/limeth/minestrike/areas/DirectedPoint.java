package cz.minestrike.me.limeth.minestrike.areas;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

public class DirectedPoint extends Point
{
	@Expose private float yaw, pitch;
	
	public DirectedPoint(int x, int y, int z, float yaw, float pitch)
	{
		super(x, y, z);
	}
	
	@Override
	public Location getLocation(World world)
	{
		Location loc = super.getLocation(world);
		
		loc.setYaw(yaw);
		loc.setPitch(pitch);
		
		return loc;
	}

	public float getYaw()
	{
		return yaw;
	}

	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
}
