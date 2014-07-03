package cz.minestrike.me.limeth.minestrike;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Translation
{
	GAME_POLL_HEADER_VOTING("&a&lMap Poll &8| &e{1}s", "&a&lHlasování o mapu &8| &e{1}s"),
	GAME_POLL_HEADER_CHANGING("&2&lChanging in &8| &6{1}s", "&2&lZmena za &8| &6{1}s"),
	GAME_POLL_VOTE_SUCCESS("&aYou have voted for map &e&l{1}&a.", "&aHlasoval(a) jste pro mapu &e&l{1}&a."),
	GAME_POLL_VOTE_REPEATED("&cYou have already voted for map &e&l{1}&c!", "&cJiz jste hlasoval(a) pro mapu &e&l{1}&c!"),
	GAME_POLL_CHANGING("&eMap poll ended. The next map will be &l{1}&e.", "&eHlasování ukonceno. Dalsí mapa bude &l{1}&e."),
	GAME_SHOP_PURCHASED("&aYou have purchased &e{1}&a.", "&aKoupil(a) jste &e{1}&a."),
	GAME_SHOP_ERROR_GRENADE_FULL_GENERAL("&cYou cannot carry any more grenades.", "&cVíce granatu neuneses."),
	GAME_SHOP_ERROR_GRENADE_FULL_SPECIFIC("&cYou cannot carry any more &e{1}&cs.", "&cVíce &e{1} &cneuneses."),
	GAME_SHOP_ERROR_UNKNOWN("&cUnknown equipment type &e{1}&c.", "&cNeznámy typ vybavení - &e{1}&c."),
	GAME_SHOP_ERROR_BALANCE("&cNot enough money for &e{1}&c.", "&cNedostatek penez pro &e{1}&c."),
	GAME_SHOP_ERROR_SCHEME("&cInvalid scheme.", "&cNesprávné schéma."),
	GAME_SHOP_ERROR_AWAY("&cYou are not in the shopping zone.", "&cNejste v nakupovací zóne."),
	GAME_SHOP_ERROR_UNAVAILABLE_MAP("&cShopping not available on this map.", "&cNakupování na této mape není dostupné."),
	GAME_SHOP_ERROR_UNAVAILABLE_GAME("&cYou can not purchase any equipment in this game type.", "&cV tomto herním módu nelze nakupovat vybavení."),
	;
	
	public static final String ENGLISH_NAME = "en", CZECH_NAME = "cz", DEFAULT_LANGUAGE_NAME = ENGLISH_NAME;
	private final String defaultEN;
	private final String defaultCZ;
	private String message;
	
	private Translation(String defaultEN, String defaultCZ)
	{
		this.defaultEN = defaultEN;
		this.defaultCZ = defaultCZ;
		setMessage(defaultEN);
	}
	
	public static void load()
	{
		try
		{
			refreshDefaultFiles();
		}
		catch(IOException e) { System.out.println(e); }
		
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
				enFile.delete();
			
			enFile.getParentFile().mkdirs();
			enFile.createNewFile();
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
				czFile.delete();
			
			czFile.getParentFile().mkdirs();
			czFile.createNewFile();
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
		String result = message;
		
		for(int i = 0; i < args.length; i++)
		{
			String value = args[i].toString();
			result = result.replaceAll("\\{" + (i + 1) + "\\}", value);
		}
		
		return result;
	}
	
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = ChatColor.translateAlternateColorCodes('&', message);
	}
}
