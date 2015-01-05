package cz.minestrike.me.limeth.minestrike.equipment.containers;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

public class ScalableContainer implements Container
{
	private ArrayList<Equipment> contents = Lists.newArrayList();

	@Override
	public int getSize()
	{
		return contents.size();
	}

	@Override
	public Equipment[] getContents()
	{
		return contents.toArray(new Equipment[contents.size()]);
	}
	
	public void addItem(int index, Equipment equipment)
	{
		contents.add(index, equipment);
	}
	
	public void addItem(Equipment equipment)
	{
		contents.add(equipment);
	}

	@Override
	public void setItem(int index, Equipment equipment)
	{
		if(index >= contents.size())
		{
			while(index > contents.size())
				contents.add(contents.size(), null);
			
			contents.add(index, equipment);
		}
		
		contents.set(index, equipment);
	}

	@Override
	public Equipment getItem(int index)
	{
		return contents.get(index);
	}

	public boolean contains(Equipment equipment)
	{
		return contents.contains(equipment);
	}
	
	public boolean remove(Equipment equipment)
	{
		return contents.remove(equipment);
	}
	
	@Override
	public void apply(Inventory inv, MSPlayer msPlayer)
	{
		for(int i = 0; i < contents.size() && i < inv.getSize(); i++)
		{
			Equipment equipment = contents.get(i);
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
		
		for(int i = 0; i < contents.size(); i++)
		{
			Equipment current = contents.get(i);
			
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

	@Override
	public Iterator<Equipment> iterator()
	{
		return contents.iterator();
	}

	@Override
	public void clear()
	{
		contents.clear();
	}
}
