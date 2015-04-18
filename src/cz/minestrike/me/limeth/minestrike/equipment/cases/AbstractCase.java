package cz.minestrike.me.limeth.minestrike.equipment.cases;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.ClickSound;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCategory;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;

public abstract class AbstractCase implements Equipment
{
	private final String id;
	private final String                                                         name;
	private       FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> lazyContents;

	public AbstractCase(String id, String name)
	{
		Preconditions.checkArgument(id != null && id.length() > 0, "The case ID must not be null or empty.");
		Preconditions.checkArgument(name != null && name.length() > 0, "The case name must not be null or empty.");

		this.id = id;
		this.name = name;
	}

	protected abstract CaseContent[] initContents();

	public CaseKey getKey()
	{
		return CaseKey.ofCase(this);
	}

	public String getName()
	{
		return name;
	}

	public FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> getContents()
	{
		return lazyContents != null ? lazyContents : (lazyContents = createContents());
	}

	public FilledArrayList<CaseContent> getContents(CaseContentRarity rarity)
	{
		Validate.notNull(rarity, "The rarity must not be null!");

		return getContents().get(rarity);
	}

	private FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> createContents()
	{
		CaseContent[] rawContents = initContents();
		FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> contents = new FilledHashMap<>();

		for(CaseContentRarity rarity : CaseContentRarity.values())
			contents.put(rarity, new FilledArrayList<>());

		for(CaseContent content : rawContents)
		{
			CaseContentRarity rarity = content.getRarity();
			FilledArrayList<CaseContent> rarityContents = contents.get(rarity);

			rarityContents.add(content);
		}

		return contents;
	}

	@Override
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = new ItemStack(Material.CHEST);

		is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

		ItemMeta im = is.getItemMeta();

		im.setDisplayName(getDisplayName());
		is.setItemMeta(im);

		return is;
	}

	@Override
	public String getDisplayName()
	{
		return Translation.EQUIPMENT_CASE_NAME.getMessage(name);
	}

	@Override
	public String getId()
	{
		return "CASE_" + id;
	}

	public String getCaseId()
	{
		return id;
	}

	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Equipment.class;
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		throw new NotImplementedException();
	}

	@Override
	public AbstractCase getSource()
	{
		return this;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}

	@Override
	public String getSoundDrawing()
	{
		return null;
	}

	@Override
	public String getDefaultSkin(MSPlayer msPlayer)
	{
		return "DEFAULT";
	}

	@Override
	public boolean purchase(MSPlayer msPlayer)
	{
		throw new NotImplementedException();
	}

	public void open(MSPlayer msPlayer)
	{
		InventoryContainer container = msPlayer.getInventoryContainer();
		boolean found = container.contains(this) && container.contains(getKey());

		if(!found || !container.remove(this) || !container.remove(getKey()))
			throw new RuntimeException("Missing items.");

		Random random = new Random();
		CaseContentRarity rarity = CaseContentRarity.getRandom(random);
		FilledArrayList<CaseContent> rarityContents = getContents(rarity);
		CaseContent resultContent = rarityContents.get(random.nextInt(rarityContents.size()));
		Equipment result = resultContent.getEquipment();

		container.addItem(result);
		new CaseOpening(msPlayer, this, resultContent).start();
	}

	@Override
	public boolean rightClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}

	@Override
	public boolean leftClick(MSPlayer msPlayer, Block clickedBlock)
	{
		return false;
	}

	@Override
	public void dropButtonPress(MSPlayer msPlayer)
	{
	}

	@Override
	public void onSelect(MSPlayer msPlayer)
	{
	}

	@Override
	public void onDeselect(MSPlayer msPlayer)
	{
	}

	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		FilledArrayList<ItemButton> buttons = new FilledArrayList<>();
		
		buttons.add(new ItemButton() {
			@Override
			public ItemStack newItemStack()
			{
				CaseKey key = getKey();
				ItemStack item = key.newItemStack(null);
				ItemMeta im = item.getItemMeta();
				String caseName = getSource().getName();
				
				im.setDisplayName(Translation.BUTTON_CASE_USE.getMessage(caseName));
				item.setItemMeta(im);
				
				return item;
			}

			@Override
			public void onClick(Inventory inv, MSPlayer msPlayer)
			{
				InventoryContainer container = msPlayer.getInventoryContainer();
				CaseKey key = getKey();
				
				if(container.getFirstBySource(key) == null)
				{
					String caseName = getSource().getName();
					
					SoundManager.play(ClickSound.INVALID.getAbsolouteName(), msPlayer.getPlayer());
					msPlayer.sendMessage(Translation.BUTTON_CASE_ERROR_KEYNOTFOUND.getMessage(caseName));
					return;
				}
				
				SoundManager.play(ClickSound.ACCEPT.getAbsolouteName(), msPlayer.getPlayer());
				getSource().open(msPlayer);
			}
		});
		
		return buttons;
	}
	
	@Override
	public boolean isDroppedOnDeath()
	{
		return false;
	}
	
	@Override
	public boolean isDroppableManually()
	{
		return false;
	}

	@Override
	public EquipmentCategory getCategory()
	{
		return EquipmentCategory.CASES;
	}

	@Override
	public boolean isTradable()
	{
		return true;
	}
}
