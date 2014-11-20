package cz.minestrike.me.limeth.minestrike.scene.games;

import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
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
				msPlayer.quitScene(SceneQuitReason.LEAVE, true);
			}
			else if(item.equals(MSConstant.QUIT_MENU_ITEM))
			{
				Game game = getScene();
				
				game.quitArena(msPlayer);
			}
	}
}
