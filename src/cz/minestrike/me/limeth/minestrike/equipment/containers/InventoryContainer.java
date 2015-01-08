package cz.minestrike.me.limeth.minestrike.equipment.containers;

import com.avaje.ebeaninternal.server.lib.util.NotFoundException;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashSet;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class InventoryContainer extends ScalableContainer
{
	public static final Equipment[] DEFAULT_EQUIPMENT = {
		new Gun(GunType.USP_S), new Gun(GunType.CZ75), new Gun(GunType.M4A1_S)
	};

	public InventoryContainer(Collection<Equipment> equipment) {
		addAllItems(equipment);
	}

	public InventoryContainer() {}
	
	public Equipment getFirstBySource(Equipment source)
	{
		for(Equipment equipment : this)
			if(equipment != null && equipment.getSource().equals(source))
				return equipment;
		
		return null;
	}
	
	public CustomizedEquipment<? extends Equipment> getEquippedCustomizedEquipment(EquipmentSectionEntry categoryEntry) throws NotFoundException
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
		
		return null;
	}
	
	public CustomizedEquipment<? extends Equipment> getEquippedCustomizedEquipment(Equipment sourceEquipment) throws NotFoundException
	{
		Validate.notNull(sourceEquipment, "The source equipment must not be null!");
		
		for(Equipment equipment : this)
		{
			if(!(equipment instanceof CustomizedEquipment))
				continue;
			
			if(!sourceEquipment.equals(equipment.getSource()))
				continue;
			
			@SuppressWarnings("unchecked")
			CustomizedEquipment<? extends Equipment> customEquipment = (CustomizedEquipment<? extends Equipment>) equipment;
			
			if(!customEquipment.isEquipped())
				continue;
			
			return customEquipment;
		}
		
		return null;
	}
	
	public Equipment getEquippedEquipment(EquipmentSectionEntry categoryEntry)
	{
		CustomizedEquipment<? extends Equipment> customized = null;
		
		try
		{
			customized = getEquippedCustomizedEquipment(categoryEntry);
		}
		catch(Exception e) {}
		
		return customized != null ? customized : categoryEntry.getDefaultEquipment();
	}
	
	public InventoryContainer addDefaults()
	{
		for(Equipment equipment : DEFAULT_EQUIPMENT)
			if(!containsSource(equipment.getSource()))
				addItem(0, equipment);

		return this;
	}
	
	public boolean containsSource(Equipment source)
	{
		for(Equipment equipment : this)
			if(source.equals(equipment.getSource()))
				return true;
		
		return false;
	}
	
	public void unequip(Equipment source)
	{
		unequip(EquipmentSectionEntry.getContaining(source));
	}
	
	public void unequip(EquipmentSectionEntry entry)
	{
		FilledHashSet<Equipment> entryContents = entry.getEquipment();
		
		for(Equipment equipment : this)
			if(equipment != null && equipment instanceof CustomizedEquipment)
				for(Equipment entryEquipment : entryContents)
					if(equipment.getSource().equals(entryEquipment))
					{
						@SuppressWarnings("unchecked")
						CustomizedEquipment<? extends Equipment> ce = (CustomizedEquipment<? extends Equipment>) equipment;
						
						ce.setEquipped(false);
						break;
					}
	}
	
	private static final int inventoryHeight = 5, inventoryWidth = MSConstant.INVENTORY_WIDTH - 1;
	public static final String SCROLL_DATA = "container.inventory.scroll",
			SELECTION_INDEX_DATA = "container.inventory.selection.index";

	public static String getTitleInventory()
	{
		String title = MSConstant.INVENTORY_NAME_PREFIX + Translation.INVENTORY_TITLE.getMessage();
		
		if(title.length() > 32)
			title = title.substring(0, 32);
		
		return title;
	}
	
	public static String getTitleSelection()
	{
		String title = MSConstant.INVENTORY_NAME_PREFIX + Translation.INVENTORY_SELECTION_TITLE.getMessage();
		
		if(title.length() > 32)
			title = title.substring(0, 32);
		
		return title;
	}
	
	private static final ItemButton BUTTON_BACK = new ItemButton()
	{
		@Override
		public void onClick(Inventory inv, MSPlayer msPlayer)
		{
			SoundManager.play(ClickSound.BACK.getAbsolouteName(), msPlayer.getPlayer());
			openInventory(msPlayer, false);
		}
		
		@Override
		public ItemStack newItemStack()
		{
			ItemStack is = new ItemStack(MSConstant.MATERIAL_BACK);
			ItemMeta im = is.getItemMeta();
			
			im.setDisplayName(Translation.BUTTON_INVENTORY_BACK.getMessage());
			is.setItemMeta(im);
			
			return is;
		}
	};
	
	public static InventoryView openInventory(MSPlayer msPlayer, boolean initialize)
	{
		Player player = msPlayer.getPlayer();
		Inventory inv = Bukkit.createInventory(player, 9 * inventoryHeight, getTitleInventory());
		
		if(initialize)
			msPlayer.setCustomData(SCROLL_DATA, 0);
		
		equipInventory(inv, msPlayer);
		
		return player.openInventory(inv);
	}
	
	public static InventoryView openSelection(MSPlayer msPlayer, Integer selectionIndex)
	{
		Player player = msPlayer.getPlayer();
		Inventory inv = Bukkit.createInventory(player, 9 * inventoryHeight, getTitleSelection());
		
		if(selectionIndex != null)
			msPlayer.setCustomData(SELECTION_INDEX_DATA, selectionIndex);
		
		equipSelection(inv, msPlayer);
		
		return player.openInventory(inv);
	}
	
	public static void equipInventory(Inventory inv, MSPlayer msPlayer)
	{
		InventoryContainer container = msPlayer.getInventoryContainer();
		Equipment[] contents = container.getContents();
		int scroll = msPlayer.getCustomData(Integer.class, SCROLL_DATA, 0) * inventoryWidth;
		int index = 0;
		
		inv.clear();
		
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
	
	public static void equipSelection(Inventory inv, MSPlayer msPlayer)
	{
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		int selectionIndex = msPlayer.getCustomData(Integer.class, SELECTION_INDEX_DATA);
		Equipment selectedEquipment = invContainer.getItem(selectionIndex);
		FilledArrayList<ItemButton> selectionButtons = selectedEquipment.getSelectionButtons(msPlayer);
		
		selectionButtons.add(BUTTON_BACK);
		
		int startX = 4 - (int) (selectionButtons.size() / 2D);
		ItemStack selectedItem = selectedEquipment.newItemStack(msPlayer);
		
		for(int i = 0; i < inv.getSize(); i++)
			inv.setItem(i, MSConstant.ITEM_BACKGROUND);
		
		PlayerUtil.setItem(inv, 4, 1, selectedItem);
		
		for(int i = 0; i < selectionButtons.size(); i++)
		{
			ItemButton button = selectionButtons.get(i);
			ItemStack buttonItem = button.newItemStack();
			int x = startX + i;
			
			PlayerUtil.setItem(inv, x, 3, buttonItem);
		}
	}
	
	public void onClick(Inventory inv, int slot, MSPlayer msPlayer)
	{
		String title = inv.getTitle();
		
		if(title.equals(getTitleInventory()))
		{
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			
			invContainer.onClickInventory(inv, slot, msPlayer);
		}
		else if(title.equals(getTitleSelection()))
		{
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			
			invContainer.onClickSelection(inv, slot, msPlayer);
		}
	}
	
	public void onClickInventory(Inventory inv, int slot, MSPlayer msPlayer)
	{
		int x = PlayerUtil.getInventoryX(slot);
		
		if(x < inventoryWidth)
		{
			int y = PlayerUtil.getInventoryY(slot);
			int scroll = msPlayer.getCustomData(Integer.class, SCROLL_DATA, 0);
			int equipmentIndex = x + (y + scroll) * inventoryWidth;
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			
			if(equipmentIndex < 0 || equipmentIndex >= invContainer.getSize())
				return;
			
			Equipment equipment = invContainer.getItem(equipmentIndex);
			
			if(equipment == null)
				return;
			
			openSelection(msPlayer, equipmentIndex);
		}
		else
			if(slot == inventoryWidth)
			{
				int scroll = msPlayer.getCustomData(Integer.class, SCROLL_DATA, 0);
				
				if(scroll <= 0)
					return;
				
				msPlayer.setCustomData(SCROLL_DATA, scroll - 1);
				equipInventory(inv, msPlayer);
			}
			else if(slot == (inventoryHeight - 1) * MSConstant.INVENTORY_WIDTH + inventoryWidth)
			{
				int scroll = msPlayer.getCustomData(Integer.class, SCROLL_DATA, 0);
				InventoryContainer invContainer = msPlayer.getInventoryContainer();
				int rows = (int) Math.ceil((double) invContainer.getSize() / (double) inventoryHeight);
				
				if(rows - scroll <= inventoryHeight)
					return;
				
				msPlayer.setCustomData(SCROLL_DATA, scroll + 1);
				equipInventory(inv, msPlayer);
			}
			else
				return;
		
		SoundManager.play(ClickSound.DEFAULT.getAbsolouteName(), msPlayer.getPlayer());
	}
	
	public void onClickSelection(Inventory inv, int slot, MSPlayer msPlayer)
	{
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		int selectionIndex = msPlayer.getCustomData(Integer.class, SELECTION_INDEX_DATA);
		Equipment selectedEquipment = invContainer.getItem(selectionIndex);
		FilledArrayList<ItemButton> selectionButtons = selectedEquipment.getSelectionButtons(msPlayer);
		
		selectionButtons.add(BUTTON_BACK);
		
		int startX = 4 - (int) (selectionButtons.size() / 2D);
		int x = PlayerUtil.getInventoryX(slot);
		int y = PlayerUtil.getInventoryY(slot);
		
		if(x < startX || x >= startX + selectionButtons.size() || y != 3)
			return;
		
		int buttonIndex = x - startX;
		ItemButton button = selectionButtons.get(buttonIndex);
		
		button.onClick(inv, msPlayer);
	}
}
