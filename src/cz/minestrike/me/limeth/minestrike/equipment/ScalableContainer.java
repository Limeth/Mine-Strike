package cz.minestrike.me.limeth.minestrike.equipment;

import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public class ScalableContainer extends ArrayList<Equipment> implements Container
{
	private static final long serialVersionUID = -3664492331537095431L;

	@Override
	public int getSize()
	{
		return size();
	}

	@Override
	public Equipment[] getContents()
	{
		return toArray(new Equipment[size()]);
	}

	@Override
	public void setItem(int index, Equipment equipment)
	{
		if(index >= size())
		{
			while(index > size())
				add(size(), null);
			
			add(index, equipment);
		}
		
		set(index, equipment);
	}

	@Override
	public Equipment getItem(int index)
	{
		return get(index);
	}

	@Override
	public void apply(Inventory inv, MSPlayer msPlayer)
	{
		for(int i = 0; i < size() && i < inv.getSize(); i++)
		{
			Equipment equipment = get(i);
			ItemStack item = equipment != null ? equipment.newItemStack(msPlayer) : null;
			
			inv.setItem(i, item);
		}
			
	}

	@Override
	public void apply(MSPlayer msPlayer)
	{
		apply(msPlayer.getPlayer().getInventory(), msPlayer);
	}
}
