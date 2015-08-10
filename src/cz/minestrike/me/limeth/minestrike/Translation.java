package cz.minestrike.me.limeth.minestrike;

import cz.minestrike.me.limeth.minestrike.scene.games.listeners.MSInteractionListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static cz.minestrike.me.limeth.minestrike.scene.games.listeners.MSInteractionListener.CHARACTER_SKULL;

public enum Translation
{
	ENABLED("&8\u00BB&9&lMINE&f&l-&6&lSTRIKE&8\u00AB&a&l has been successfully loaded!",
			"&8\u00BB&9&lMINE&f&l-&6&lSTRIKE&8\u00AB&a&l byl uspesne nacten!"),
	DISABLING("&c&lWe are sorry for disturbing you, &8\u00BB&9&lMINE&f&l-&6&lSTRIKE&8\u00AB&c&l being updated.",
			"&c&lOmlouváme se za vyrusení, &8\u00BB&9&lMINE&f&l-&6&lSTRIKE&8\u00AB&c&l se aktualizuje."),
	ERROR("&c&lAn error occured.", "&c&lNastala chyba."),
	RELOAD("Press &lQ", "Stisknete &lQ"),
	KICK_RESTARTING("&8\u00BB&9MINE&f-&6STRIKE&8\u00AB&f\nServer is restarting...", "&8\u00BB&9MINE&f-&6STRIKE&8\u00AB&f\nProbíhá restart serveru..."),
	TEAM_TERRORISTS("Terrorists", "Teroristé"),
	TEAM_COUNTERTERRORISTS("Counter-Terrorists", "Zásahová Jednotka"),
	
