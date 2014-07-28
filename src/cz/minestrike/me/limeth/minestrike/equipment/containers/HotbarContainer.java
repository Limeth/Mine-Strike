package cz.minestrike.me.limeth.minestrike.equipment.containers;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

public class HotbarContainer implements Container
{
	private final Equipment[] contents = new Equipment[MSConstant.INVENTORY_WIDTH];
	
	@Override
	public int getSize()
	{
		return contents.length;
	}

	@Override
	public Equipment[] getContents()
	{
		return contents;
	}
	
	@Override
	public void clear()
	{
		for(int i = 0; i < contents.length; i++)
			contents[i] = null;
	}
	
	public Equipment getHeld(MSPlayer msPlayer)
	{
		return getHeld(msPlayer.getPlayer());
	}
	
	public Equipment getHeld(Player player)
	{
		return getHeld(player.getInventory());
	}
	
	public Equipment getHeld(PlayerInventory inv)
	{
		return getHeld(inv.getHeldItemSlot());
	}
	
	public Equipment getHeld(int slot)
	{
		return contents[slot];
	}
	
	@Override
	public void setItem(int index, Equipment equipment)
	{
		contents[index] = equipment;
	}
	
	@Override
	public Equipment getItem(int index)
	{
		return contents[index];
	}

	@Override
	public void apply(Inventory inv, MSPlayer msPlayer)
	{
		for(int i = 0; i < contents.length && i < inv.getSize(); i++)
			inv.setItem(i, contents[i] == null ? null : contents[i].newItemStack(msPlayer));
	}
	
	@Override
	public void apply(MSPlayer msPlayer)
	{
		apply(msPlayer.getPlayer().getInventory(), msPlayer);
	}
	
	@Override
	public boolean apply(Inventory inv, MSPlayer msPlayer, Equipment equipment)
	{
		Validate.notNull(equipment, "The equipment must not be null!");
		boolean found = false;
		
		for(int i = 0; i < contents.length; i++)
		{
			Equipment current = contents[i];
			
			if(current != equipment)
				continue;
			
			found = true;
			ItemStack is = equipment.newItemStack(msPlayer);
			
			inv.setItem(i, is);
		}
		
		return found;
	}

	@Override
	public boolean apply(MSPlayer msPlayer, Equipment equipment)
	{
		return apply(msPlayer.getPlayer().getInventory(), msPlayer, equipment);
	}
}
