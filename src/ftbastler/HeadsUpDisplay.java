package ftbastler;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R1.DataWatcher;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cz.minestrike.me.limeth.minestrike.MineStrike;

// IMPORTANT
// Remember to change "yourMainClass" to your plugin's main class and "yourPackage" to your package's name
// Original code by chasechocolate
// Modified by ftbastler for Minecraft 1.7
// Modified by Limeth for own purposes

public class HeadsUpDisplay
{
	public static final int ENTITY_ID = 1234, WITHER_HEALTH = 300;

	private static HashMap<String, BukkitRunnable> healthBars = new HashMap<String, BukkitRunnable>();

	public static void sendPacket(Player player, Packet packet)
	{
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

		entityPlayer.playerConnection.sendPacket(packet);
	}

	public static Field getField(Class<?> cl, String field_name)
	{
		try
		{
			Field field = cl.getDeclaredField(field_name);
			return field;
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	// Accessing packets
	@SuppressWarnings("deprecation")
	public static PacketPlayOutSpawnEntityLiving getMobPacket(String text, Location loc)
	{
		PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving();

		try
		{
			Field a = getField(mobPacket.getClass(), "a");
			a.setAccessible(true);
			a.set(mobPacket, (int) ENTITY_ID);

			Field b = getField(mobPacket.getClass(), "b");
			b.setAccessible(true);
			b.set(mobPacket, (byte) EntityType.WITHER.getTypeId());

			Field c = getField(mobPacket.getClass(), "c");
			c.setAccessible(true);
			c.set(mobPacket, (int) Math.floor(loc.getBlockX() * 32.0D));

			Field d = getField(mobPacket.getClass(), "d");
			d.setAccessible(true);
			d.set(mobPacket, (int) Math.floor(loc.getBlockY() * 32.0D));

			Field e = getField(mobPacket.getClass(), "e");
			e.setAccessible(true);
			e.set(mobPacket, (int) Math.floor(loc.getBlockZ() * 32.0D));

			Field f = getField(mobPacket.getClass(), "f");
			f.setAccessible(true);
			f.set(mobPacket, (byte) 0);

			Field g = getField(mobPacket.getClass(), "g");
			g.setAccessible(true);
			g.set(mobPacket, (byte) 0);

			Field h = getField(mobPacket.getClass(), "h");
			h.setAccessible(true);
			h.set(mobPacket, (byte) 0);

			Field i = getField(mobPacket.getClass(), "i");
			i.setAccessible(true);
			i.set(mobPacket, (byte) 0);

			Field j = getField(mobPacket.getClass(), "j");
			j.setAccessible(true);
			j.set(mobPacket, (byte) 0);

			Field k = getField(mobPacket.getClass(), "k");
			k.setAccessible(true);
			k.set(mobPacket, (byte) 0);

		}
		catch(IllegalArgumentException e1)
		{
			e1.printStackTrace();
		}
		catch(IllegalAccessException e1)
		{
			e1.printStackTrace();
		}

		DataWatcher watcher = getWatcher(text, WITHER_HEALTH);

		try
		{
			Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
			t.setAccessible(true);
			t.set(mobPacket, watcher);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return mobPacket;
	}

	public static PacketPlayOutEntityDestroy getDestroyEntityPacket()
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy();

		Field a = getField(packet.getClass(), "a");
		a.setAccessible(true);
		try
		{
			a.set(packet, new int[] { ENTITY_ID });
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return packet;
	}

	public static PacketPlayOutEntityMetadata getMetadataPacket(DataWatcher watcher)
	{
		PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata();

		Field a = getField(metaPacket.getClass(), "a");
		a.setAccessible(true);
		try
		{
			a.set(metaPacket, (int) ENTITY_ID);
		}
		catch(IllegalArgumentException e1)
		{
			e1.printStackTrace();
		}
		catch(IllegalAccessException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			Field b = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
			b.setAccessible(true);
			b.set(metaPacket, watcher.c());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return metaPacket;
	}

	public static PacketPlayInClientCommand getRespawnPacket()
	{
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand();

		Field a = getField(packet.getClass(), "a");
		a.setAccessible(true);
		try
		{
			a.set(packet, (int) 1);
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return packet;
	}

	public static DataWatcher getWatcher(String text, int health)
	{
		DataWatcher watcher = new DataWatcher(null);

		watcher.a(0, (Byte) (byte) 0x20); // Flags, 0x20 = invisible
		watcher.a(6, (Float) (float) health);
		watcher.a(10, (String) text); // Entity name
		watcher.a(11, (Byte) (byte) 1); // Show name, 1 = show, 0 = don't show
		// watcher.a(16, (Integer) (int) health); //Wither health, WITHER_HEALTH = full
		// health

		return watcher;
	}
	
	public static boolean hasBar(Player player)
	{
		return healthBars.containsKey(player.getName());
	}

	// Other methods
	public static void displayTextBar(String text, Player player)
	{
		if(hasBar(player))
			removeBar(player);
		
		PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, player.getLocation());
		sendPacket(player, mobPacket);
		healthBars.put(player.getName(), null);
	}
	
	public static void removeBar(Player player)
	{
		String playerName = player.getName();
		BukkitRunnable runnable = healthBars.get(playerName);
		
		if(runnable != null)
			runnable.cancel();
		
		PacketPlayOutEntityDestroy destroyEntityPacket = getDestroyEntityPacket();
		sendPacket(player, destroyEntityPacket);
		healthBars.remove(playerName);
	}
	
	public static void displayLoadingBar(String text, Player player, double progress, double healthAdd, long delay, boolean loadUp, Runnable onFinish)
	{
		if(hasBar(player))
			removeBar(player);
		
		PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, player.getLocation());

		sendPacket(player, mobPacket);

		BukkitRunnable runnable = new BukkitRunnable()
		{
			double health = (loadUp ? progress : (WITHER_HEALTH - progress));

			@Override
			public void run()
			{
				if((loadUp ? health < WITHER_HEALTH : health > 0))
				{
					DataWatcher watcher = getWatcher(text, (int) health);
					PacketPlayOutEntityMetadata metaPacket = getMetadataPacket(watcher);

					sendPacket(player, metaPacket);
					
					health += loadUp ? healthAdd : -healthAdd;
				}
				else
				{
					DataWatcher watcher = getWatcher(text, (loadUp ? WITHER_HEALTH : 0));
					PacketPlayOutEntityMetadata metaPacket = getMetadataPacket(watcher);

					sendPacket(player, metaPacket);
					removeBar(player);
					
					onFinish.run();
					this.cancel();
				}
			}
		};
		
		healthBars.put(player.getName(), runnable);
		runnable.runTaskTimer(MineStrike.getInstance(), delay, delay);
	}

	public static void displayLoadingBar(String text, Player player, double progress, double duration, boolean loadUp, Runnable onFinish)
	{
		long period = (long) (duration / WITHER_HEALTH);
		
		if(period <= 0)
			period = 1;
		
		double healthChange = (period * WITHER_HEALTH) / duration;
		progress *= healthChange;
		
		displayLoadingBar(text, player, progress, healthChange, period, loadUp, onFinish);
	}
}