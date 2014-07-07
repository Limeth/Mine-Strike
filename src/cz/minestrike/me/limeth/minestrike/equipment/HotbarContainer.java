package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.Inventory;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;

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
			inv.setItem(i, contents[i].newItemStack(msPlayer));
	}
	
	@Override
	public void apply(MSPlayer msPlayer)
	{
		apply(msPlayer.getPlayer().getInventory(), msPlayer);
	}
}
