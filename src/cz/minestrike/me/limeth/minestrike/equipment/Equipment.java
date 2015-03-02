package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public interface Equipment
{
	String getId();
	Class<? extends Equipment> getEquipmentClass();
	Equipment getSource();
	
	/**
	 * @return setCancelled
	 */
	boolean rightClick(MSPlayer msPlayer, Block clickedBlock);
	
	/**
	 * @return setCancelled
	 */
	boolean leftClick(MSPlayer msPlayer, Block clickedBlock);
	ItemStack newItemStack(MSPlayer msPlayer);
	String getDisplayName();
	String getSoundDrawing();
	Integer getPrice(MSPlayer msPlayer);
	float getMovementSpeed(MSPlayer msPlayer);
	FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer);
	
	/**
	 * @return Whether it should add the item to the player's inventory
	 */
	boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException;
	boolean isDroppedOnDeath();
	boolean isDroppableManually();
	String getDefaultSkin(MSPlayer msPlayer);
	void onSelect(MSPlayer msPlayer);
	void onDeselect(MSPlayer msPlayer);
	EquipmentCategory getCategory();
	boolean isTradable();
}
