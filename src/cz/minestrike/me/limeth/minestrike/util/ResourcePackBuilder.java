package cz.minestrike.me.limeth.minestrike.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;

public class ResourcePackBuilder
{
	public static void main(String[] args) throws IOException
	{
		build(new File("C:\\Users\\Limeth\\AppData\\Roaming\\.minecraft\\resourcepacks\\Mine-Strike_1.0.4"));
	}
	
	private ResourcePackBuilder() {}
	
	public static void build(File directory) throws IOException
	{
		for(GunType gunType : GunType.values())
		{
			String name = gunType.getDirectoryName();
			String texturePath = "projectsurvive:textures/" + name + ".png";
			File propertiesFile = new File(directory, "assets\\minecraft\\mcpatcher\\cit\\projectsurvive\\textures\\" + name + ".properties");
			
			System.out.println(propertiesFile);
			
			if(!propertiesFile.exists())
			{
				propertiesFile.getParentFile().mkdirs();
				propertiesFile.createNewFile();
			}
			
			FileWriter writer = new FileWriter(propertiesFile);
			
			writer.write("#\n#  Made for the Mine-Strike server\n#  By Limeth\n#\n\n");
			writer.write("texture.fireworks_charge=" + texturePath + '\n');
			writer.write("texture.fireworks_charge_overlay=projectsurvive:textures/empty.png\n");
			writer.write("items=minecraft:firework_charge\n");
			writer.write("nbt.display.Lore.*=iregex:(.[0-9a-f].)(Type: )(" + gunType.name() + ")");
			writer.close();
		}
		
		for(GrenadeType grenadeType : GrenadeType.values())
		{
			String name = grenadeType.getDirectoryName();
			String texturePath = "projectsurvive:textures/" + name + ".png";
			File propertiesFile = new File(directory, "assets\\minecraft\\mcpatcher\\cit\\projectsurvive\\textures\\" + name + ".properties");
			
			System.out.println(propertiesFile);
			
			if(!propertiesFile.exists())
			{
				propertiesFile.getParentFile().mkdirs();
				propertiesFile.createNewFile();
			}
			
			FileWriter writer = new FileWriter(propertiesFile);
			
			writer.write("#\n#  Made for the Mine-Strike server\n#  By Limeth\n#\n\n");
			writer.write("texture.potion_bottle_splash=" + texturePath + '\n');
			writer.write("texture.potion_overlay=projectsurvive:textures/empty.png\n");
			writer.write("items=minecraft:potion\n");
			writer.write("damage=" + grenadeType.getColor());
			writer.close();
		}
	}
}
