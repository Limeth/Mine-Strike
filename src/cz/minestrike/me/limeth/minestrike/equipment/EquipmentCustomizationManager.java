package cz.minestrike.me.limeth.minestrike.equipment;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.cases.AbstractCase;
import cz.minestrike.me.limeth.minestrike.equipment.cases.Case;
import cz.minestrike.me.limeth.minestrike.equipment.cases.CaseContent;
import cz.minestrike.me.limeth.minestrike.equipment.cases.CaseContentRarity;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.FAMAS;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Limeth
 */
public class EquipmentCustomizationManager
{
	public static final  File   FILE              = new File("plugins/MineStrike/equipment_customizations.json");
	private static final String KEY_CASES         = "cases";
	private static final String KEY_CASE_NAME     = "name";
	private static final String KEY_CASE_DISABLED = "disabled";
	private static final String KEY_CASE_CONTENT  = "content";
	private static final String KEY_FREE          = "free";
	private static List<AbstractCase> cases = Lists.newArrayList();
	private static List<Equipment>    freeEquipment = Lists.newArrayList();

	public static List<AbstractCase> getCases()
	{
		return cases;
	}

	public static List<Equipment> getFreeEquipment()
	{
		return freeEquipment;
	}

	public static void loadCustomizations()
	{
		JsonElement root = prepareFile();
		
		loadCases(root);
		loadFreeEquipment(root);
	}
	
	private static void loadCases(JsonElement rawRoot)
	{
		if(!rawRoot.isJsonObject())
			return;

		JsonObject root = rawRoot.getAsJsonObject();
		JsonObject jsonCases = root.get(KEY_CASES).getAsJsonObject();

		cases.clear();

		for(Map.Entry<String, JsonElement> entry : jsonCases.entrySet())
		{
			JsonObject jsonCase = entry.getValue().getAsJsonObject();
			String caseId = entry.getKey().toUpperCase();
			boolean disabled = jsonCase.has(KEY_CASE_DISABLED)
			                   && jsonCase.get(KEY_CASE_DISABLED).isJsonPrimitive()
			                   && jsonCase.get(KEY_CASE_DISABLED).getAsJsonPrimitive().isBoolean()
			                   && jsonCase.get(KEY_CASE_DISABLED).getAsBoolean();

			if(disabled)
				continue;

			String caseName = jsonCase.get(KEY_CASE_NAME).getAsString();
			JsonObject caseContent = jsonCase.get(KEY_CASE_CONTENT).getAsJsonObject();
			List<CaseContent> contents = Lists.newArrayList();

			for(CaseContentRarity rarity : CaseContentRarity.values())
			{
				String rarityKey = rarity.name().toLowerCase();

				if(!caseContent.has(rarityKey))
					continue;

				JsonElement jsonRarity = caseContent.get(rarityKey);

				if(jsonRarity.isJsonArray())
				{
					JsonArray jsonRarityContent = jsonRarity.getAsJsonArray();

					for(int i = 0; i < jsonRarityContent.size(); i++)
					{
						JsonElement jsonEquipment = jsonRarityContent.get(i);

						try
						{
							Equipment equipment = EquipmentManager.fromJsonElement(jsonEquipment);
							CaseContent content = new CaseContent(equipment, rarity);

							contents.add(content);
						}
						catch(Exception e)
						{
							MineStrike.warn("Couldn't parse equipment customization #" + (i + 1) + " of " + rarity.name().toLowerCase() + " rarity in case " + caseId + ".");
							e.printStackTrace();
						}
					}
				}
				else
				{
					try
					{
						Equipment equipment = EquipmentManager.fromJsonElement(jsonRarity);
						CaseContent content = new CaseContent(equipment, rarity);

						contents.add(content);
					}
					catch(Exception e)
					{
						MineStrike.warn("Couldn't parse equipment customization of " + rarity.name().toLowerCase() + " rarity in case " + caseId + ".");
						e.printStackTrace();
					}
				}
			}

			Case caze = new Case(caseId.toUpperCase(), caseName, contents.toArray(new CaseContent[contents.size()]));

			cases.add(caze);
		}

		//TODO register dynamically
		EquipmentManager.registerAll(cases);
		EquipmentManager.registerAll(cases.stream().map(AbstractCase::getKey).collect(Collectors.toList()));
	}
	
