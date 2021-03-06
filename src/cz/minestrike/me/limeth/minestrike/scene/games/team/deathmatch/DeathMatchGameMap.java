package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.SchemeType;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.libs.com.google.gson.annotations.Expose;


public class DeathMatchGameMap extends GameMap
{
	@Expose private RegionList spawnZones;

	public DeathMatchGameMap(String id, Region region, String name, RegionList spawnZones, RegionList spectatorZones, Point spectatorSpawn)
	{
		super(SchemeType.MAP_DEATHMATCH, id, region, name, spectatorZones, spectatorSpawn);

		Validate.notNull(spawnZones, "The spawnZones list cannot be null!");

		this.spawnZones = spawnZones;
	}

	public DeathMatchGameMap(String id, Region region)
	{
		this(id, region, null, new RegionList(), new RegionList(), region.getMidpoint());
	}
	
	@Override
	public FilledHashMap<String, RegionList> getRegionsLists()
	{
		FilledHashMap<String, RegionList> map = super.getRegionsLists();
		
		map.put("spawnZones", spawnZones);
		
		return map;
	}

	public RegionList getSpawnZones()
	{
		return spawnZones;
	}

	public void setSpawnZones(RegionList spawnZones)
	{
        Validate.notNull(spawnZones, "The spawnZones list cannot be null!");
        
		this.spawnZones = spawnZones;
	}

	@Override
	public boolean isSetUp()
	{
		return super.isSetUp() && !spawnZones.isEmpty();
	}
}
