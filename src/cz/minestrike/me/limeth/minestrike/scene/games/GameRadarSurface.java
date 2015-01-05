package cz.minestrike.me.limeth.minestrike.scene.games;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.projectsurvive.me.limeth.psmaps.surfaces.MapSurface;
import cz.projectsurvive.me.limeth.psmaps.surfaces.RegionMapSurface;

public class GameRadarSurface implements MapSurface
{
	private Structure<Scheme> structure;
	private RegionMapSurface regionSurface = new RegionMapSurface(MSConfig.getWorld());
	
	@Override
	public byte[][] getSurface()
	{
		return regionSurface == null ? MapSurface.emptySurface() : regionSurface.getSurface();
	}

	@Override
	public GameRadarSurface render()
	{
		if(regionSurface != null)
			regionSurface.render();
		
		return this;
	}
	
	public void setStructure(Structure<Scheme> structure)
	{
		if(structure != null)
		{
			Scheme scheme = structure.getScheme();
			Region region = scheme.getRegion();
			Point base = scheme.getBase();
			Point lower = region.getLower().clone().subtract(base);
			Point higher = region.getHigher().clone().subtract(base);
			
			regionSurface.setRegion(lower.getX(), lower.getY(), lower.getZ(), higher.getX(), higher.getY(), higher.getZ());
		}
		
		this.structure = structure;
	}
	
	public Structure<Scheme> getStructure()
	{
		return structure;
	}
}
