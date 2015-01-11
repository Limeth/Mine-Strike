package cz.minestrike.me.limeth.minestrike.util;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_7_R4.conversations.ConversationTracker;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * @author Limeth
 */
public final class InjectedConversationTracker extends ConversationTracker
{
	private static final String CRAFTPLAYER_FIELD_CONVERSATION_TRACKER_NAME = "conversationTracker";
	private static final Field  CRAFTPLAYER_FIELD_CONVERSATION_TRACKER;
	private static final String ILLEGAL_ACCESS_MESSAGE = "Couldn't access the CraftPlayer#" +
	                                                     CRAFTPLAYER_FIELD_CONVERSATION_TRACKER_NAME + " field";

	static
	{
		Field conversationTrackerField = null;

		try
		{
			conversationTrackerField = CraftPlayer.class.getDeclaredField(CRAFTPLAYER_FIELD_CONVERSATION_TRACKER_NAME);

			if(!conversationTrackerField.isAccessible())
				conversationTrackerField.setAccessible(true);
		}
		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}

		CRAFTPLAYER_FIELD_CONVERSATION_TRACKER = conversationTrackerField;
	}

	private final        ConversationTracker tracker;
	private final        MSPlayer            msPlayer;

	private InjectedConversationTracker(ConversationTracker tracker, MSPlayer msPlayer)
	{
		Preconditions.checkArgument(!(tracker instanceof InjectedConversationTracker), "The field is already injected!");
		Preconditions.checkNotNull(msPlayer);

		this.msPlayer = msPlayer;
		this.tracker = tracker;
	}

	public static boolean isInjected(Player player)
	{
		try
		{
			return CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.get(player) instanceof InjectedConversationTracker;
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(ILLEGAL_ACCESS_MESSAGE, e);
		}
	}

	public static void inject(Player player, MSPlayer msPlayer)
	{
		try
		{
			ConversationTracker tracker = (ConversationTracker) CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.get(player);
			InjectedConversationTracker injectedTracker = new InjectedConversationTracker(tracker, msPlayer);
			CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.set(player, injectedTracker);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(ILLEGAL_ACCESS_MESSAGE, e);
		}
	}

	public static void eject(Player player)
	{
		try
		{
			InjectedConversationTracker injectedTracker = (InjectedConversationTracker) CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.get(player);
			CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.set(player, injectedTracker.tracker);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(ILLEGAL_ACCESS_MESSAGE, e);
		}
	}

	public static MSPlayer getMSPlayer(Player player)
	{
		try
		{
			return ((InjectedConversationTracker) CRAFTPLAYER_FIELD_CONVERSATION_TRACKER.get(player)).msPlayer;
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(ILLEGAL_ACCESS_MESSAGE, e);
		}
	}

	//Super methods

	@Override
	public synchronized boolean beginConversation(Conversation conversation)
	{
		return tracker.beginConversation(conversation);
	}

	@Override
	public synchronized void abandonConversation(Conversation conversation, ConversationAbandonedEvent details)
	{
		tracker.abandonConversation(conversation, details);
	}

	@Override
	public synchronized void abandonAllConversations()
	{
		tracker.abandonAllConversations();
	}

	@Override
	public synchronized void acceptConversationInput(String input)
	{
		tracker.acceptConversationInput(input);
	}

	@Override
	public synchronized boolean isConversing()
	{
		return tracker.isConversing();
	}

	@Override
	public synchronized boolean isConversingModaly()
	{
		return tracker.isConversingModaly();
	}
}
