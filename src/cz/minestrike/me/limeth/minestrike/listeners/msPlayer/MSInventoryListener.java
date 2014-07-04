package cz.minestrike.me.limeth.minestrike.listeners.msPlayer;

import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.events.GameEquipEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

public class MSInventoryListener<T extends Game<?, ?, ?, ?>> extends MSGameListener<T>
{
	public MSInventoryListener(T game)
	{
		super(game);
	}
	
	@EventHandler
	public void onGameEquip(GameEquipEvent event, MSPlayer msPlayer)
	{
		Player player = event.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		for(int rel = 0; rel < PlayerUtil.INVENTORY_WIDTH * 3; rel++)
		{
			int abs = rel + PlayerUtil.INVENTORY_WIDTH;
			
			inv.setItem(abs, MSConstant.BACKGROUND_ITEM);
		}
		
		PlayerUtil.setItem(inv, 1, 1, MSConstant.QUIT_SERVER_ITEM);
		PlayerUtil.setItem(inv, 2, 1, MSConstant.QUIT_MENU_ITEM);
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
		
		if(state == PlayerState.JOINED_GAME)
			if(item.equals(MSConstant.QUIT_SERVER_ITEM))
			{
				getGame().quit(msPlayer, GameQuitReason.LEAVE, true);
			}
			else if(item.equals(MSConstant.QUIT_MENU_ITEM))
			{
				T game = getGame();
				MSPlayer owner = game.getOwner();
				
				if(owner != null && owner.equals(msPlayer))
					game.joinLobby(msPlayer);
				else
					game.joinMenu(msPlayer);
			}
	}
}
