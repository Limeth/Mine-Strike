package cz.minestrike.me.limeth.minestrike.areas.schemes;

import java.lang.reflect.InvocationTargetException;

import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGameMenu;
import cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch.DeathMatchGameMap;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGameMap;

public enum SchemeType
{
	LOBBY(GameLobby.class), MENU_TEAM(TeamGameMenu.class), MAP_DEFUSE(DefuseGameMap.class), MAP_DEATHMATCH(DeathMatchGameMap.class);
	
	private final Class<? extends Scheme> clazz;
	
	private SchemeType(Class<? extends Scheme> clazz)
	{
		this.clazz = clazz;
	}
	
	public Scheme newInstance(String id, Region region) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return getCorrespondingClass().getConstructor(String.class, Region.class).newInstance(id, region);
	}

	public Class<? extends Scheme> getCorrespondingClass()
	{
		return clazz;
	}
}
