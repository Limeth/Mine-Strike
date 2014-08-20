package cz.minestrike.me.limeth.minestrike.events;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;

public class SneakPacketEvent extends MSPlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled, sneaking;
	private final MSPlayer sneakingPlayer;
	
	public SneakPacketEvent(MSPlayer viewingPlayer, MSPlayer sneakingPlayer, boolean sneaking)
	{
		super(viewingPlayer);
		
		this.sneakingPlayer = sneakingPlayer;
		this.sneaking = sneaking;
	}
	
	public static void update(Player msPlayer, Player[] receivers, boolean sneaking)
	{
		ProtocolManager pm = MineStrike.getProtocolManager();
		PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		ArrayList<WrappedWatchableObject> list = new ArrayList<WrappedWatchableObject>();
		
		list.add(new WrappedWatchableObject(0, (byte) (sneaking ? 2 : 0)));
		packet.getIntegers().write(0, msPlayer.getPlayer().getEntityId());
		packet.getWatchableCollectionModifier().write(0, list);
		
		for(Player receiver : receivers)
			try
			{
				pm.sendServerPacket(receiver, packet, true);
			}
			catch(InvocationTargetException e)
			{
				e.printStackTrace();
			}
	}
	
	public static void update(Player msPlayer, Player[] receivers)
	{
		update(msPlayer, receivers, msPlayer.getPlayer().isSneaking());
	}
	
	public MSPlayer getSneakingPlayer()
	{
		return sneakingPlayer;
	}
	
	public boolean isSneaking()
	{
		return sneaking;
	}
	
	public void setSneaking(boolean sneaking)
	{
		this.sneaking = sneaking;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
