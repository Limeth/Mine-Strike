package cz.minestrike.me.limeth.minestrike.areas.schemes;

import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;

public class StructureMSListener<T extends Scheme> extends MSListener
{
	private final Structure<T> structure;
	
	public StructureMSListener(Structure<T> structure)
	{
		super();
		this.structure = structure;
	}
	
	public Structure<T> getStructure()
	{
		return structure;
	}
}
