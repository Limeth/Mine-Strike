package cz.minestrike.me.limeth.minestrike.equipment.cases;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class CaseOpening implements Runnable
{
	private static final int VIEW_WIDTH = 7, INITIAL_SPEED = 50, MAX_DELAY = 30, POWER = 5,
			SHOWN_EQUIPMENT_AMOUNT = VIEW_WIDTH - 1 + INITIAL_SPEED,
			INVENTORY_HEIGHT = 5;
	private final MSPlayer msPlayer;
	private final Case caze;
	private final Equipment result;
	private Inventory inventory;
	private FilledArrayList<Equipment> shownEquipment;
	private Integer taskId;
	private int speed;
	
	public CaseOpening(MSPlayer msPlayer, Case caze, Equipment result)
	{
		this.msPlayer = msPlayer;
		this.caze = caze;
		this.result = result;
	}
	
	public void start()
	{
		if(taskId != null)
			throw new RuntimeException("This opening has already been started.");
		
		shownEquipment = initShownEquipment();
		speed = INITIAL_SPEED;
		openInventory();
		run();
	}
	
	private int nextTask()
	{
		double percentage = 1 - speed / (double) INITIAL_SPEED;
		percentage = Math.pow(percentage, POWER);
		double preciseDelay = (int) (percentage * (double) MAX_DELAY);
		int delay = (int) Math.round(preciseDelay);
		
		if(delay < 1)
			delay = 1;
		
		return taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this, delay);
	}
	
	private static String getInventoryTitle()
	{
		String title = MSConstant.INVENTORY_NAME_PREFIX + Translation.CASE_TITLE.getMessage();
		
		if(title.length() > 32)
			title = title.substring(0, 32);
		
		return title;
	}
	
	private void openInventory()
	{
		Player player = msPlayer.getPlayer();
		inventory = Bukkit.createInventory(player, INVENTORY_HEIGHT * MSConstant.INVENTORY_WIDTH, getInventoryTitle());
		
		for(int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(i, MSConstant.ITEM_BACKGROUND);
		
		inventory.setItem(MSConstant.INVENTORY_WIDTH + 4, caze.newItemStack(msPlayer));
		equipShownEquipment();
		player.openInventory(inventory);
	}
	
	private FilledArrayList<Equipment> initShownEquipment()
	{
		FilledArrayList<Equipment> list = new FilledArrayList<Equipment>();
		Random random = new Random();
		
		for(int i = 0; i < SHOWN_EQUIPMENT_AMOUNT; i++)
		{
			Equipment equipment;
			
			if(i == SHOWN_EQUIPMENT_AMOUNT - 5)
				equipment = result;
			else
			{
				CaseContentRarity rarity = CaseContentRarity.getRandom(random);
				FilledArrayList<CaseContent> rarityEquipment = caze.getContents(rarity);
				CaseContent randomContent = rarityEquipment.get(random.nextInt(rarityEquipment.size()));
				
				equipment = randomContent.getEquipment();
			}
			
			list.add(equipment);
		}
		
		return list;
	}
	
	private int getIndex()
	{
		return INITIAL_SPEED - speed;
	}
	
	private FilledArrayList<Equipment> getCurrentlyShownEquipment()
	{
		FilledArrayList<Equipment> currentlyShown = new FilledArrayList<Equipment>();
		
		for(int i = 0; i < VIEW_WIDTH; i++)
		{
			int index = getIndex() + i;
			Equipment equipment = shownEquipment.get(index);
			
			currentlyShown.add(equipment);
		}
		
		return currentlyShown;
	}
	
	private void equipShownEquipment()
	{
		FilledArrayList<Equipment> shownEquipment = getCurrentlyShownEquipment();
		
		for(int i = 0; i < shownEquipment.size(); i++)
		{
			Equipment equipment = shownEquipment.get(i);
			int index = MSConstant.INVENTORY_WIDTH * 3 + 1 + i;
			ItemStack item = equipment.newItemStack(msPlayer);
			
			inventory.setItem(index, item);
		}
	}
	
	@Override
	public void run()
	{
		equipShownEquipment();
		
		if(--speed > 0)
			nextTask();
		else
		{
			InventoryContainer container = msPlayer.getInventoryContainer();
			
			InventoryContainer.openSelection(msPlayer, container.getSize() - 1);
		}
	}
}