	GAME_JOIN_TEAM_T("&7You have joined the &6Terrorists&7.", "&7Pripojil(a) jste se k &6Teroristum&7."),
	GAME_JOIN_TEAM_CT("&7You have joined the &9Counter-Terrorists&7.", "&7Pripojil(a) jste se k &9Zásahové Jednotce&7."),
	GAME_JOIN_SPECTATORS("&7You have joined the &fSpectators&7.", "&7Pripojil(a) jste se k &fPozorovatelum&7."),
	GAME_TEAMSELECT_FULL_TITLE("&cThis team has more players.", "&cTento tym ma prevahu."),
	GAME_TEAMSELECT_FULL_SUBTITLE("&cPlease join the other one, or purchase &8[&bVIP&8]&c.", "&cPripojte se prosim do druheho, nebo si kupte &8[&bVIP&8]&c."),
	GAME_ROUND_END_PLURAL("{1} have won!", "{1} vyhráli!"),
	GAME_ROUND_END_SINGULAR("{1} has won!", "{1} vyhrál!"),
	GAME_ROUND_END_NONE("No-one has won!", "Nikdo nevyhrál!"),
	GAME_MATCH_END_1_PLURAL("{1}", "{1}"),
	GAME_MATCH_END_2_PLURAL("Have won this match", "Vyhráli tento zápas"),
	GAME_MATCH_END_1_SINGULAR("{1}", "{1}"),
	GAME_MATCH_END_2_SINGULAR("Has won this match", "Vyhrál tento zápas"),
	GAME_MATCH_END_1_NONE("No-one", "Nikdo"),
	GAME_MATCH_END_2_NONE("has won this match", "nevyhrál tento zápas"),
	GAME_POLL_HEADER_VOTING("&a&lMap Poll &8| &e{1}s", "&a&lHlasování o mapu &8| &e{1}s"),
	GAME_POLL_HEADER_CHANGING("&2&lChanging in &8| &6{1}s", "&2&lZmena za &8| &6{1}s"),
	GAME_POLL_VOTE_SUCCESS("&aYou have voted for map &e&l{1}&a.", "&aHlasoval(a) jste pro mapu &e&l{1}&a."),
	GAME_POLL_VOTE_REPEATED("&cYou have already voted for map &e&l{1}&c!", "&cJiz jste hlasoval(a) pro mapu &e&l{1}&c!"),
	GAME_POLL_CHANGING("&eMap poll ended. The next map will be &l{1}&e.", "&eHlasování ukonceno. Dalsí mapa bude &l{1}&e."),
	GAME_SHOP_PURCHASED("&aYou have purchased &e{1}&a.", "&aKoupil(a) jste &e{1}&a."),
	GAME_SHOP_ICONPRICE("{1} &8(&a$&l{2}&8)", "{1} &8(&a$&l{2}&8)"),
	GAME_SHOP_ERROR_GRENADE_FULL_GENERAL("&cYou cannot carry any more grenades.", "&cVíce granatu neuneses."),
	GAME_SHOP_ERROR_GRENADE_FULL_SPECIFIC("&cYou cannot carry any more &e{1}&cs.", "&cVíce &e{1} &cneuneses."),
	@Deprecated GAME_SHOP_ERROR_UNKNOWN("&cUnknown equipment type &e{1}&c.", "&cNeznámy typ vybavení - &e{1}&c."),
	GAME_SHOP_ERROR_BALANCE("&cNot enough money for &e{1}&c.", "&cNedostatek penez pro &e{1}&c."),
	GAME_SHOP_ERROR_SCHEME("&cInvalid scheme.", "&cNesprávné schéma."),
	GAME_SHOP_ERROR_AWAY("&cYou are not in the shopping zone.", "&cNejste v nakupovací zóne."),
	GAME_SHOP_ERROR_MOVED("&cYou cannot move before opening the shop.", "&cNesmíte se hybat po respawnu pred nakupováním."),
	GAME_SHOP_ERROR_UNAVAILABLE_MAP("&cShopping not available on this map.", "&cNakupování na této mape není dostupné."),
	GAME_SHOP_ERROR_UNAVAILABLE_GAME("&cYou can not purchase any equipment in this game type.", "&cV tomto herním módu nelze nakupovat vybavení."),
	GAME_SHOP_ERROR_SLOTTAKEN("&cWeapon slot already taken!", "&cMísto na zbran je jiz zabráno."),
	GAME_SHOP_ERROR_KEVLARNEW("&cYou already own a brand new kevlar vest.", "&cJiz na sobe máte neposkozenou kevlar vestu."),
	GAME_SHOP_ERROR_HELMETPRESENT("&cYou already own a helmet.", "&cJiz na sobe máte helmu."),
	GAME_SHOP_ERROR_SETPRESENT("&cYou already own a brand new kevlar vest and a helmet.", "&cJiz na sobe máte neposkozenou kevlar vestu a helmu."),
	GAME_SHOP_ERROR_DEFUSEKITTEAM("&cYou have to be in the counter-terrorists' team get a defuse kit.", "&cMusíte byt v tymu zásahové jednotky pro získání zneskodnovacího náradí."),
	GAME_SHOP_ERROR_DEFUSEKITPRESENT("&cYou already own a defuse kit.", "&cJiz máte zneskodnovací náradí."),
	GAME_SHOP_ERROR_BOMBTEAM("&cYou have to be in the terrorists' team to get a bomb.", "&cMusíte byt v tymu teroristu pro získání bomby."),
	GAME_SHOP_ERROR_BOMBPRESENT("&cYou already own a bomb.", "&cJiz máte bombu."),
	GAME_BOMB_RECEIVED("&c&lYou have the bomb, plant it at a bombsite.", "&c&lMás bombu, aktivuj ji na stanovisti."),
	GAME_BOMB_INVALIDPLACEMENT("&cThe bomb must be planted at a bombsite.", "&cBomba musí byt polozena na stanovisti."),
	@Deprecated GAME_BOMB_PLANTED("&c&lThe bomb has been planted!", "&c&lBomba byla polozena!"),
	GAME_BOMB_DEFUSED("&e&lThe bomb has been defused.", "&e&lBomba byla zneskodnena."),
	GAME_DEATH_UNKNOWN("&7" + CHARACTER_SKULL + " &r{1}"),
	GAME_DEATH_SUICIDE_SOLO("&7" + CHARACTER_SKULL + " &r{1}&7 < &r{2}&r{3}&7"),
	GAME_DEATH_WEAPONSOURCE_SOLO("&7" + CHARACTER_SKULL + " &r{1}&7 < &r{2}&r{3}&7 < &r{4}&7"),
	GAME_DEATH_SUICIDE_ASSIST("&7" + CHARACTER_SKULL + " &r{1}&7 < &r{2}&r{3}&7 < &r{1}&7 + &r{4}&7"),
	GAME_DEATH_WEAPONSOURCE_ASSIST("&7" + CHARACTER_SKULL + " &r{1}&7 < &r{2}&r{3}&7 < &r{4}&7 + &r{5}&7"),

