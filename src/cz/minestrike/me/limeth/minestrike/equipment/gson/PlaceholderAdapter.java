package cz.minestrike.me.limeth.minestrike.equipment.gson;

import cz.minestrike.me.limeth.minestrike.equipment.simple.Placeholder;
import org.bukkit.craftbukkit.libs.com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Type;

/**
 * @author Limeth
 */
public class PlaceholderAdapter implements JsonSerializer<Placeholder>, JsonDeserializer<Placeholder>
{
	public static final PlaceholderAdapter INSTANCE = new PlaceholderAdapter();

	private PlaceholderAdapter() {}

	@Override
	public Placeholder deserialize(JsonElement target, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		throw new NotImplementedException();
	}

	@Override
	public JsonElement serialize(Placeholder target, Type type, JsonSerializationContext context)
	{
		return target.getData();
	}
}
