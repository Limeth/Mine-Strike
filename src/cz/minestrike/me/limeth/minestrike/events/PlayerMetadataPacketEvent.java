package cz.minestrike.me.limeth.minestrike.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.spigotmc.ProtocolData;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Limeth
 */
@SuppressWarnings("unused")
public class PlayerMetadataPacketEvent extends MSPlayerEvent implements Cancellable
{
	private static final int         INDEX_VARIOUS                = 0;
	private static final int         BIT_VARIOUS_ONFIRE           = 0;
	private static final int         BIT_VARIOUS_CROUCHED         = 1;
	private static final int         BIT_VARIOUS_SPRINTING        = 3;
	private static final int         BIT_VARIOUS_USINGITEM        = 4;
	private static final int         BIT_VARIOUS_INVISIBLE        = 5;
	private static final int         INDEX_AIRLEVEL               = 1;
	private static final int         INDEX_NAMETAG                = 2;
	private static final int         INDEX_ALWAYSSHOWNAMETAG      = 3;
	private static final int         INDEX_HEALTH                 = 6;
	private static final int         INDEX_POTIONEFFECTCOLOR      = 7;
	private static final int         INDEX_POTIONEFFECTAMBIENT    = 8;
	private static final int         INDEX_NUMBEROFARROWSINENTITY = 9;
	private static final int         INDEX_SKINFLAGS              = 10;
	private static final int         INDEX_MISSINGAI              = 15;
	private static final int         INDEX_HIDECAPE               = 16;
	private static final int         BIT_HIDECAPE_HIDECAPE        = 1;
	private static final int         INDEX_ABSORPTIONHEARTS       = 17;
	private static final int         INDEX_SCORE                  = 18;
	private static final HandlerList handlers                     = new HandlerList();
	private boolean cancelled;
	private final MSPlayer msTarget;
	private Map<Integer, WrappedWatchableObject> dataMap = Maps.newHashMap();

	private PlayerMetadataPacketEvent(MSPlayer msViewer, MSPlayer msTarget)
	{
		super(msViewer);

		this.msTarget = msTarget;
	}

	public static PlayerMetadataPacketEvent of(MSPlayer msViewer, MSPlayer msTarget, List<WrappedWatchableObject> data)
	{
		PlayerMetadataPacketEvent event = new PlayerMetadataPacketEvent(msViewer, msTarget);

		if(data != null)
			for(WrappedWatchableObject dataEntry : data)
				event.dataMap.put(dataEntry.getIndex(), dataEntry);

		return event;
	}

	public static void update(Player target, Player... viewers)
	{
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);

		packet.getIntegers().write(0, target.getEntityId());

