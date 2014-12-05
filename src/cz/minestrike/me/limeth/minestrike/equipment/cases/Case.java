package cz.minestrike.me.limeth.minestrike.equipment.cases;

import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.ClickSound;
import cz.minestrike.me.limeth.minestrike.equipment.CustomizedEquipment;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentCustomization;
import cz.minestrike.me.limeth.minestrike.equipment.ItemButton;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Knife;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;

public enum Case implements Equipment
{
	ALPHA(ChatColor.GOLD + "Alpha")
	{
		@Override
		protected CaseContent[] initContents()
		{
			//Knifesd
			CustomizedEquipment<Knife> knife = new CustomizedEquipment<Knife>(Knife.KNIFE,
					EquipmentCustomization.skin("Gut - " + ChatColor.GOLD + "Daemon", "GUT_DAEMON", Color.ORANGE));
			
			//Unique
			Gun AK47 = new Gun(GunType.AK_47, ChatColor.BOLD + "Glory", "GLORY");
			Gun M4A4 = new Gun(GunType.M4A4, ChatColor.DARK_BLUE + "Storm", "STORM");
			
			//Rare
			Gun P2000 = new Gun(GunType.P2000, ChatColor.DARK_RED + "Dragon", "DRAGON");
			Gun glock = new Gun(GunType.GLOCK, ChatColor.GRAY + "Strike" + ChatColor.BLUE + "back",
					"STRIKEBACK", Color.BLUE);
			
			//Valuable
			Gun deagle = new Gun(GunType.DEAGLE, ChatColor.DARK_GRAY + "Dark Steel", "DARK_STEEL");
			Gun AWP = new Gun(GunType.AWP, ChatColor.DARK_GREEN + "Punch", "PUNCH");
			
			//Common
			Gun AUG = new Gun(GunType.AUG, "Desert", "DESERT");
			Gun SG556 = new Gun(GunType.SG_556, "Jungle Camo", "JUNGLE_CAMO");
			Gun MP7 = new Gun(GunType.MP7, "Skulls", "SKULLS");
			
			return new CaseContent[] {
					new CaseContent(knife, CaseContentRarity.LEGENDARY),
					new CaseContent(AK47, CaseContentRarity.UNIQUE),
					new CaseContent(M4A4, CaseContentRarity.UNIQUE),
					new CaseContent(P2000, CaseContentRarity.RARE),
					new CaseContent(glock, CaseContentRarity.RARE),
					new CaseContent(deagle, CaseContentRarity.VALUABLE),
					new CaseContent(AWP, CaseContentRarity.VALUABLE),
					new CaseContent(AUG, CaseContentRarity.COMMON),
					new CaseContent(SG556, CaseContentRarity.COMMON),
					new CaseContent(MP7, CaseContentRarity.COMMON),
			};
		}
	};
	
	private final String name;
	private FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> lazyContents;
	
	private Case(String name)
	{
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
		FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> contents
		= new FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>>();
		
		for(CaseContentRarity rarity : CaseContentRarity.values())
			contents.put(rarity, new FilledArrayList<CaseContent>());
		
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
		return Translation.EQUIPMENT_CASE.getMessage(name);
	}
	
	@Override
	public String getId()
	{
		return "CASE_" + name();
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
	public Case getSource()
	{
		return this;
	}
	
	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return MSConstant.MOVEMENT_SPEED_DEFAULT;
	}
	
	@Override
	public String getSoundDraw()
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
		Player player = msPlayer.getPlayer();
		
		container.add(result);
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
		FilledArrayList<ItemButton> buttons = new FilledArrayList<ItemButton>();
		
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
}
