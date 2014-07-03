package cz.minestrike.me.limeth.minestrike.games;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.games.team.defuse.DefuseEquipmentManager;
import cz.minestrike.me.limeth.minestrike.games.team.defuse.DefuseGame;

public enum GameType
{
	DEFUSE(DefuseGame.class, DefuseEquipmentManager.class);
	
	private final Class<? extends Game<?, ?, ?, ?>> clazz;
	private final Class<? extends EquipmentManager> equipmentManagerClazz;
	
	private GameType(Class<? extends Game<?, ?, ?, ?>> clazz, Class<? extends EquipmentManager> equipmentManagerClazz)
	{
		this.clazz = clazz;
		this.equipmentManagerClazz = equipmentManagerClazz;
	}
	
	public Game<?, ?, ?, ?> construct(String id, String name) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Constructor<? extends Game<?, ?, ?, ?>> constructor = clazz.getDeclaredConstructor(String.class, String.class);
		
		return constructor.newInstance(id, name);
	}

	public Class<? extends Game<?, ?, ?, ?>> getCorrespondingClass()
	{
		return clazz;
	}

	public Class<? extends EquipmentManager> getEquipmentManagerClass()
	{
		return equipmentManagerClazz;
	}
	
	public EquipmentManager newEquipmentManager(Game<?, ?, ?, ?> game) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Constructor<? extends EquipmentManager> constructor = equipmentManagerClazz.getDeclaredConstructor(game.getClass());
		
		return constructor.newInstance(game);
	}
}