		for(Player viewer : viewers)
			try
			{
				pm.sendServerPacket(viewer, packet, true);
			}
			catch(InvocationTargetException e)
			{
				e.printStackTrace();
			}
	}

	public static void update(MSPlayer msTarget, MSPlayer... msViewers)
	{
		update(msTarget.getPlayer(), Arrays.stream(msViewers).map(MSPlayer::getPlayer).toArray(Player[]::new));
	}

	public List<WrappedWatchableObject> getData()
	{
		return Lists.newArrayList(dataMap.values());
	}

	public MSPlayer getMSTarget()
	{
		return msTarget;
	}

	private boolean getBit(int index, int bitPosition)
	{
		WrappedWatchableObject wrap = dataMap.get(index);

		if(wrap == null)
			return false;

		byte value = (byte) wrap.getValue();

		return ((value >> bitPosition) & 1) == 1;
	}

	private boolean setBit(int index, int bitPosition, boolean value)
	{
		WrappedWatchableObject wrap = dataMap.get(index);
		boolean present = wrap != null;

		if(!present)
		{
			if(value)
			{
				wrap = new WrappedWatchableObject(index, (byte) (1 << bitPosition));

				dataMap.put(index, wrap);

				return false;
			}
			else
				return false;
		}

		byte fullValue = (byte) wrap.getValue();
		boolean previousValue = ((fullValue >> bitPosition) & 1) == 1;
		fullValue = (byte) (value ? fullValue  | (1 << bitPosition)
		                          : fullValue & ~(1 << bitPosition));

		if(fullValue == 0)
			dataMap.remove(index);
		else
			wrap.setValue(fullValue);

		return previousValue;
	}

	private boolean getBitDual(int index, int bitPosition)
	{
		WrappedWatchableObject wrap = dataMap.get(index);

		if(wrap == null)
			return false;

		byte value = ((ProtocolData.DualByte) wrap.getValue()).value;

		return ((value >> bitPosition) & 1) == 1;
	}

	private boolean setBitDual(int index, int bitPosition, boolean value)
	{
		WrappedWatchableObject wrap = dataMap.get(index);
		boolean present = wrap != null;

		if(!present)
		{
			if(value)
			{
				wrap = new WrappedWatchableObject(index, new ProtocolData.DualByte((byte) (1 << bitPosition), (byte) 0));

				dataMap.put(index, wrap);

				return false;
			}
			else
				return false;
		}

		byte fullValue = ((ProtocolData.DualByte) wrap.getValue()).value;
		boolean previousValue = ((fullValue >> bitPosition) & 1) == 1;
		fullValue = (byte) (value ? fullValue  | (1 << bitPosition)
		                          : fullValue & ~(1 << bitPosition));

		if(fullValue == 0)
			dataMap.remove(index);
		else
			wrap.setValue(new ProtocolData.DualByte(fullValue, (byte) 0));

		return previousValue;
	}

	private <T> T updateOrRemove(int index, UnaryOperator<T> operator)
	{
		if(operator == null)
			return (T) dataMap.remove(index);
		else
			return (T) dataMap.compute(index, (k, v) -> {
				if(v == null)
				{
					T newValue = operator.apply(null);
					v = new WrappedWatchableObject(k, newValue);
				}
				else
				{
					T oldValue = (T) v.getValue();
					T newValue = operator.apply(oldValue);

					v.setValue(newValue, false);
				}

				return v;
			}).getValue();
	}

	private <T> T updateOrRemove(int index, T object)
	{
		return updateOrRemove(index, (a) -> object);
	}

	private <T> T get(int index)
	{
		WrappedWatchableObject wrap = dataMap.get(index);

		return (T) (wrap != null ? wrap.getValue() : null);
	}

	public Byte setVarious(VariousMetadata variousMetadata)
	{
		return updateOrRemove(INDEX_VARIOUS, variousMetadata != null ? variousMetadata.getFullValue() : null);
	}

	public VariousMetadata getVarious()
	{
		Byte value = get(INDEX_VARIOUS);

		return value != null ? new VariousMetadata(value) : null;
	}

	public VariousMetadata getVariousOrCreate()
	{
		Byte value = get(INDEX_VARIOUS);

		return value != null ? new VariousMetadata(value) : new VariousMetadata();
	}

	public Short setAirLevel(Short airLevel)
	{
		return updateOrRemove(INDEX_AIRLEVEL, airLevel);
	}

	public Short getAirLevel()
	{
		return get(INDEX_AIRLEVEL);
	}

	public String setNameTag(String nameTag)
	{
		return updateOrRemove(INDEX_NAMETAG, nameTag);
	}

	public String getNameTag()
	{
		return get(INDEX_NAMETAG);
	}

	public boolean setAlwaysShowNameTag(boolean alwaysShowNameTag)
	{
		return setBit(INDEX_ALWAYSSHOWNAMETAG, 0, alwaysShowNameTag);
	}

	public boolean isAlwaysShowNameTag()
	{
		return getBit(INDEX_ALWAYSSHOWNAMETAG, 0);
	}

	public Float setHealth(Float health)
	{
		return updateOrRemove(INDEX_HEALTH, health);
	}

	public Float getHealth()
	{
		return get(INDEX_HEALTH);
	}

	public Float getFloat()
	{
		return get(INDEX_HEALTH);
	}

	public Integer setPotionEffectColorRGB(Integer rgb)
	{
		return updateOrRemove(INDEX_POTIONEFFECTCOLOR, rgb);
	}

	public Integer getPotionEffectColorRGB()
	{
		return get(INDEX_POTIONEFFECTCOLOR);
	}

	public boolean setPotionEffectAmbient(boolean potionEffectAmbient)
	{
		return setBit(INDEX_POTIONEFFECTAMBIENT, 0, potionEffectAmbient);
	}

	public boolean isPotionEffectAmbient()
	{
		return getBit(INDEX_POTIONEFFECTAMBIENT, 0);
	}

	public Byte setNumberOfArrowsInEntity(Byte number)
	{
		return updateOrRemove(INDEX_NUMBEROFARROWSINENTITY, number);
	}

	public Byte getNumberOfArrowsInEntity()
	{
		return get(INDEX_NUMBEROFARROWSINENTITY);
	}

	public Byte setSkinFlags(Byte skinFlags)
	{
		ProtocolData.HiddenByte newHiddenByte = skinFlags != null ? new ProtocolData.HiddenByte(skinFlags) : null;
		ProtocolData.HiddenByte resultHiddenByte = updateOrRemove(INDEX_SKINFLAGS, newHiddenByte);

		return resultHiddenByte != null ? resultHiddenByte.byteValue() : null;
	}

	public Byte getSkinFlags()
	{
		ProtocolData.HiddenByte hiddenByte = get(INDEX_SKINFLAGS);

		return hiddenByte != null ? hiddenByte.byteValue() : null;
	}

	public boolean setMissingAI(boolean missingAI)
	{
		return setBit(INDEX_MISSINGAI, 0, missingAI);
	}

	public boolean isMissingAI()
	{
		return getBit(INDEX_MISSINGAI, 0);
	}

	public boolean setHideCape(boolean hideCape)
	{
		return setBitDual(INDEX_HIDECAPE, BIT_HIDECAPE_HIDECAPE, hideCape);
	}

	public boolean isHideCape()
	{
		return getBitDual(INDEX_HIDECAPE, BIT_HIDECAPE_HIDECAPE);
	}

	public Float setAbsorptionHearts(Float absorptionHearts)
	{
		return updateOrRemove(INDEX_ABSORPTIONHEARTS, absorptionHearts);
	}

	public Float getAbsorptionHearts()
	{
		return get(INDEX_ABSORPTIONHEARTS);
	}

	public Integer setScore(Integer score)
	{
		return updateOrRemove(INDEX_SCORE, score);
	}

	public Integer getScore()
	{
		return get(INDEX_SCORE);
	}

	public void debug()
	{
		for(WrappedWatchableObject object : dataMap.values())
			MineStrike.warn(object.toString());

		MineStrike.debug("PlayerMetadataPacketEvent[viewer=" + getPlayer().getName() + ", target=" + getMSTarget().getName() + "] {");
		MineStrike.debug("  various: " + getVarious());
		MineStrike.debug("  airLevel: " + getAirLevel());
		MineStrike.debug("  nameTag: " + getNameTag());
		MineStrike.debug("  alwaysShowNameTag: " + isAlwaysShowNameTag());
		MineStrike.debug("  health: " + getHealth());
		MineStrike.debug("  potionEffectColor: " + (getPotionEffectColorRGB() != null ? ("#" + Integer.toHexString(getPotionEffectColorRGB())) : "null"));
		MineStrike.debug("  potionEffectAmbient: " + isPotionEffectAmbient());
		MineStrike.debug("  numberOfArrowsInEntity: " + getNumberOfArrowsInEntity());
		MineStrike.debug("  skinFlags: " + getSkinFlags());
		MineStrike.debug("  missingAI: " + isMissingAI());
		MineStrike.debug("  hideCape: " + isHideCape());
		MineStrike.debug("  absorptionHearts: " + getAbsorptionHearts());
		MineStrike.debug("  score: " + getScore());
		MineStrike.debug("}");
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
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

	public static class InvalidPlayerIdException extends RuntimeException
	{
		public InvalidPlayerIdException(String message)
		{
			super(message);
		}
	}

	public static class VariousMetadata
	{
		private byte fullValue;

		public VariousMetadata(byte fullValue)
		{
			this.fullValue = fullValue;
		}

		public VariousMetadata()
		{
			this((byte) 0);
		}

		public boolean setOnFire(boolean value)
		{
			return setBit(BIT_VARIOUS_ONFIRE, value);
		}

		public boolean isOnFire()
		{
			return getBit(BIT_VARIOUS_ONFIRE);
		}

		public boolean setSneaking(boolean value)
		{
			return setBit(BIT_VARIOUS_CROUCHED, value);
		}

		public boolean isSneaking()
		{
			return getBit(BIT_VARIOUS_CROUCHED);
		}

		public boolean setSprinting(boolean value)
		{
			return setBit(BIT_VARIOUS_SPRINTING, value);
		}

		public boolean isSprinting()
		{
			return getBit(BIT_VARIOUS_SPRINTING);
		}

		public boolean setUsingItem(boolean value)
		{
			return setBit(BIT_VARIOUS_USINGITEM, value);
		}

		public boolean isUsingItem()
		{
			return getBit(BIT_VARIOUS_USINGITEM);
		}

		public boolean setInvisible(boolean value)
		{
			return setBit(BIT_VARIOUS_INVISIBLE, value);
		}

		public boolean isInvisible()
		{
			return getBit(BIT_VARIOUS_INVISIBLE);
		}

		private boolean setBit(int bitPosition, boolean value)
		{
			boolean previousValue = getBit(bitPosition);
			fullValue = (byte) (value ? fullValue  | (1 << bitPosition)
			                          : fullValue & ~(1 << bitPosition));

			return previousValue;
		}

		private boolean getBit(int bitPosition)
		{
			return ((fullValue >> bitPosition) & 1) == 1;
		}

		public byte getFullValue()
		{
			return fullValue;
		}

		@Override
		public String toString()
		{
			return "VariousMetadata[" + fullValue + "] {onFire=" + isOnFire() + ", sneaking=" + isSneaking()
			       + ", sprinting=" + isSprinting() + ", usingItem=" + isUsingItem() + ", invisible=" + isInvisible() + "}";
		}
	}
}
