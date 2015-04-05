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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Limeth
 */
public class EquipmentCustomizationManager
{
	public static final  File   FILE              = new File("plugins/MineStrike/equipmentCustomizations.json");
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
		YamlConfiguration yml = prepareFile();
		
		loadCases(yml);
		loadFreeEquipment(yml);
	}
	
	private static void loadCases(YamlConfiguration yml)
	{
		ConfigurationSection sectionCases = yml.getConfigurationSection(KEY_CASES);

		cases.clear();

		for(String caseId : sectionCases.getKeys(false))
		{
			boolean disabled = sectionCases.contains(connect(caseId, KEY_CASE_DISABLED)) && sectionCases.getBoolean(connect(caseId, KEY_CASE_DISABLED));

			if(disabled)
				continue;

			String caseName = ChatColor.translateAlternateColorCodes('&', sectionCases.getString(connect(caseId, KEY_CASE_NAME)));
			List<CaseContent> contents = Lists.newArrayList();

			for(CaseContentRarity rarity : CaseContentRarity.values())
			{
				String rarityKey = connect(caseId, KEY_CASE_CONTENT, rarity.name().toLowerCase());

				if(!sectionCases.contains(rarityKey))
					continue;

				if(sectionCases.isList(rarityKey))
				{
					List<String> rawEquipmentList = sectionCases.getStringList(rarityKey);

					for(int i = 0; i < rawEquipmentList.size(); i++)
					{
						String rawEquipment = rawEquipmentList.get(i);

						try
						{
							Equipment equipment = EquipmentManager.fromJson(rawEquipment);
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
				else if(sectionCases.isString(rarityKey))
				{
					String rawEquipment = sectionCases.getString(rarityKey);

					try
					{
						Equipment equipment = EquipmentManager.fromJson(rawEquipment);
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

		EquipmentManager.registerAll(cases);
	}
	
	private static void loadFreeEquipment(YamlConfiguration yml)
	{
		freeEquipment.clear();

		if(!yml.contains(KEY_FREE))
			return;

		if(yml.isList(KEY_FREE))
		{
			List<String> rawEquipmentList = yml.getStringList(KEY_FREE);

			for(int i = 0; i < rawEquipmentList.size(); i++)
			{
				String rawEquipment = rawEquipmentList.get(i);

				try
				{
					Equipment equipment = EquipmentManager.fromJson(rawEquipment);

					freeEquipment.add(equipment);
				}
				catch(Exception e)
				{
					MineStrike.warn("Couldn't parse free equipment #" + (i + 1) + ".");
					e.printStackTrace();
				}
			}
		}
		else if(yml.isString(KEY_FREE))
		{
			String rawEquipment = yml.getString(KEY_FREE);

			try
			{
				Equipment equipment = EquipmentManager.fromJson(rawEquipment);

				freeEquipment.add(equipment);
			}
			catch(Exception e)
			{
				MineStrike.warn("Couldn't parse free equipment.");
				e.printStackTrace();
			}
		}
	}

	private static YamlConfiguration prepareFile()
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

	private static YamlConfiguration prepareFileUnsafe() throws IOException
	{
		if(!FILE.isFile())
		{
			if(FILE.exists() && !FILE.delete())
				throw new IOException("Couldn't delete the equipmentCustomizations.json file.");

			if(!FILE.getParentFile().isDirectory() && !FILE.getParentFile().mkdirs())
				throw new IOException("Couldn't create a directory for the equipmentCustomizations.json file.");

			if(!FILE.createNewFile())
				throw new IOException("Couldn't create a new equipmentCustomizations.json file.");

			YamlConfiguration yml = new YamlConfiguration();
			String caseName = "exampleCase";
			String example = EquipmentManager.toJson(new Gun(FAMAS.getInstance(), ChatColor.DARK_RED + "Example Red", "EXAMPLE", Color.RED));

			yml.set(connect(KEY_CASES, caseName, KEY_CASE_NAME), "&4Example");
			yml.set(connect(KEY_CASES, caseName, KEY_CASE_DISABLED), false);

			for(CaseContentRarity rarity : CaseContentRarity.values())
				yml.set(connect(KEY_CASES, caseName, KEY_CASE_CONTENT, rarity.name().toLowerCase()), new String[0]);

			yml.set(connect(KEY_CASES, caseName, KEY_CASE_CONTENT, CaseContentRarity.LEGENDARY.name().toLowerCase()), new String[] {example});
			yml.save(FILE);

			return yml;
		}

		return YamlConfiguration.loadConfiguration(FILE);
	}

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