	private static void loadFreeEquipment(JsonElement rawRoot)
	{
		freeEquipment.clear();

		if(!rawRoot.isJsonObject())
			return;

		JsonObject root = rawRoot.getAsJsonObject();

		if(!root.has(KEY_FREE))
			return;

		JsonElement rawFree = root.get(KEY_FREE);

		if(rawFree.isJsonArray())
		{
			JsonArray free = rawFree.getAsJsonArray();

			for(int i = 0; i < free.size(); i++)
			{
				JsonElement jsonEquipment = free.get(i);

				try
				{
					Equipment equipment = EquipmentManager.fromJsonElement(jsonEquipment);

					freeEquipment.add(equipment);
				}
				catch(Exception e)
				{
					MineStrike.warn("Couldn't parse free equipment #" + (i + 1) + ".");
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				Equipment equipment = EquipmentManager.fromJsonElement(rawFree);

				freeEquipment.add(equipment);
			}
			catch(Exception e)
			{
				MineStrike.warn("Couldn't parse free equipment.");
				e.printStackTrace();
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
				throw new IOException("Couldn't delete the equipmentCustomizations.json file.");

			if(!FILE.getParentFile().isDirectory() && !FILE.getParentFile().mkdirs())
				throw new IOException("Couldn't create a directory for the equipmentCustomizations.json file.");

			if(!FILE.createNewFile())
				throw new IOException("Couldn't create a new equipmentCustomizations.json file.");

			JsonObject root = new JsonObject();
			JsonObject cases = new JsonObject();

			JsonObject jsonExampleCase = new JsonObject();
			String caseName = "exampleCase";
			JsonElement jsonExampleEquipment = EquipmentManager.toJsonElement(new Gun(FAMAS.getInstance(), ChatColor.DARK_RED + "Example Red", "EXAMPLE", Color.RED));
			JsonArray jsonExampleLegendary = new JsonArray();
			JsonObject jsonExampleContent = new JsonObject();

			jsonExampleCase.addProperty(KEY_CASE_NAME, "&4Example");
			jsonExampleCase.addProperty(KEY_CASE_DISABLED, false);
			jsonExampleCase.add(KEY_CASE_CONTENT, jsonExampleContent);

			for(CaseContentRarity rarity : CaseContentRarity.values())
				jsonExampleContent.add(rarity.name().toLowerCase(), new JsonArray());

			jsonExampleLegendary.add(jsonExampleEquipment);
			jsonExampleContent.add(CaseContentRarity.LEGENDARY.name().toLowerCase(), jsonExampleLegendary);

			cases.add(caseName, jsonExampleCase);
			root.add(KEY_CASES, cases);
			root.add(KEY_FREE, jsonExampleLegendary);

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

	/*
	public static void toJson() throws IOException
	{
		File outFile = new File("plugins/MineStrike/equipment_customizations.json");

		//outFile.createNewFile();

		FileWriter writer = new FileWriter(outFile);
		JsonObject root = new JsonObject();
		JsonObject cases = new JsonObject();
		JsonArray free = new JsonArray();

		root.add(KEY_CASES, cases);
		root.add(KEY_FREE, free);

		for(AbstractCase caze : EquipmentCustomizationManager.cases)
		{
			JsonObject jsonCase = new JsonObject();
			JsonObject jsonContent = new JsonObject();

			for(CaseContentRarity rarity : CaseContentRarity.values())
			{
				JsonArray jsonRarityContents = new JsonArray();

				for(CaseContent content : caze.getContents(rarity))
					jsonRarityContents.add(EquipmentManager.toJsonElement(content.getEquipment()));

				jsonContent.add(rarity.name().toLowerCase(), jsonRarityContents);
			}

			jsonCase.addProperty(KEY_CASE_NAME, caze.getName().replace('\u0026', '&'));
			jsonCase.addProperty(KEY_CASE_DISABLED, false);
			jsonCase.add(KEY_CASE_CONTENT, jsonContent);

			cases.add(caze.getCaseId().toLowerCase(), jsonCase);
		}

		for(Equipment equipment : freeEquipment)
			free.add(EquipmentManager.toJsonElement(equipment));

		EquipmentManager.GSON_PRETTY.toJson(root, writer);
		writer.flush();
		writer.close();
	}
	*/

	/*
	betaContent = createContents(new CaseContent.ArrayBuilder()
                                .legendary(new CustomizedEquipment<>(Knife.KNIFE, EquipmentCustomization.skin("Flip - " + ChatColor.AQUA + "Fade", "FLIP_FADE", Color.AQUA)))

                                .unique(new Gun(G3SG1.getInstance(), ChatColor.LIGHT_PURPLE + "Subwoofer", "SUBWOOFER", Color.fromRGB(255, 0, 127))).unique(new Gun(Negev.getInstance(), ChatColor.YELLOW + "Retro", "RETRO", Color.YELLOW))

                                .rare(new Gun(FAMAS.getInstance(), ChatColor.WHITE + "Tuxedo", "TUXEDO", Color.WHITE)).rare(new Gun(USPS.getInstance(), ChatColor.AQUA + "Candy", "CANDY", Color.AQUA))

                                .valuable(new Gun(GalilAR.getInstance(), ChatColor.GOLD + "Tigris", "TIGRIS", Color.ORANGE)).valuable(new Gun(P90.getInstance(), ChatColor.AQUA + "Lightning", "LIGHTNING", Color.AQUA))

                                .common(new Gun(Bizon.getInstance(), ChatColor.GREEN + "Poison", "POISON", Color.fromRGB(0, 255, 127))).common(new Gun(TEC9.getInstance(), ChatColor.DARK_GREEN + "Reptile", "REPTILE", Color.fromRGB(0, 128, 0))).common(new Gun(SawedOff.getInstance(), ChatColor.BLUE + "Lapis", "LAPIS", Color.BLUE))

                                .build());

	alphaContent = createContents(new CaseContent.ArrayBuilder()
		                         .legendary(new CustomizedEquipment<>(Knife.KNIFE, EquipmentCustomization.skin("Gut - " + ChatColor.GOLD + "Daemon", "GUT_DAEMON", Color.ORANGE)))

		                         .unique(new Gun(XM1014.getInstance(), ChatColor.GOLD + "Golden", "GOLDEN", Color.fromRGB(204, 153, 0))).unique(new Gun(M4A4.getInstance(), ChatColor.DARK_BLUE + "Storm", "STORM", Color.BLUE))

		                         .rare(new Gun(AK47.getInstance(), ChatColor.BOLD + "Glory", "GLORY", Color.BLUE)).rare(new Gun(P2000.getInstance(), ChatColor.DARK_RED + "Dragon", "DRAGON", Color.RED))

		                         .valuable(new Gun(Glock.getInstance(), ChatColor.GRAY + "Strike" + ChatColor.BLUE + "back", "STRIKEBACK", Color.BLUE)).valuable(new Gun(Deagle.getInstance(), ChatColor.DARK_GRAY + "Dark Steel", "DARK_STEEL", Color.BLACK))

		                         .common(new Gun(AWP.getInstance(), ChatColor.DARK_GREEN + "Punch", "PUNCH", Color.GREEN)).common(new Gun(MP9.getInstance(), ChatColor.GREEN + "Acid", "ACID", Color.LIME)).common(new Gun(MP7.getInstance(), ChatColor.WHITE + "Skulls", "SKULLS", Color.WHITE))

		                         .build());

	yml.set(connect(KEY_FREE), Arrays.stream(FreeRewardEquipment.VALUES).map(EquipmentManager::toJson).collect(Collectors.toList()));

	private static FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> createContents(CaseContent[] rawContents)
	{
		FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> contents = new FilledHashMap<>();

		for(CaseContentRarity rarity : CaseContentRarity.values())
			contents.put(rarity, new FilledArrayList<>());

		for(CaseContent content : rawContents)
		{
			CaseContentRarity rarity = content.getRarity();
			FilledArrayList<CaseContent> rarityContents = contents.get(rarity);

			rarityContents.add(content);
		}

		return contents;
	}*/

	private static String connect(String... parts)
	{
		return Joiner.on('.').join(parts);
	}
}
