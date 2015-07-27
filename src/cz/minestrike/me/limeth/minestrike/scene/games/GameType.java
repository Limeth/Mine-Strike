package cz.minestrike.me.limeth.minestrike.scene.games;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch.DeathMatchGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseEquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;

public enum GameType
{
	DEFUSE(DefuseGame.class, DefuseEquipmentProvider.class),
	DEATH_MATCH(DeathMatchGame.class, DeathMatchEquipmentProvider.class);
	
	private final Class<? extends Game> clazz;
	private final Class<? extends EquipmentProvider> equipmentManagerClazz;
	
	private GameType(Class<? extends Game> clazz, Class<? extends EquipmentProvider> equipmentManagerClazz)
	{
		this.clazz = clazz;
		this.equipmentManagerClazz = equipmentManagerClazz;
	}
	
	public Game construct(String id, String name) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Constructor<? extends Game> constructor = clazz.getDeclaredConstructor(String.class, String.class);
		
		return constructor.newInstance(id, name);
	}

	public Class<? extends Game> getCorrespondingClass()
	{
		return clazz;
	}

	public Class<? extends EquipmentProvider> getEquipmentManagerClass()
	{
		return equipmentManagerClazz;
	}
	
	public EquipmentProvider newEquipmentManager(Game game) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Constructor<? extends EquipmentProvider> constructor = equipmentManagerClazz.getDeclaredConstructor(game.getClass());
		
		return constructor.newInstance(game);
	}
}
