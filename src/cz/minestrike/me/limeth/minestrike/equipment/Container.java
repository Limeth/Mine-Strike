package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.inventory.Inventory;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

public interface Container
{
	public int getSize();
	public Equipment<? extends EquipmentType>[] getContents();
	public void setItem(int index, Equipment<? extends EquipmentType> equipment);
	public Equipment<? extends EquipmentType> getItem(int index);
	public void apply(Inventory inv, MSPlayer msPlayer);
	public void apply(MSPlayer msPlayer);
}