	EQUIPMENT_PLACEHOLDER_NAME("&c&lAn error occured", "&c&lNastala chyba"),
	EQUIPMENT_CUSTOMIZATION_NAME("&r{2} &7(&r{1}&7)"),
	EQUIPMENT_CASE_NAME("{1}&f Case", "{1}&f Bedna"),
	EQUIPMENT_CASE_OPENED("&7{1}&6&l opened &7{2}&f Case&6&l and got &7&l{3}&6&l!", "&6&lHrác &7{1}&6&l otevrel &7{2}&f Bednu&6&l a získal &7{3}&6&l!"),
	EQUIPMENT_KEY("{1}&f Case &lKey", "&f&lKlíč&f pro {1}&f Bednu"),
	EQUIPMENT_DEFUSEKIT_DEFAULT("&rDefuse Kit", "&rNáradí"),
	EQUIPMENT_DEFUSEKIT_BOUGHT("&aAdvanced Defuse Kit", "&aPokrocilé Náradí"),
	EQUIPMENT_CATEGORY_PISTOLS("Pistols", "Pistole"),
	EQUIPMENT_CATEGORY_HEAVY("Heavy", "Tezké"),
	EQUIPMENT_CATEGORY_SMGS("SMGs", "Samopaly"),
	EQUIPMENT_CATEGORY_RIFLES("Rifles", "Pusky"),
	EQUIPMENT_CATEGORY_GEAR("Gear", "Vystroj"),
	EQUIPMENT_CATEGORY_GRENADES("Grenades", "Granáty"),
	EQUIPMENT_RARITY_COMMON("Common", "Bezny"),
	EQUIPMENT_RARITY_VALUABLE("Valuable", "Cenny"),
	EQUIPMENT_RARITY_RARE("Rare", "Vzácny"),
	EQUIPMENT_RARITY_UNIQUE("Unique", "Unikátní"),
	EQUIPMENT_RARITY_LEGENDARY("Legendary", "Legendární"),
	EQUIPMENT_RADAR("&5Radar"),
	EQUIPMENT_KNIFE("&fKnife", "&fNuz"),
	EQUIPMENT_BURSTFIRE_ENABLED("&7Burstfire mode enabled", "&7Burstfire mód povolen"),
	EQUIPMENT_BURSTFIRE_DISABLED("&7Burstfire mode disabled", "&7Burstfire mód zakázán"),

	BUTTON_INVENTORY_USE("Open Inventory", "Otevrít Inventár"),
	BUTTON_INVENTORY_BACK("&fBack", "&fZpet"),
	BUTTON_INVENTORY_EQUIP("&aEquip", "&aVybavit"),
	BUTTON_INVENTORY_UNEQUIP("&eUnequip", "&eOdlozit"),
	BUTTON_CASE_USE("&f&lUnlock case using a(n) {1}&f&l key.", "&f&lOdemknout bednu pomocí {1}&f&l klíce."),
	BUTTON_CASE_ERROR_KEYNOTFOUND("&cYou don't have a(n) {1}&f Case &lKey&c.", "&cNemáte žádny &f&lKlíč&f pro {1}&f Bednu&c."),
	BUTTON_KEY_USE("&f&lUnlock a(n) {1}&f&l case.", "&f&lOtevřít {1}&f&l Bednu"),
	BUTTON_KEY_ERROR_CASENOTFOUND("&cYou don't have a(n) {1}&f Case&c.", "&cNemáte žádnou {1}&f Bednu&c."),
	BUTTON_QUITGAME("&4Quit the game", "&4Opustit hru"),
	BUTTON_TEAMCHANGE("&fChange team", "&fZmenit tym"),

	INVENTORY_TITLE("Inventory", "Inventár"),
	INVENTORY_SELECTION_TITLE("Selection", "Vyber"),
	CASE_TITLE("Case", "Bedna"),

	XP_GAIN("&aYou have gained &l{1}&a experience.", "&aZískal(a) jste &l{1}&a zkuseností."),
	XP_LOSS("&cYou have lost &l{1}&c experience.", "&cZtratil(a) jste &l{1}&c zkuseností."),
	XP_LEVEL_UPGRADE("&aYou now have the &r{1}&a rank!", "&aNyní máte &r{1}&a rank!"),
	XP_LEVEL_DOWNGRADE("&cYou now have the &r{1}&c rank!", "&cNyní máte &r{1}&c rank!"),

	DISPLAY_RANK_TITLE("{1}  {2}"),
	DISPLAY_RANK_SUBTITLE("&8[{1}&8]  &7{2} / {3} XP", "&8[{1}&8]  &7{2} / {3} ZK"),
	DISPLAY_RANK_NOTYET_1("&7You don't have a rank yet.", "&7Zatím nemáte hodnost."),
	DISPLAY_RANK_NOTYET_2("&fProgress:", "&fPrubeh:"),
	DISPLAY_RANK_BOTTOM("&7{1} / {2} XP", "&7{1} / {2} ZK"),

	COMMAND_TOP_COOLDOWN("&cYou can use this command in {1} seconds again.", "&cTento príkaz muzete znovu pouzit za {1} sekund."),
	COMMAND_TOP_INVALIDPAGE("&cInvalid page!", "&cNesprávná strana!"),
	COMMAND_TOP_ENTRY("&8[&7{1}&8|&f{2}&8]&f {3}&7   {4} XP", "&8[&7{1}&8|&7{2}&8]&f {3}&7   {4} ZK"),

