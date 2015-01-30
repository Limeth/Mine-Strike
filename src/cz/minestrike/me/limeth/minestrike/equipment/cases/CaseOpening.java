package cz.minestrike.me.limeth.minestrike.equipment.cases;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CaseOpening implements Runnable
{
	private static final int VIEW_WIDTH = 7, INITIAL_SPEED = 100, MAX_DELAY = 30,
			SHOWN_EQUIPMENT_AMOUNT = VIEW_WIDTH - 1 + INITIAL_SPEED, RESULT_DELAY = 20,
			INVENTORY_HEIGHT = 5;
	private static final double BASE = 2, POWER = 30;
	private static final String SOUND_OPEN = "projectsurvive:counterstrike.ui.csgo_ui_crate_open";
	private static final String SOUND_SCROLL = "projectsurvive:counterstrike.ui.csgo_ui_crate_item_scroll";
	private final MSPlayer msPlayer;
	private final Case caze;
	private final CaseContent result;
	private Inventory inventory;
	private FilledArrayList<CaseContent> shownContent;
	private Integer taskId;
	private int speed;
	
	public CaseOpening(MSPlayer msPlayer, Case caze, CaseContent result)
	{
		this.msPlayer = msPlayer;
		this.caze = caze;
		this.result = result;
	}
	
	public void start()
	{
		if(taskId != null)
			throw new RuntimeException("This opening has already been started.");
		
		shownContent = initShownContent();
		speed = INITIAL_SPEED;
		SoundManager.play(SOUND_OPEN, msPlayer.getPlayer());
		openInventory();
		run();
	}
	
	private int nextTask()
	{
		/*double percentage = 1 - speed / (double) INITIAL_SPEED;
		percentage = Math.pow(Math.exp(percentage - 1), POWER);
		double preciseDelay = (int) (percentage * (double) MAX_DELAY);
		int delay = (int) Math.floor(preciseDelay) + (Math.random() < (preciseDelay - Math.floor(preciseDelay)) ? 1 : 0);*/

		double preciseDelay = MAX_DELAY / Math.pow(BASE, POWER * (speed - 1) / INITIAL_SPEED);
		int delay = (int) Math.floor(preciseDelay) + (Math.random() < (preciseDelay - Math.floor(preciseDelay)) ? 1 : 0);

		if(delay < 1)
			delay = 1;

		if(speed - 1 <= 0)
			delay += RESULT_DELAY;

		SoundManager.play(SOUND_SCROLL, msPlayer.getPlayer());
		
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
	
	private FilledArrayList<CaseContent> initShownContent()
	{
		FilledArrayList<CaseContent> list = new FilledArrayList<>();
		Random random = new Random();
		
		for(int i = 0; i < SHOWN_EQUIPMENT_AMOUNT; i++)
		{
			CaseContent content;

			if(i == SHOWN_EQUIPMENT_AMOUNT - 5)
				content = result;
			else
			{
				CaseContentRarity rarity = CaseContentRarity.getRandom(random);
				FilledArrayList<CaseContent> rarityEquipment = caze.getContents(rarity);
				content = rarityEquipment.get(random.nextInt(rarityEquipment.size()));
			}
			
			list.add(content);
		}
		
		return list;
	}
	
	private int getIndex()
	{
		return INITIAL_SPEED - speed;
	}
	
	private FilledArrayList<CaseContent> getCurrentlyShownContent()
	{
		FilledArrayList<CaseContent> currentlyShown = new FilledArrayList<>();
		
		for(int i = 0; i < VIEW_WIDTH; i++)
		{
			int index = getIndex() + i;
			CaseContent content = shownContent.get(index);
			
			currentlyShown.add(content);
		}
		
		return currentlyShown;
	}
	
	private void equipShownEquipment()
	{
		FilledArrayList<CaseContent> shownContent = getCurrentlyShownContent();
		
		for(int i = 0; i < shownContent.size(); i++)
		{
			CaseContent content = shownContent.get(i);
			CaseContentRarity rarity = content.getRarity();
			Equipment equipment = content.getEquipment();
			int index = MSConstant.INVENTORY_WIDTH * 3 + 1 + i;
			ItemStack item = equipment.newItemStack(msPlayer);
			boolean selected = i == (shownContent.size() / 2);

			inventory.setItem(index - MSConstant.INVENTORY_WIDTH, rarity.getItemDisplayTop(selected));
			inventory.setItem(index, item);
			inventory.setItem(index + MSConstant.INVENTORY_WIDTH, rarity.getItemDisplayBottom(selected));
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
			Player player = msPlayer.getPlayer();
			CaseContentRarity rarity = result.getRarity();
			String sound = rarity.getSoundName();
			
			InventoryContainer.openSelection(msPlayer, container.getSize() - 1);
			SoundManager.play(sound, player);
		}
	}
}
