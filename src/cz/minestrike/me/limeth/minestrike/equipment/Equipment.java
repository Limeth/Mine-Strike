package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public interface Equipment
{
	public String getId();
	public Class<? extends Equipment> getEquipmentClass();
	public Equipment getSource();
	public ItemStack newItemStack(MSPlayer msPlayer);
	public String getDisplayName();
	public String getSoundDraw();
	public Integer getPrice(MSPlayer msPlayer);
	public float getMovementSpeed(MSPlayer msPlayer);
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer);
	
	/**
	 * @return Whether it should add the item to the player's inventory
	 * @throws EquipmentPurchaseException 
	 */
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException;
}
