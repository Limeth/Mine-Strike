package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public interface Equipment
{
	public String getId();
	public Class<? extends Equipment> getEquipmentClass();
	public Equipment getSource();
	
	/**
	 * @return setCancelled
	 */
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock);
	
	/**
	 * @return setCancelled
	 */
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock);
	public ItemStack newItemStack(MSPlayer msPlayer);
	public String getDisplayName();
	public String getSoundDraw();
	public Integer getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer);
	
	/**
	 * @return Whether it should add the item to the player's inventory
	 */
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException;
	public boolean isDroppedOnDeath();
	public boolean isDroppableManually();
	public String getDefaultSkin(MSPlayer msPlayer);
}
