package cz.minestrike.me.limeth.minestrike.listeners.packet;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

public class PacketManager implements Listener
{
	private static HashMap<Integer, Player> entityIdToPlayer = Maps.newHashMap();
	private static PacketManager            instance         = new PacketManager();

	private PacketManager()
	{
	}

	public void registerListeners()
	{
		PluginManager pm = Bukkit.getPluginManager();
		ProtocolManager prm = MineStrike.getProtocolManager();

		pm.registerEvents(this, MineStrike.getInstance());
		prm.addPacketListener(new EntityMetadataPacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL));
		prm.addPacketListener(new NamedEntitySpawnPacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL));
	}

	public Player getPlayerByEntityId(int id)
	{
		return entityIdToPlayer.get(id);
	}

	public void unregisterListeners()
	{
		MineStrike.getProtocolManager().removePacketListeners(MineStrike.getInstance());
		entityIdToPlayer.clear();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		int entityId = player.getEntityId();

		entityIdToPlayer.put(entityId, player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		int entityId = player.getEntityId();

		entityIdToPlayer.remove(entityId);
	}

	public static PacketManager getInstance()
	{
		return instance;
	}
}
