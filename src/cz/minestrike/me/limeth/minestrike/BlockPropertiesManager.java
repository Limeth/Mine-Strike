package cz.minestrike.me.limeth.minestrike;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Limeth
 */
public class BlockPropertiesManager
{
	public static final String                              GROUP_BLOCK_NAMESPACE = "namespace";
	public static final String                              GROUP_BLOCK_ID        = "id";
	public static final String                              GROUP_BLOCK_DATA      = "data";
	public static final String                              REGEX_BLOCK           = "^((?<" + GROUP_BLOCK_NAMESPACE + ">[a-zA-Z0-9_]+):)??(?<" + GROUP_BLOCK_ID + ">[a-zA-Z0-9_]+)(:(?<" + GROUP_BLOCK_DATA + ">[0-9]+))?$";
	public static final Pattern                             PATTERN_BLOCK         = Pattern.compile(REGEX_BLOCK);
	public static final File                                FILE                  = new File("plugins/MineStrike/blocks.json");
	private static final Map<Material, BlockProperties>     materialMap           = Maps.newHashMap();
	private static final Map<MaterialData, BlockProperties> materialDataMap       = Maps.newHashMap();

	public static BlockProperties getProperties(Block block)
	{
		Material type = block.getType();
		byte data = block.getData();
		MaterialData materialData = new MaterialData(type, data);
		BlockProperties properties = materialDataMap.get(materialData);

		if(properties != null)
			return properties;

		properties = materialMap.get(type);

		if(properties != null)
			return properties;

		return BlockProperties.DEFAULT;
	}

	public static void load()
	{
		materialMap.clear();
		materialDataMap.clear();

		JsonElement rawRoot = prepareFile();

		if(!rawRoot.isJsonObject())
			return;

		JsonObject root = rawRoot.getAsJsonObject();

		for(Map.Entry<String, JsonElement> entry : root.entrySet())
		{
			String target = entry.getKey();

			try
			{
				Matcher matcher = PATTERN_BLOCK.matcher(target);

				if(!matcher.matches())
					throw new IllegalStateException("Invalid material-data: " + target);

				String namespace = matcher.group(GROUP_BLOCK_NAMESPACE);
				String id = matcher.group(GROUP_BLOCK_ID);
				String rawData = matcher.group(GROUP_BLOCK_DATA);
				Byte data = rawData != null ? Byte.parseByte(rawData) : null;
				Material material = Material.matchMaterial(id);
				JsonElement element = entry.getValue();
				BlockProperties properties = BlockProperties.parse(element);

				if(data == null)
				{
					materialMap.put(material, properties);
				}
				else
				{
					MaterialData materialData = new MaterialData(material, data);

					materialDataMap.put(materialData, properties);
				}
			}
			catch(BlockProperties.BlockPropertiesParseException | IllegalArgumentException e)
			{
				new BlockProperties.BlockPropertiesParseException("Could not parse material-data: " + target, e).printStackTrace();
			}
		}
	}

	private static JsonElement prepareFile()
	{
		try
		{
			return prepareFileUnsafe();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static JsonElement prepareFileUnsafe() throws IOException
	{
		if(!FILE.isFile())
		{
			if(FILE.exists() && !FILE.delete())
				throw new IOException("Couldn't delete the blocks.json file.");

			if(!FILE.getParentFile().isDirectory() && !FILE.getParentFile().mkdirs())
				throw new IOException("Couldn't create a directory for the equipmentCustomizations.json file.");

			if(!FILE.createNewFile())
				throw new IOException("Couldn't create a new equipmentCustomizations.json file.");

			JsonObject root = new JsonObject();
			JsonObject glass = new JsonObject();
			JsonObject pot = new JsonObject();

			glass.addProperty(BlockProperties.KEY_PENETRATION, 1);
			glass.addProperty(BlockProperties.KEY_DURABILITY, 0);
			pot.addProperty(BlockProperties.KEY_DURABILITY, 0);
			root.add("thin_glass", glass);
			root.add("flower_pot", pot);
			root.addProperty("wood", 0.5);

			FileWriter writer = new FileWriter(FILE);

			EquipmentManager.GSON_PRETTY.toJson(root, writer);
			writer.close();

			return root;
		}

		FileReader reader = new FileReader(FILE);
		JsonParser parser = new JsonParser();
		JsonElement result = parser.parse(reader);

		reader.close();

		return result;
	}
}
