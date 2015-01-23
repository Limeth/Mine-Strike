package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.events.ArenaDeathEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

/**
 * @author Limeth
 */
public abstract class MSRewardListener<T extends Game> extends MSSceneListener<T>
{
	private HashMap<String, Long> joinedAt = Maps.newHashMap();
	private HashMap<String, Integer> kills = Maps.newHashMap();
	private long                     requiredPlaytimeMillis;
	private int                      requiredKills;

	public MSRewardListener(T scene, long requiredPlaytimeMillis, int requiredKills)
	{
		super(scene);

		this.requiredPlaytimeMillis = requiredPlaytimeMillis;
		this.requiredKills = requiredKills;
	}

	public abstract Equipment getReward(MSPlayer msPlayer);

	public void rewardPlayers()
	{
		Game game = getScene();

		game.getPlayingPlayers(this::meetsRequirements).forEach(this::rewardPlayer);
		joinedAt.clear();
		kills.clear();
	}

	private void rewardPlayer(MSPlayer msPlayer)
	{
		Equipment reward = getReward(msPlayer);

		if(reward == null)
			return;

		Game game = getScene();
		Player player = msPlayer.getPlayer();
		String nameTag = msPlayer.getNameTag();
		PlayerInventory playerInventory = player.getInventory();
		ItemStack rewardItemStack = reward.newItemStack(msPlayer);
		String rewardName = reward.getDisplayName();
		InventoryContainer inventoryContainer = msPlayer.getInventoryContainer();

		inventoryContainer.addItem(reward);
		msPlayer.clearInventory();
		playerInventory.setItem(9 * 3, rewardItemStack);
		openHopperInventory(player);
		game.equip(msPlayer, false);
		game.broadcast(Translation.REWARD_GIVEN.getMessage(nameTag, rewardName));
	}

	private static void openHopperInventory(Player player)
	{
		int inventoryId = 0;
		int inventoryType = 9;
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(inventoryId, inventoryType, "Mine-Strike reward", 0, true);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public boolean meetsRequirements(MSPlayer msPlayer)
	{
		return meetsRequirements(msPlayer.getPlayer().getName());
	}

	public boolean meetsRequirements(String playerName)
	{
		Long joinTime = joinedAt.get(playerName);

		if(joinTime != null && System.currentTimeMillis() - requiredPlaytimeMillis < joinTime)
			return false;

		Integer playerKills = kills.get(playerName);

		return playerKills != null && playerKills >= requiredKills;
	}

	@EventHandler
	public void onArenaDeath(ArenaDeathEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		String playerName = player.getName();
		Integer foundKills = kills.get(playerName);
		int newKills = foundKills != null ? foundKills + 1 : 1;

		kills.put(playerName, newKills);
	}

	@EventHandler
	public void onArenaJoin(ArenaJoinEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		String playerName = player.getName();
		long time = System.currentTimeMillis();

		joinedAt.put(playerName, time);
	}

	@EventHandler
	public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		String playerName = player.getName();

		joinedAt.remove(playerName);
		kills.remove(playerName);
	}

	public long getRequiredPlaytimeMillis()
	{
		return requiredPlaytimeMillis;
	}

	public void setRequiredPlaytimeMillis(long requiredPlaytimeMillis)
	{
		this.requiredPlaytimeMillis = requiredPlaytimeMillis;
	}

	public int getRequiredKills()
	{
		return requiredKills;
	}

	public void setRequiredKills(int requiredKills)
	{
		this.requiredKills = requiredKills;
	}
}
