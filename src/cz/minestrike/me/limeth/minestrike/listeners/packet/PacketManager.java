package cz.minestrike.me.limeth.minestrike.listeners.packet;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

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
import cz.minestrike.me.limeth.minestrike.events.SneakPacketEvent;

public class PacketManager
{
	private PacketManager() {}
	
	public static void registerListeners()
	{
		ProtocolManager pm = MineStrike.getProtocolManager();
		
		pm.addPacketListener(new PacketAdapter(MineStrike.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA)
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
					
					MineStrike.warn("ID:" + object.getTypeID() + " INDEX:" + object.getIndex());
					
					if(object.getTypeID() != 0 || object.getIndex() != 0)
						return;
		
					MineStrike.warn("is sneak packet");
					
					byte value = (byte) object.getValue();
					
					if(value == 0)
						sneaking = false;
					else if(value == 2)
						sneaking = true;
					else
						return;
					
					SneakPacketEvent sneakEvent = new SneakPacketEvent(msViewing, msSneaking, sneaking);
					PluginManager pm = Bukkit.getPluginManager();
					
					pm.callEvent(sneakEvent);
					
					int newValue = (value & (Byte.MAX_VALUE ^ 2)) | (sneakEvent.isSneaking() ? 2 : 0);
					
					MineStrike.warn(ChatColor.GRAY + sneakingPlayer.getName() + " < " + viewingPlayer.getName() + '\n' + ChatColor.YELLOW + " [" + value + " > " + newValue + "] " + sneakEvent.isSneaking());
					
					if(sneakEvent.isCancelled())
					{
						iterator.remove();
						continue;
					}
					
					object.setValue((byte) newValue);
					modifier.write(0, list);
				}
				
				if(list.size() <= 0)
					event.setCancelled(true);
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
