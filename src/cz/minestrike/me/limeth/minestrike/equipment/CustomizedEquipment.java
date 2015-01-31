package cz.minestrike.me.limeth.minestrike.equipment;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class CustomizedEquipment<T extends Equipment> implements Equipment
{
	private T equipment;
	private final EquipmentCustomization customization;
	private boolean equipped;
	
	public CustomizedEquipment(T equipment, EquipmentCustomization customization, boolean equipped)
	{
		Validate.notNull(equipment, "The source equipment must not be null!");
		
		this.equipment = equipment;
		this.customization = customization;
		this.equipped = equipped;
	}
	
	public CustomizedEquipment(T equipment, EquipmentCustomization customization)
	{
		this(equipment, customization, false);
	}
	
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = equipment.newItemStack(msPlayer);
		
		if(customization != null)
			customization.apply(equipment, is, msPlayer);
		
		return is;
	}
	
	@Override
	public Equipment getSource()
	{
		return equipment.getSource();
	}
	
	@Override
	public String getDefaultSkin(MSPlayer msPlayer)
	{
		return equipment.getDefaultSkin(msPlayer);
	}
	
	public T getEquipment()
	{
		return equipment;
	}

	public EquipmentCustomization getCustomization()
	{
		return customization;
	}

	@Override
	public String toString()
	{
		return "Equipment [type=" + equipment + ", customization=" + customization + "]";
	}

	@Override
	public String getId()
	{
		return equipment.getId();
	}
	
	@Override
	public boolean isDroppableManually()
	{
		return equipment.isDroppableManually();
	}
	
	@Override
	public boolean isDroppedOnDeath()
	{
		return equipment.isDroppedOnDeath();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends CustomizedEquipment> getEquipmentClass()
	{
		return CustomizedEquipment.class;
	}

	@Override
	public String getDisplayName()
	{
		return equipment.getDisplayName();
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return equipment.getPrice(msPlayer);
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return equipment.getMovementSpeed(msPlayer);
	}
	
	@Override
	public String getSoundDraw()
	{
		return equipment.getSoundDraw();
	}

	public boolean isEquipped()
	{
		return equipped;
	}

	public void setEquipped(boolean equipped)
	{
		this.equipped = equipped;
	}
	
	@Override
	public void onSelect(MSPlayer msPlayer)
	{
		equipment.onSelect(msPlayer);
	}
	
	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
		equipment.onDeselect(msPlayer);
	}
	
	@Override
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		return equipment.purchase(msPlayer);
	}
	
	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return equipment.rightClick(msPlayer, clickedBlock);
	}
	
	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return equipment.leftClick(msPlayer, clickedBlock);
	}
	
	public static final ItemButton BUTTON_EQUIP = new ItemButton()
	{
		@Override
		public ItemStack newItemStack()
		{
			ItemStack itemStack = new ItemStack(MSConstant.MATERIAL_CONFIRM);
			ItemMeta im = itemStack.getItemMeta();
			
			im.setDisplayName(Translation.BUTTON_INVENTORY_EQUIP.getMessage());
			itemStack.setItemMeta(im);
			
			return itemStack;
		}
		
		@Override
		public void onClick(Inventory inv, MSPlayer msPlayer)
		{
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			int selectionIndex = msPlayer.getCustomData(InventoryContainer.SELECTION_INDEX_DATA);
			Equipment selectedEquipment = invContainer.getItem(selectionIndex);
			
			if(!(selectedEquipment instanceof CustomizedEquipment))
			{
				SoundManager.play(ClickSound.INVALID.getAbsolouteName(), msPlayer.getPlayer());
				throw new RuntimeException("The equipment isn't an instance of customized equipment.");
			}
			
			@SuppressWarnings("unchecked")
			CustomizedEquipment<? extends Equipment> ce = (CustomizedEquipment<? extends Equipment>) selectedEquipment;
			Equipment source = ce.getSource();
			
			invContainer.unequip(source);
			ce.setEquipped(true);
			InventoryContainer.equipSelection(inv, msPlayer);
			SoundManager.play(ClickSound.ACCEPT.getAbsolouteName(), msPlayer.getPlayer());
		}
	};
	
	public static final ItemButton BUTTON_UNEQUIP = new ItemButton()
	{
		@Override
		public ItemStack newItemStack()
		{
			ItemStack itemStack = new ItemStack(MSConstant.MATERIAL_DENY);
			ItemMeta im = itemStack.getItemMeta();
			
			im.setDisplayName(Translation.BUTTON_INVENTORY_UNEQUIP.getMessage());
			itemStack.setItemMeta(im);
			
			return itemStack;
		}
		
		@Override
		public void onClick(Inventory inv, MSPlayer msPlayer)
		{
			InventoryContainer invContainer = msPlayer.getInventoryContainer();
			int selectionIndex = msPlayer.getCustomData(InventoryContainer.SELECTION_INDEX_DATA);
			Equipment selectedEquipment = invContainer.getItem(selectionIndex);
			
			if(!(selectedEquipment instanceof CustomizedEquipment))
			{
				SoundManager.play(ClickSound.INVALID.getAbsolouteName(), msPlayer.getPlayer());
				throw new RuntimeException("The equipment isn't an instance of customized equipment.");
			}
			
			@SuppressWarnings("unchecked")
			CustomizedEquipment<? extends Equipment> ce = (CustomizedEquipment<? extends Equipment>) selectedEquipment;
			
			ce.setEquipped(false);
			InventoryContainer.equipSelection(inv, msPlayer);
			SoundManager.play(ClickSound.ACCEPT.getAbsolouteName(), msPlayer.getPlayer());
		}
	};
	
	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		FilledArrayList<ItemButton> buttons = equipment.getSelectionButtons(msPlayer);
		InventoryContainer invContainer = msPlayer.getInventoryContainer();
		Equipment source = getSource();
		Equipment equippedEquipment = invContainer.getEquippedCustomizedEquipment(source);
		boolean isEquipped = equippedEquipment != null && equippedEquipment.equals(this);
		
		buttons.add(isEquipped ? BUTTON_UNEQUIP : BUTTON_EQUIP);
		
		return buttons;
	}

	@Override
	public EquipmentCategory getCategory()
	{
		return equipment.getCategory();
	}

	@Override
	public boolean isTradable()
	{
		return true;
	}
}
