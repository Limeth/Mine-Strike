package cz.minestrike.me.limeth.minestrike.equipment.cases;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentPurchaseException;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public class CaseKey implements Equipment
{
	private static final FilledHashMap<Case, CaseKey> CASE_TO_KEY = new FilledHashMap<Case, CaseKey>();
	private final Case caze;
	
	private CaseKey(Case caze)
	{
		this.caze = caze;
	}
	
	public static CaseKey ofCase(Case caze)
	{
		CaseKey key = CASE_TO_KEY.get(caze);
		
		if(key != null)
			return key;
		
		key = new CaseKey(caze);
		
		CASE_TO_KEY.put(caze, key);
		
		return key;
	}
	
	public Case getCase()
	{
		return caze;
	}

	@Override
	public String getId()
	{
		return "KEY_" + caze.getId();
	}
	
	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Equipment.class;
	}
	
	@Override
	public Equipment getSource()
	{
		return this;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack item = new ItemStack(Material.IRON_HOE);
		ItemMeta im = item.getItemMeta();
		
		im.setDisplayName(getDisplayName());
		item.setItemMeta(im);
		
		return item;
	}

	@Override
	public String getDisplayName()
	{
		return Translation.EQUIPMENT_KEY.getMessage(caze.getName());
	}

	@Override
	public String getSoundDraw()
	{
		return null;
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		throw new NotImplementedException();
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		FilledArrayList<ItemButton> buttons = new FilledArrayList<ItemButton>();
		
		buttons.add(new ItemButton() {
			@Override
			public ItemStack newItemStack()
			{
				ItemStack is = caze.newItemStack(null);
				ItemMeta im = is.getItemMeta();
				String caseName = caze.getName();
				
				im.setDisplayName(Translation.BUTTON_KEY_USE.getMessage(caseName));
				is.setItemMeta(im);
				
				return is;
			}
			
			@Override
			public void onClick(Inventory inv, MSPlayer msPlayer)
			{
				InventoryContainer container = msPlayer.getInventoryContainer();
				
				if(container.getFirstBySource(caze) == null)
				{
					String caseName = caze.getName();
					
					msPlayer.sendMessage(Translation.BUTTON_KEY_ERROR_CASENOTFOUND.getMessage(caseName));
					return;
				}
				
				caze.open(msPlayer);
			}
		});
		
		return buttons;
	}

	@Override
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		throw new NotImplementedException();
	}
}
