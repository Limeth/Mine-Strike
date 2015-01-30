package cz.minestrike.me.limeth.minestrike.scene.games;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.events.ArenaDeathEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameStartEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
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

	public abstract Optional<Equipment> getReward(MSPlayer msPlayer);

	public void rewardPlayers()
	{
		Game game = getScene();

		game.getPlayingPlayers(this::meetsRequirements).forEach(this::rewardPlayer);
		joinedAt.clear();
		kills.clear();
	}

	private void rewardPlayer(MSPlayer msPlayer)
	{
		Optional<Equipment> reward = getReward(msPlayer);

		if(!reward.isPresent())
			return;

		Game game = getScene();
		Player player = msPlayer.getPlayer();
		String nameTag = msPlayer.getNameTag();
		PlayerInventory playerInventory = player.getInventory();
		ItemStack rewardItemStack = reward.get().newItemStack(msPlayer);
		String rewardName = reward.get().getDisplayName();
		InventoryContainer inventoryContainer = msPlayer.getInventoryContainer();

		inventoryContainer.addItem(reward.get());
		msPlayer.clearInventory();
		playerInventory.setItem(9 + 4, rewardItemStack);
		msPlayer.updateInventory();
		openHopperInventory(player);
		game.broadcast(Translation.REWARD_GIVEN.getMessage(nameTag, rewardName));
	}

	private static void openHopperInventory(Player player)
	{
		Inventory inventory = Bukkit.createInventory(player, InventoryType.HOPPER);

		player.openInventory(inventory);
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

		if(playerKills == null)
			playerKills = 0;

		return playerKills >= requiredKills;
	}

	@EventHandler
	public void onArenaDeath(ArenaDeathEvent event, MSPlayer msPlayer)
	{
		MSPlayer damageSource = msPlayer.getLastDamageSource();
		Equipment damageSourceWeapon = msPlayer.getLastDamageWeapon();

		if(damageSource == null || damageSourceWeapon == null)
			return;

		Player damageSourcePlayer = damageSource.getPlayer();
		String damageSourcePlayerName = damageSourcePlayer.getName();
		Integer foundKills = kills.get(damageSourcePlayerName);
		int newKills = foundKills != null ? foundKills + 1 : 1;

		kills.put(damageSourcePlayerName, newKills);
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
	public void onInventoryClose(InventoryCloseEvent event, MSPlayer msPlayer)
	{
		Inventory inventory = event.getInventory();
		InventoryType inventoryType = inventory.getType();

		if(inventoryType == InventoryType.HOPPER)
			getScene().equip(msPlayer, false);
	}

	@EventHandler
	public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
	{
		resetPlayerStats(msPlayer);
	}

	@EventHandler
	public void onArenaQuit(GameStartEvent event)
	{
		if(event.getGame() != getScene())
			return;

		getScene().getPlayers().forEach(this::resetPlayerStats);
	}

	public void resetPlayerStats(String playerName)
	{
		joinedAt.remove(playerName);
		kills.remove(playerName);
	}

	public void resetPlayerStats(MSPlayer msPlayer)
	{
		resetPlayerStats(msPlayer.getPlayer().getName());
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
