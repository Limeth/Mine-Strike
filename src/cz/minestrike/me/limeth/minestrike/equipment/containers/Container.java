package cz.minestrike.me.limeth.minestrike.equipment.containers;

import org.bukkit.inventory.Inventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

public interface Container extends Iterable<Equipment>
{
	public int getSize();
	public void clear();
	public Equipment[] getContents();
	public void setItem(int index, Equipment equipment);
	public Equipment getItem(int index);
	public void apply(Inventory inv, MSPlayer msPlayer);
	public void apply(MSPlayer msPlayer);
	public boolean apply(Inventory inv, MSPlayer msPlayer, Equipment equipment);
	public boolean apply(MSPlayer msPlayer, Equipment equipment);
}
