package cz.minestrike.me.limeth.minestrike;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;

/**
 * @author Limeth
 */
public class BlockProperties
{
	public static final BlockProperties DEFAULT = new BlockProperties(0, null);
	public static final String KEY_PENETRATION = "penetration";
	public static final String KEY_DURABILITY = "durability";
	private final double penetration;
	private final Double durability;

	public BlockProperties(double penetration, Double durability)
	{
		Preconditions.checkArgument(penetration >= 0 && penetration <= 1, "The penetration must be in range <0; 1>.");
		Preconditions.checkArgument(durability == null || durability >= 0, "The durability must be larger or equal to 0, or null.");

		this.penetration = penetration;
		this.durability = durability;
	}

	public static BlockProperties parse(JsonElement element) throws BlockPropertiesParseException
	{
		try
		{
			if(element.isJsonPrimitive())
				return new BlockProperties(element.getAsDouble(), null);
			else
			{
				JsonObject object = element.getAsJsonObject();
				double penetration = object.has(KEY_PENETRATION) ? object.get(KEY_PENETRATION).getAsDouble() : 0;
				Double durability = object.has(KEY_DURABILITY) ? object.get(KEY_DURABILITY).getAsDouble() : null;

				return new BlockProperties(penetration, durability);
			}
		}
		catch(UnsupportedOperationException e)
		{
			throw new BlockPropertiesParseException(e);
		}
	}

	public double getPenetration()
	{
		return penetration;
	}

	public double getDurability()
	{
		Preconditions.checkArgument(isBreakable(), "The block is not breakable.");

		return durability;
	}

	public boolean isPenetrable()
	{
		return penetration > 0;
	}

	public boolean isBreakable()
	{
		return durability != null;
	}

	public static class BlockPropertiesParseException extends Throwable
	{
		public BlockPropertiesParseException()
		{
		}

		public BlockPropertiesParseException(String message)
		{
			super(message);
		}

		public BlockPropertiesParseException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public BlockPropertiesParseException(Throwable cause)
		{
			super(cause);
		}

		public BlockPropertiesParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}
	}
}
