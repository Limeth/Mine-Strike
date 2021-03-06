package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.areas.schemes.TeamGameMap;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeType;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;


public class DefuseGameMap extends TeamGameMap
{
	@Expose private RegionList bombSites;
	
	public DefuseGameMap(String id, Region region, String name, RegionList TSpawn, RegionList CTSpawn, RegionList shoppingZones, RegionList bombSites, RegionList spectatorZones, Point spectatorSpawn)
	{
		super(SchemeType.MAP_DEFUSE, id, region, name, TSpawn, CTSpawn, shoppingZones, spectatorZones, spectatorSpawn);
		
		Validate.notNull(bombSites, "The bombsite list cannot be null!");
		
		this.bombSites = bombSites;
	}
	
	public DefuseGameMap(String id, Region region)
	{
		this(id, region, null, new RegionList(), new RegionList(), new RegionList(), new RegionList(), new RegionList(), region.getMidpoint());
	}

	@Override
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		FilledHashMap<String, RegionList> map = super.getRegionsLists();
		
		map.put("bombsites", bombSites);
		
		return map;
	}

	public RegionList getBombSites()
	{
		return bombSites;
	}

	public void setBombSites(RegionList bombSites)
	{
		Validate.notNull(bombSites, "The bombsite list cannot be null!");

		this.bombSites = bombSites;
	}

	@Override
	public boolean isSetUp()
	{
		return super.isSetUp() && !bombSites.isEmpty();
	}
}
