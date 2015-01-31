package cz.minestrike.me.limeth.minestrike.scene.games;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSection;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentSectionEntry;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.events.ShopOpenEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class MSShoppingListener extends MSSceneListener<Game>
{
	public MSShoppingListener(Game game)
	{
		super(game);
	}

	@EventHandler
	public void onGameEquip(GameEquipEvent event, MSPlayer msPlayer)
	{
		Game game = getScene();

		if(!game.isWeaponEquippable(msPlayer))
			return;

		Player player = msPlayer.getPlayer();
		EquipmentProvider equipmentProvider = game.getEquipmentProvider();
		PlayerInventory inv = player.getInventory();
		FilledArrayList<EquipmentSection> categories = equipmentProvider.getEquipmentCategories();

		PlayerUtil.setItem(inv, 1, 1, MSConstant.QUIT_SERVER_ITEM);
		PlayerUtil.setItem(inv, 2, 1, MSConstant.QUIT_MENU_ITEM);

		for(int i = 0; i < categories.size(); i++)
		{
			EquipmentSection category = categories.get(i);
			ItemStack icon = category.getIcon();
			int x = 6 + i % 2;
			int y = i / 2;

			PlayerUtil.setItem(inv, x, y, icon);
		}
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
		
		Game game = getScene();
		Inventory topInv = view.getTopInventory();
		int slot = event.getRawSlot();
		EquipmentProvider ep = game.getEquipmentProvider();
		FilledArrayList<EquipmentSection> categories = ep.getEquipmentCategories();
		EquipmentSection openCat = EquipmentSection.getByInventory(categories, topInv);
		
		if(openCat != null && slot < topInv.getSize())
		{
			EquipmentSectionEntry entry = openCat.getEntry(msPlayer, slot);
			
			if(entry == null)
				return;
			
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			Equipment equipment = invContainer.getEquippedEquipment(entry);
			EquipmentProvider em = game.getEquipmentProvider();
			
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
			slot = PlayerUtil.getBottomInventoryIndex(view, slot);
			int y = PlayerUtil.getInventoryY(slot);
			int x = PlayerUtil.getInventoryX(slot);
			
			if(x < 6 || x > 7)
				return;
			
			int index = (x - 6) + y * 2;
			
			if(index < 0)
				return;
			
			if(index >= categories.size())
				return;
			
			EquipmentSection cat = categories.get(index);
			
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
