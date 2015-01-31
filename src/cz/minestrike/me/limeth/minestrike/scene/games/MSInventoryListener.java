package cz.minestrike.me.limeth.minestrike.scene.games;

import net.minecraft.server.v1_7_R4.PacketPlayOutCollect;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftItem;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.events.EquipmentDropEvent;
import cz.minestrike.me.limeth.minestrike.events.EquipmentPickupEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

public class MSInventoryListener extends MSSceneListener<Game>
{
	public MSInventoryListener(Game game)
	{
		super(game);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick(InventoryClickEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		Game game = getScene();
		
		event.setCancelled(true);
		PlayerUtil.delayedInventoryUpdate(player, MineStrike.getInstance());
		
		if(game.isPlayerPlaying(msPlayer))
		{
			SlotType slotType = event.getSlotType();
			
			if(slotType == SlotType.QUICKBAR)
			{
				dropItem(event, msPlayer);
				return;
			}
		}
		
		InventoryView view = event.getView();
		Inventory bottomInv = view.getBottomInventory();
		
		if(!(bottomInv instanceof CraftInventoryPlayer))
			return;
		
		ItemStack item = event.getCurrentItem();
		
		if(item == null)
			return;
		
		PlayerState state = msPlayer.getPlayerState();
		
		if(state == PlayerState.JOINED_GAME)
			if(item.equals(MSConstant.QUIT_SERVER_ITEM))
			{
				msPlayer.quitScene(SceneQuitReason.LEAVE, true);
			}
			else if(item.equals(MSConstant.QUIT_MENU_ITEM))
			{
				game.quitArena(msPlayer);
			}
	}
	
	public void dropItem(InventoryClickEvent event, MSPlayer msPlayer)
	{
		int slot = event.getSlot();
		int hotbarSlot = slot % MSConstant.INVENTORY_WIDTH;
		HotbarContainer container = msPlayer.getHotbarContainer();
		Equipment equipment = container.getItem(hotbarSlot);
		
		if(equipment == null || !equipment.isDroppableManually())
			return;
		
		Game game = getScene();
		EquipmentDropEvent equipmentEvent = new EquipmentDropEvent(msPlayer, game, equipment);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(equipmentEvent);
		
		if(equipmentEvent.isCancelled())
			return;
		
		game.drop(equipment, msPlayer, true);
		container.setItem(hotbarSlot, null);
		container.apply(msPlayer);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event, MSPlayer msPlayer)
	{
		Game game = getScene();
		Item item = event.getItem();
		EquipmentProvider provider = game.getEquipmentProvider();
		Equipment equipment = game.getDrop(item);
		
		event.setCancelled(true);
		
		if(equipment == null)
			return;
		
		EquipmentPickupEvent equipmentEvent = new EquipmentPickupEvent(msPlayer, game, equipment, item);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(equipmentEvent);
		
		if(equipmentEvent.isCancelled())
			return;
		
		boolean success = provider.pickup(msPlayer, equipment);
		
		if(!success)
			return;
		
		Player player = msPlayer.getPlayer();
		
		game.removeDrop(item);
		playPickupAnimation(player, item);
	}
	
	private void playPickupAnimation(Player player, Item item)
	{
		int playerId = ((CraftPlayer) player).getEntityId();
		int itemId = ((CraftItem) item).getEntityId();
		PacketPlayOutCollect packet = new PacketPlayOutCollect(itemId, playerId);
		Game game = getScene();
		
		for(Player currentPlayer : game.getBukkitPlayers())
			((CraftPlayer) currentPlayer).getHandle().playerConnection.sendPacket(packet);
	}
}
