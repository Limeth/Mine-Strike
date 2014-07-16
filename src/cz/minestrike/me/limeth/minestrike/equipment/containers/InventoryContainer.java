package cz.minestrike.me.limeth.minestrike.equipment.containers;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategoryEntry;

public class InventoryContainer extends ScalableContainer
{
	private static final long serialVersionUID = -8525702276639421368L;
	
	public CustomizedEquipment<? extends Equipment> getEquippedCustomizedEquipment(EquipmentCategoryEntry categoryEntry) throws NotFoundException
	{
		Validate.notNull(categoryEntry, "The category entry must not be null!");
		
		for(Equipment equipment : this)
		{
			if(!(equipment instanceof CustomizedEquipment))
				continue;
			
			if(!categoryEntry.getEquipment().contains(equipment.getSource()))
				continue;
			
			@SuppressWarnings("unchecked")
			CustomizedEquipment<? extends Equipment> customEquipment = (CustomizedEquipment<? extends Equipment>) equipment;
			
			if(!customEquipment.isEquipped())
				continue;
			
			return customEquipment;
		}
		
		throw new NotFoundException("CustomizedEquipment for category entry " + categoryEntry + " not found.");
	}
	
	public Equipment getEquippedEquipment(EquipmentCategoryEntry categoryEntry)
	{
		CustomizedEquipment<? extends Equipment> customized;
		
		try
		{
			customized = getEquippedCustomizedEquipment(categoryEntry);
		}
		catch(Exception e)
		{
			return categoryEntry.getDefaultEquipment();
		}
		
		return customized;
	}
	
	private static final int inventoryHeight = 5, inventoryWidth = MSConstant.INVENTORY_WIDTH - 1;
	
	public static InventoryView openInventory(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		Inventory inv = Bukkit.createInventory(player, 9 * inventoryHeight, MSConstant.INVENTORY_NAME_PREFIX + Translation.INVENTORY.getMessage());
		
		equipInventory(inv, msPlayer);
		
		return player.openInventory(inv);
	}
	
	public static void equipInventory(Inventory inv, MSPlayer msPlayer)
	{
		InventoryContainer container = msPlayer.getInventoryContainer();
		Equipment[] contents = container.getContents();
		int scroll = msPlayer.getCustomData(Integer.class, "container.inventory.scroll", 0) * inventoryWidth;
		int index = 0;
		
		do
		{
			if(index + scroll >= contents.length)
				break;
			else if(index / inventoryWidth >= inventoryHeight)
				break;
			
			int x = index % inventoryWidth;
			int y = index / inventoryWidth;
			int inventoryIndex = x + y * MSConstant.INVENTORY_WIDTH;
			Equipment equipment = contents[index + scroll];
			ItemStack item = equipment.newItemStack(msPlayer);
			
			inv.setItem(inventoryIndex, item);
			
			index++;
		}
		while(true);
		
		for(int i = 1; i < inventoryHeight - 1; i++)
			inv.setItem(inventoryWidth + i * MSConstant.INVENTORY_WIDTH, MSConstant.ITEM_BACKGROUND);
		
		ItemStack up = new ItemStack(MSConstant.MATERIAL_ARROW_UP);
		ItemStack down = new ItemStack(MSConstant.MATERIAL_ARROW_DOWN);
		ItemMeta imUp = up.getItemMeta();
		ItemMeta imDown = down.getItemMeta();
		
		imUp.setDisplayName("");
		imDown.setDisplayName("");
		up.setItemMeta(imUp);
		down.setItemMeta(imDown);
		inv.setItem(inventoryWidth, up);
		inv.setItem((inventoryHeight - 1) * MSConstant.INVENTORY_WIDTH + inventoryWidth, down);
	}
}
