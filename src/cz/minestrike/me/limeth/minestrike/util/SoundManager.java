package cz.minestrike.me.limeth.minestrike.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R1.PlayerConnection;

import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;

public class SoundManager
{
	public static void play(PacketPlayOutNamedSoundEffect packet, Player... players)
	{
		for(Player player : players)
		{
			CraftPlayer craftPlayer = (CraftPlayer) player;
			EntityPlayer nmsPlayer = craftPlayer.getHandle();
			PlayerConnection connection = nmsPlayer.playerConnection;
			
			connection.sendPacket(packet);
		}
	}
	
	public static void play(String path, float volume, float pitch, Player... players)
	{
		for(Player player : players)
		{
			Location loc = player.getEyeLocation();
			PacketPlayOutNamedSoundEffect packet = buildPacket(path, loc, volume, pitch);
			
			play(packet, player);
		}
	}
	
	public static void play(String path, Location loc, float volume, float pitch, Player... players)
	{
		PacketPlayOutNamedSoundEffect packet = buildPacket(path, loc, volume, pitch);
		
		play(packet, players);
	}
	
	public static void play(String path, float volume, Player... players)
	{
		play(path, volume, 1, players);
	}
	
	public static void play(String path, Location loc, float volume, Player... players)
	{
		play(path, loc, volume, 1, players);
	}
	
	public static void play(String path, Player... players)
	{
		play(path, 1, players);
	}
	
	public static void play(String path, Location loc, Player... players)
	{
		play(path, loc, 1, players);
	}
	
	public static PacketPlayOutNamedSoundEffect buildPacket(String path, Location loc, float volume, float pitch)
	{
		return new PacketPlayOutNamedSoundEffect(path, loc.getX(), loc.getY(), loc.getZ(), volume, pitch);
	}
	
	public static PacketPlayOutNamedSoundEffect buildPacket(String path, Location loc, float volume)
	{
		return buildPacket(path, loc, volume, 1);
	}
	
	public static PacketPlayOutNamedSoundEffect buildPacket(String path, Location loc)
	{
		return buildPacket(path, loc, 1);
	}
	
	private static String removeExtension(String fullName) throws IOException
	{
		String[] directories = fullName.split("" + File.separatorChar);
		String name = directories[directories.length - 1];
		
		if(name.contains("."))
		{
			int dotIndex = 0;
			
			for(int i = fullName.length() - 1; i >= fullName.length() - name.length(); i--)
				if(fullName.charAt(i) == '.')
				{
					dotIndex = i;
					break;
				}
			
			fullName = fullName.substring(0, dotIndex);
		}
		
		return fullName;
	}
	
	@SuppressWarnings("unused")
	private static final String RAW_PATTERN_OLD = "^(.*?)([_\\-][0-9]+([_\\-][a-zA-Z]+)?$)?([0-9]+)?$";
	private static final String RAW_PATTERN_NEW = "^(.*?(([\\/|^]([a-zA-Z0-9]+)\\/(\4)).*?)?)([_\\-][0-9]+([_\\-][a-zA-Z]+)?$)?([0-9]+)?$";
	private static final Pattern PATTERN = Pattern.compile(RAW_PATTERN_NEW);
	
	private static String cutName(File file) throws IOException
	{
		String name = file.getCanonicalPath();
		String path = removeExtension(name);
		Matcher matcher = PATTERN.matcher(path);
		
		while(matcher.find())
		{
			String base = matcher.group(1);
			String suffix = matcher.group(7);
			
			if(suffix != null)
				base += suffix;
			
			return base;
		}
		
		throw new RuntimeException("File not found.");
	}
	
	public static FilledHashSet<File> findFiles(File origin)
	{
		FilledHashSet<File> files = new FilledHashSet<File>();
		File[] children = origin.listFiles();
		
		for(File child : children)
			if(child.isDirectory())
				files.addAll(findFiles(child));
			else if(child.isFile())
				try
				{
					String path = cutName(child);
					File childName = new File(path);
					
					files.add(childName);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
		
		return files;
	}
	
	public static String buildSoundsJson(File directoryUnderAssets, File directory, String category) throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject root = new JsonObject();
		FilledHashSet<File> nameFiles = findFiles(directory);
		String directoryUnderAssetsName = directoryUnderAssets.getCanonicalPath();
		int directoryUnderAssetsNameLength = directoryUnderAssetsName.length() + 1;
		
		for(File nameFile : nameFiles)
		{
			JsonObject soundObject = new JsonObject();
			JsonArray filesArray = new JsonArray();
			String nameFileName = nameFile.getCanonicalPath();
			String soundName = nameFileName.substring(directoryUnderAssetsNameLength).replace('/', '.');
			File nameFileDirectory = nameFile.getParentFile();
			
			for(File file : nameFileDirectory.listFiles())
			{
				String cutName = cutName(file);
				
				if(!cutName.equals(nameFileName))
					continue;
				
				String fileName = removeExtension(file.getCanonicalPath().substring(directoryUnderAssetsNameLength));
				
				filesArray.add(gson.toJsonTree(fileName));
			}
			
			soundObject.addProperty("category", category);
			soundObject.add("sounds", filesArray);
			root.add(soundName, soundObject);
		}
		
		return gson.toJson(root);
	}
}
