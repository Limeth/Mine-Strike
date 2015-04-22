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

		if(false)
			pm.addPacketListener(new PacketAdapter(MineStrike.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_METADATA)
			{
				@Override
				public void onPacketSending(PacketEvent event)
				{
					PacketContainer packet = event.getPacket();
					int playerId = packet.getIntegers().read(0);
					Player sneakingPlayer = getPlayerByEntityId(playerId);

					if(sneakingPlayer == null)
						return;

					Player viewingPlayer = event.getPlayer();
					MSPlayer msSneaking = MSPlayer.get(sneakingPlayer);
					MSPlayer msViewing = MSPlayer.get(viewingPlayer);
					StructureModifier<List<WrappedWatchableObject>> modifier = packet.getWatchableCollectionModifier();
					List<WrappedWatchableObject> list = modifier.read(0);
					Iterator<WrappedWatchableObject> iterator = list.iterator();
					boolean sneaking;

					while(iterator.hasNext())
					{
						WrappedWatchableObject object = iterator.next();

						System.out.println(object);

						if(object.getTypeID() != 0 || object.getIndex() != 0)
							return;

						byte value = (byte) object.getValue();
						sneaking = (value & 2) != 0;
						SneakPacketEvent sneakEvent = new SneakPacketEvent(msViewing, msSneaking, sneaking);
						PluginManager pm = Bukkit.getPluginManager();

						pm.callEvent(sneakEvent);

						int newValue = (value & (Byte.MAX_VALUE ^ 2)) | (sneakEvent.isSneaking() ? 2 : 0);

						if(sneakEvent.isCancelled())
						{
							iterator.remove();
							continue;
						}
						else if(sneakingPlayer != viewingPlayer)
							MineStrike.warn(viewingPlayer.getName() + " < " + (sneakEvent.isSneaking() ? "sneaking" : "standing") + " < " + sneakingPlayer.getName());

						object.setValue((byte) newValue);
						modifier.write(0, list);
					}

					if(list.size() <= 0)
						event.setCancelled(true);
				}
			});

		pm.addPacketListener(new PacketAdapter(MineStrike.getInstance(), ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_METADATA)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				PluginManager pm = Bukkit.getPluginManager();
				PacketContainer packet = event.getPacket();
				Player viewer = event.getPlayer();
				MSPlayer msViewer = MSPlayer.get(viewer);
				int playerId = packet.getIntegers().read(0);
				Player targetPlayer = getPlayerByEntityId(playerId);

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
		});

		pm.addPacketListener(new PacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_ENTITY_SPAWN)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{
				PacketContainer packet = event.getPacket();
				int playerId = packet.getIntegers().read(0);
				Player spawnedPlayer = getPlayerByEntityId(playerId);

				if(spawnedPlayer == null)
					return;

				Player viewingPlayer = event.getPlayer();
				BukkitScheduler scheduler = Bukkit.getScheduler();

				scheduler.scheduleSyncDelayedTask(MineStrike.getInstance(), () -> PlayerMetadataPacketEvent.update(spawnedPlayer, viewingPlayer));
			}
		});
	}
	
	private static Player getPlayerByEntityId(int id)
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
