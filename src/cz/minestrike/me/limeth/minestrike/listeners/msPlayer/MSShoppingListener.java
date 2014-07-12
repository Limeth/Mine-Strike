package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategoryEntry;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.events.ShopOpenEvent;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class MSShoppingListener<T extends Game<?, ?, ?, ?>> extends MSGameListener<T>
{
	public MSShoppingListener(T game)
	{
		super(game);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		
		event.setCancelled(true);
		PlayerUtil.delayedInventoryUpdate(player, MineStrike.getInstance());
		
		InventoryView view = event.getView();
		Inventory bottomInv = view.getBottomInventory();
		
		if(!(bottomInv instanceof CraftInventoryPlayer))
			return;
		
		ItemStack item = event.getCurrentItem();
		
		if(item == null)
			return;
		
		PlayerState state = msPlayer.getPlayerState();
		
		if(state != PlayerState.JOINED_GAME)
			return;
		
		Game<?, ?, ?, ?> game = getGame();
		Inventory topInv = view.getTopInventory();
		int slot = event.getRawSlot();
		FilledArrayList<EquipmentCategory> categories = game.getEquipmentCategories();
		EquipmentCategory openCat = EquipmentCategory.getByInventory(categories, topInv);
		
		if(openCat != null && slot < topInv.getSize())
		{
			EquipmentCategoryEntry entry = openCat.getEntry(msPlayer, slot);
			
			if(entry == null)
				return;
			
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			Equipment equipment = invContainer.getEquippedEquipment(entry);
			EquipmentProvider em = game.getEquipmentManager();
			
			try
			{
				em.purchase(msPlayer, equipment);
				player.sendMessage(Translation.GAME_SHOP_PURCHASED.getMessage(equipment.getDisplayName()));
			}
			catch(EquipmentPurchaseException e)
			{
				Throwable cause = e.getCause();
				
				if(cause != null)
					e.printStackTrace();
				else
					player.sendMessage(e.getMessage());
			}
		}
		else
		{
			if(topInv instanceof CraftInventoryCrafting)
				slot -= MSConstant.INVENTORY_WIDTH;
			else
				slot -= topInv.getSize();
			
			int y = slot / MSConstant.INVENTORY_WIDTH;
			int x = slot % MSConstant.INVENTORY_WIDTH;
			
			if(x < 6 || x > 7)
				return;
			
			int index = (x - 6) + y * 2;
			
			if(index < 0)
				return;
			
			if(index >= categories.size())
				return;
			
			EquipmentCategory cat = categories.get(index);
			
			if(cat == null)
				return;
			
			ShopOpenEvent soe = new ShopOpenEvent(msPlayer, cat);
			PluginManager pm = Bukkit.getPluginManager();
			
			pm.callEvent(soe);
			
			if(soe.isCancelled())
				return;
			
			cat = soe.getCategory();
			
			cat.openInventory(msPlayer);
		}
	}
}
