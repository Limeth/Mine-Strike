package cz.minestrike.me.limeth.minestrike.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.PlayerMetadataPacketEvent;
import cz.minestrike.me.limeth.minestrike.events.SneakPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Iterator;
import java.util.List;

public class PacketManager
{
	private PacketManager() {}
	
	public static void registerListeners()
	{
		ProtocolManager pm = MineStrike.getProtocolManager();

		pm.addPacketListener(new EntityMetadataPacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL));
		pm.addPacketListener(new NamedEntitySpawnPacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL));
	}

	//TODO Find a faster way (HashMap?)
	public static Player getPlayerByEntityId(int id)
	{
		for(Player player : Bukkit.getOnlinePlayers())
			if(player.getEntityId() == id)
				return player;

		return null;
	}
	
	public static void unregisterListeners()
	{
		MineStrike.getProtocolManager().removePacketListeners(MineStrike.getInstance());
	}
}
