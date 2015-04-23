package cz.minestrike.me.limeth.minestrike.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.PlayerMetadataPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * @author Limeth
 */
public class NamedEntitySpawnPacketAdapter extends PacketAdapter
{
	public NamedEntitySpawnPacketAdapter(Plugin plugin, ListenerPriority priority)
	{
		super(plugin, priority, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
	}

	@Override
	public void onPacketSending(PacketEvent event)
	{
		PacketContainer packet = event.getPacket();
		int playerId = packet.getIntegers().read(0);
		Player spawnedPlayer = PacketManager.getInstance().getPlayerByEntityId(playerId);

		if(spawnedPlayer == null)
			return;

		Player viewingPlayer = event.getPlayer();
		BukkitScheduler scheduler = Bukkit.getScheduler();

		scheduler.scheduleSyncDelayedTask(MineStrike.getInstance(), () -> PlayerMetadataPacketEvent.update(spawnedPlayer, viewingPlayer));
	}
}
