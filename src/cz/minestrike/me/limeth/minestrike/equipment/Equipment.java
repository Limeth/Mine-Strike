package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

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
	
	/**
	 * @return Whether it should add the item to the player's inventory
	 */
	public boolean purchase(MSPlayer msPlayer);
}