	TAB_HEADER("&8&l»&9&lMINE&f&l-&6&lSTRIKE&8&l«"),
	TAB_LOBBY_FOOTER("&lWebsite not specified.", "&lWeb nebyl urcen."),
	TAB_GAME_DEFUSE_FOOTER("&9{3} &8[ &9&l{1} &f: &6&l{2} &8] &6{4}"),

	ACTIONBAR_GAME_DEFUSE_BOMB_PLANTED_T("&4&lThe bomb has been planted, don't let them defuse it.", "&4&lBomba byla polozena, kryj ji."),
	ACTIONBAR_GAME_DEFUSE_BOMB_PLANTED_CT("&4&lThe bomb has been planted, defuse it.", "&4&lBomba byla polozena, zneskodni ji."),
	REWARD_GIVEN("&ePlayer &r{1}&e was awarded by a gift: &r{2}", "&eHrác &r{1}&e byl za aktivitu odmenen dárkem: &r{2}"),
	CLAN_CREATE_ERROR_LOWRANK("&cYour rank must be &6Gold I &cor higher to create a clan. Purchase &8[&bVIP&8] &cto create it now.", "&cVase hodnost musí byt alespon &6Gold I&c. Kupte si &8[&bVIP&8] &cpro vytvorení klanu nyní."),
	DATA_COMPARISON("&8&l--- DATA CHANGE NOTIFICATION ---\n&r{1}\n&8&l--- --- --- --- ---", "&8&l--- UPOZORNENI NA ZMENU DAT ---\n&r{1}\n&8&l--- --- --- --- ---"),;

	public static final String ENGLISH_NAME = "en", CZECH_NAME = "cz", DEFAULT_LANGUAGE_NAME = ENGLISH_NAME;
	private final String defaultEN;
	private final String defaultCZ;
	private       String message;

	private Translation(String defaultEN, String defaultCZ)
	{
		this.defaultEN = defaultEN;
		this.defaultCZ = defaultCZ;
		setMessage(defaultEN);
	}

	private Translation(String def)
	{
		this(def, def);
	}

	public static void load()
	{
		try
		{
			refreshDefaultFiles();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		File file = getFile(MSConfig.getLanguageName());

		if(!file.isFile())
			return;

		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

		for(Translation msg : values())
		{
			String yamlName = msg.yamlName();

			if(yml.contains(yamlName))
			{
				String value = yml.getString(yamlName);

				msg.setMessage(value);
			}
		}
	}

	private static void refreshDefaultFiles() throws IOException
	{
		refreshEnglishFile();
		refreshCzechFile();
	}

	private static void refreshEnglishFile() throws IOException
	{
		File enFile = getFile(ENGLISH_NAME);

		if(!enFile.isFile())
		{
			if(enFile.exists())
				if(!enFile.delete())
					throw new IOException("Could not delete a directory blocking the english language file path.");

			if(!enFile.getParentFile().isDirectory() && !enFile.getParentFile().mkdirs())
				throw new IOException("Could not create a directory for the english language file.");

			if(!enFile.createNewFile())
				throw new IOException("Could not create the english language file.");
		}

		YamlConfiguration yml = YamlConfiguration.loadConfiguration(enFile);

		for(Translation msg : values())
		{
			String yamlName = msg.yamlName();

			if(!yml.contains(yamlName))
			{
				String value = msg.defaultEN;

				yml.set(yamlName, value);
			}
		}

		yml.save(enFile);
	}

	private static void refreshCzechFile() throws IOException
	{
		File czFile = getFile(CZECH_NAME);

		if(!czFile.isFile())
		{
			if(czFile.exists())
				if(!czFile.delete())
					throw new IOException("Could not delete a directory blocking the czech language file path.");

			if(!czFile.getParentFile().isDirectory() && !czFile.getParentFile().mkdirs())
				throw new IOException("Could not create a directory for the czech language file.");

			if(!czFile.createNewFile())
				throw new IOException("Could not create the czech language file.");
		}

		YamlConfiguration yml = YamlConfiguration.loadConfiguration(czFile);

		for(Translation msg : values())
		{
			String yamlName = msg.yamlName();

			if(!yml.contains(yamlName))
			{
				String value = msg.defaultCZ;

				yml.set(yamlName, value);
			}
		}

		yml.save(czFile);
	}

	public static File getFile(String language)
	{
		return new File("plugins/MineStrike/lang_" + language + ".yml");
	}

	public String yamlName()
	{
		return name().toLowerCase().replace('_', '.');
	}

	public String getMessage(Object... args)
	{
		return replaceArguments(message, args);
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String replaceArguments(String original, Object... args)
	{
		for(int i = 0; i < args.length; i++)
		{
			String value = String.valueOf(args[i]);
			original = original.replaceAll("\\{" + (i + 1) + "\\}", value);
		}

		return original;
	}
}
