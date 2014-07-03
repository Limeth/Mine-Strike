package cz.minestrike.me.limeth.minestrike.areas.schemes;

import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;

public class MSStructureListener<T extends Scheme> extends MSListener
{
	private final Structure<T> structure;
	
	public MSStructureListener(Structure<T> structure)
	{
		super();
		this.structure = structure;
	}
	
	public Structure<T> getStructure()
	{
		return structure;
	}
}
