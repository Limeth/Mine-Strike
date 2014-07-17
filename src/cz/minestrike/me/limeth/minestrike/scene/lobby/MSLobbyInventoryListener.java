package cz.minestrike.me.limeth.minestrike.scene.lobby;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

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
			
			button.onClick(msPlayer);
		}
	}
	
	public static enum LobbyButton
	{
		INVENTORY
		{
			@Override
			public ItemStack newItemStack()
			{
				ItemStack buttonInventory = new ItemStack(Material.CHEST);
				ItemMeta buttonInventoryIM = buttonInventory.getItemMeta();
				
				buttonInventoryIM.setDisplayName(Translation.BUTTON_INVENTORY.getMessage());
				buttonInventory.setItemMeta(buttonInventoryIM);
				
				return buttonInventory;
			}

			@Override
			public void onClick(MSPlayer msPlayer)
			{
				InventoryContainer.openInventory(msPlayer);
			}
		};
		
		public abstract ItemStack newItemStack();
		public abstract void onClick(MSPlayer msPlayer);
		
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
