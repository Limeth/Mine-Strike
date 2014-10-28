package cz.minestrike.me.limeth.minestrike.scene.lobby;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.ClickSound;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;

public class MSLobbyInventoryListener extends MSSceneListener<Lobby>
{
	public MSLobbyInventoryListener(Lobby scene)
	{
		super(scene);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event, MSPlayer msPlayer)
	{
		InventoryView view = event.getView();
		int rawSlot = event.getRawSlot();
		int bottomSlot = PlayerUtil.getBottomInventoryIndex(view, rawSlot);
		
		event.setCancelled(true);
		
		if(bottomSlot >= 0)
		{
			int x = PlayerUtil.getInventoryX(bottomSlot);
			int y = PlayerUtil.getInventoryY(bottomSlot);
			
			if(y != 1)
				return;
			else if(x < 1 || x >= MSConstant.INVENTORY_WIDTH - 1)
				return;
			
			LobbyButton button = LobbyButton.get(x - 1);
			
			if(button == null)
				return;
			
			Inventory bottomInv = view.getBottomInventory();
			
			button.onClick(bottomInv, msPlayer);
			return;
		}
		
		Inventory topInv = view.getTopInventory();
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		
		invContainer.onClick(topInv, rawSlot, msPlayer);
	}
	
	public static enum LobbyButton implements ItemButton
	{
		INVENTORY
		{
			@Override
			public ItemStack newItemStack()
			{
				ItemStack buttonInventory = new ItemStack(Material.CHEST);
				ItemMeta buttonInventoryIM = buttonInventory.getItemMeta();
				
				buttonInventoryIM.setDisplayName(Translation.BUTTON_INVENTORY_USE.getMessage());
				buttonInventory.setItemMeta(buttonInventoryIM);
				
				return buttonInventory;
			}

			@Override
			public void onClick(Inventory inv, MSPlayer msPlayer)
			{
				SoundManager.play(ClickSound.DEFAULT.getAbsolouteName(), msPlayer.getPlayer());
				InventoryContainer.openInventory(msPlayer, true);
			}
		};
		
		public static LobbyButton get(int index)
		{
			try
			{
				return values()[index];
			}
			catch(Exception e)
			{
				return null;
			}
		}
	}
}
