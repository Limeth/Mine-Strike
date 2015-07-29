package cz.minestrike.me.limeth.minestrike.areas.schemes;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.commands.SchemeCommandHandler;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import java.util.ArrayList;

//TODO Better name, please!
public abstract class TeamGameMap extends GameMap
{
	@Expose private RegionList tSpawn, ctSpawn, shoppingZones;

	public TeamGameMap(SchemeType type, String id, Region region, String name, RegionList tSpawn, RegionList ctSpawn, RegionList shoppingZones, RegionList spectatorZones, Point spectatorSpawn)
	{
		super(type, id, region, name, spectatorZones, spectatorSpawn);
		
		Validate.notNull(tSpawn, "The TSpawn set cannot be null!");
		Validate.notNull(ctSpawn, "The CTSpawn set cannot be null!");
		Validate.notNull(shoppingZones, "The shopping zones set cannot be null!");

		this.tSpawn = tSpawn;
		this.ctSpawn = ctSpawn;
		this.shoppingZones = shoppingZones;
	}
	
	public RegionList getSpawn(Team team)
	{
		switch(team)
		{
		case TERRORISTS: return getTSpawn();
		case COUNTER_TERRORISTS: return getCTSpawn();
		default: return null;
		}
	}
	
	@Override
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		FilledHashMap<String, RegionList> regionList = super.getRegionsLists();
		
		regionList.put("TSpawn", getTSpawn());
		regionList.put("CTSpawn", getCTSpawn());
		regionList.put("shoppingZones", getShoppingZones());
		
		return regionList;
	}
	
	@Override
	public boolean isSetUp()
	{
		return super.isSetUp() && !tSpawn.isEmpty() && !ctSpawn.isEmpty();
	}

	public RegionList getTSpawn()
	{
		return tSpawn != null ? tSpawn : (tSpawn = new RegionList());
	}
	
	public void setTSpawn(RegionList tSpawn)
	{
		Validate.notNull(tSpawn, "The TSpawn set cannot be null!");

		this.tSpawn = tSpawn;
	}

	public RegionList getCTSpawn()
	{
		return ctSpawn != null ? ctSpawn : (ctSpawn = new RegionList());
	}

	public void setCTSpawn(RegionList ctSpawn)
	{
		Validate.notNull(ctSpawn, "The CTSpawn set cannot be null!");

		this.ctSpawn = ctSpawn;
	}

	public RegionList getShoppingZones()
	{
		return shoppingZones != null ? shoppingZones : (shoppingZones = new RegionList());
	}

	public void setShoppingZones(RegionList shoppingZones)
	{
		Validate.notNull(shoppingZones, "The shopping zones set cannot be null!");

		this.shoppingZones = shoppingZones;
	}
}
