package cz.minestrike.me.limeth.minestrike.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.PlayerMetadataPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
 * @author Limeth
 */
public class EntityMetadataPacketAdapter extends PacketAdapter
{
	public EntityMetadataPacketAdapter(Plugin plugin, ListenerPriority priority)
	{
		super(plugin, priority, PacketType.Play.Server.ENTITY_METADATA);
	}

	@Override
	public void onPacketSending(PacketEvent event)
	{
		PluginManager pm = Bukkit.getPluginManager();
		PacketContainer packet = event.getPacket();
		Player viewer = event.getPlayer();
		MSPlayer msViewer = MSPlayer.get(viewer);
		int playerId = packet.getIntegers().read(0);
		Player targetPlayer = PacketManager.getPlayerByEntityId(playerId);

		if(targetPlayer == null)
			return;

		MSPlayer msTarget = MSPlayer.get(targetPlayer);
		StructureModifier<List<WrappedWatchableObject>> modifier = packet.getWatchableCollectionModifier();
		List<WrappedWatchableObject> data = modifier.read(0);
		PlayerMetadataPacketEvent customEvent = PlayerMetadataPacketEvent.of(msViewer, msTarget, data);

		event.setReadOnly(false);
		customEvent.setCancelled(event.isCancelled());
		pm.callEvent(customEvent);
		event.setCancelled(customEvent.isCancelled());

		modifier.write(0, customEvent.getData());
		customEvent.debug();
	}
}
