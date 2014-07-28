package cz.minestrike.me.limeth.minestrike.equipment.containers;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

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
	
	@Override
	public boolean apply(Inventory inv, MSPlayer msPlayer, Equipment equipment)
	{
		Validate.notNull(equipment, "The equipment must not be null!");
		boolean found = false;
		
		for(int i = 0; i < size(); i++)
		{
			Equipment current = get(i);
			
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
