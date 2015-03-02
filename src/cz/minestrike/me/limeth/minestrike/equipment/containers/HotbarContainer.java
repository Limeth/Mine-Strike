package cz.minestrike.me.limeth.minestrike.equipment.containers;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

	public Equipment getHeld(Player player)
	{
		return getHeld(MSPlayer.get(player));
	}
	
	public Equipment getHeld(MSPlayer msPlayer)
	{
		return getHeld(msPlayer.getHeldItemSlot());
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
	
	@Override
	public Iterator<Equipment> iterator()
	{
		return new Iterator<Equipment>() {
			private Integer index;
			
			@Override
			public Equipment next()
			{
				for(int i = index == null ? 0 : (index + 1); i < contents.length; i++)
					if(contents[i] != null)
					{
						index = i;
						return contents[i];
					}
				
				throw new NoSuchElementException();
			}
			
			@Override
			public boolean hasNext()
			{
				for(int i = index == null ? 0 : (index + 1); i < contents.length; i++)
					if(contents[i] != null)
						return true;
				
				return false;
			}
			
			@Override
			public void remove()
			{
				if(index == null)
					throw new NoSuchElementException();
				
				contents[index] = null;
			}
		};
	}

	@Override
	public List<String> generateComparison(Container other)
	{
		return Container.generateComparsion(this, other);
	}
}
