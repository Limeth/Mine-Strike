package cz.minestrike.me.limeth.minestrike.dbi;

import org.skife.jdbi.v2.BeanMapper;

public class MSPlayerDataMapper extends BeanMapper<MSPlayerData>
{
	public MSPlayerDataMapper()
	{
		super(MSPlayerData.class);
	}
}
