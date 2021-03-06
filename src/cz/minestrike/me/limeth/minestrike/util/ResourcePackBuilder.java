package cz.minestrike.me.limeth.minestrike.util;

import cz.minestrike.me.limeth.minestrike.equipment.grenades.GrenadeType;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.GunType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourcePackBuilder
{
	public static void main(String[] args) throws IOException
	{
		//build(new File("/home/limeth/.minecraft/resourcepacks/Mine-Strike_1.0.7"));
		build(new File("C:/Users/Limeth/AppData/Roaming/.minecraft/resourcepacks/Mine-Strike_1.0.9"));
	}
	
	private ResourcePackBuilder() {}
	
	public static void build(File directory) throws IOException
	{
		for(GunType gunType : GunType.getRegisteredTypes().values())
		{
			String name = gunType.getTextureName();
			String texturePath = "projectsurvive:textures/" + name + ".png";
			File propertiesFile = new File(directory, "assets/minecraft/mcpatcher/cit/projectsurvive/textures/" + name + ".properties");
			
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
			writer.write("nbt.display.Lore.*=iregex:(.[0-9a-f].)(Type: )(" + gunType.getGunId() + "( \\\\| DEFAULT)?)");
			writer.close();
		}
		
		for(GrenadeType grenadeType : GrenadeType.values())
		{
			String name = grenadeType.getDirectoryName();
			String texturePath = "projectsurvive:textures/" + name + ".png";
			File propertiesFile = new File(directory, "assets/minecraft/mcpatcher/cit/projectsurvive/textures/" + name + ".properties");
			
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
		
		buildKnives(directory);
	}
	
	public static void buildKnives(File directory) throws IOException
	{
		String[][] knifeSkins = {{"DEFAULT_CT", "knife_default_ct"}, {"DEFAULT_T", "knife_default_t"}};
		
		for(String[] skin : knifeSkins)
		{
			String loreSkin = skin[0];
			String texture = skin[1];
			
			String texturePath = "projectsurvive:textures/" + texture + ".png";
			File propertiesFile = new File(directory, "assets/minecraft/mcpatcher/cit/projectsurvive/textures/" + texture + ".properties");
			
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
			writer.write("nbt.display.Lore.*=iregex:(.[0-9a-f].)(Type: )(KNIFE( \\\\| " + loreSkin + ")?)\n");
			writer.close();
		}
	}
	
	@Deprecated
	public static void buildArmor(File directory) throws IOException
	{
		String[][] types = {
				{"KEVLAR", "kevlar"},
				{"HELMET", "helmet"},
				{"KEVLAR_AND_HELMET", "kevlar_and_helmet"}
		};
		
		for(String[] type : types)
		{
			String loreType = type[0];
			String texture = type[1];
			
			String texturePath = "projectsurvive:textures/" + texture + ".png";
			File propertiesFile = new File(directory, "assets/minecraft/mcpatcher/cit/projectsurvive/textures/" + texture + ".properties");
			
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
			writer.write("nbt.display.Lore.*=iregex:(.[0-9a-f].)(Type: )(" + loreType + ")\n");
			writer.close();
		}
	}
}
