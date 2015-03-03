package cz.minestrike.me.limeth.minestrike.equipment.cases;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.*;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.lmgs.Negev;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.pistols.*;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.AK47;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.FAMAS;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.GalilAR;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.automatic.M4A4;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.AWP;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.rifles.sniper.G3SG1;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.SawedOff;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.shotguns.XM1014;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.Bizon;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.MP7;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.MP9;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.smgs.P90;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Knife;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledHashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;

public enum Case implements Equipment
{
	//TODO Load from a config file

	ALPHA(ChatColor.GOLD + "Alpha")
			{
				@Override
				protected CaseContent[] initContents()
				{
					return new CaseContent.ArrayBuilder()

							.legendary(new CustomizedEquipment<>(Knife.KNIFE, EquipmentCustomization.skin("Gut - " + ChatColor.GOLD + "Daemon", "GUT_DAEMON", Color.ORANGE)))

							.unique(new Gun(XM1014.getInstance(), ChatColor.GOLD + "Golden", "GOLDEN", Color.fromRGB(204, 153, 0))).unique(new Gun(M4A4.getInstance(), ChatColor.DARK_BLUE + "Storm", "STORM", Color.BLUE))

							.rare(new Gun(AK47.getInstance(), ChatColor.BOLD + "Glory", "GLORY", Color.BLUE)).rare(new Gun(P2000.getInstance(), ChatColor.DARK_RED + "Dragon", "DRAGON", Color.RED))

							.valuable(new Gun(Glock.getInstance(), ChatColor.GRAY + "Strike" + ChatColor.BLUE + "back", "STRIKEBACK", Color.BLUE)).valuable(new Gun(Deagle.getInstance(), ChatColor.DARK_GRAY + "Dark Steel", "DARK_STEEL", Color.BLACK))

							.common(new Gun(AWP.getInstance(), ChatColor.DARK_GREEN + "Punch", "PUNCH", Color.GREEN)).common(new Gun(MP9.getInstance(), ChatColor.GREEN + "Acid", "ACID", Color.LIME)).common(new Gun(MP7.getInstance(), ChatColor.WHITE + "Skulls", "SKULLS", Color.WHITE))

							.build();
				}
			},
	BETA(ChatColor.BLUE + "Beta")
			{
				@Override
				protected CaseContent[] initContents()
				{
					return new CaseContent.ArrayBuilder()

							.legendary(new CustomizedEquipment<>(Knife.KNIFE, EquipmentCustomization.skin("Flip - " + ChatColor.AQUA + "Fade", "FLIP_FADE", Color.AQUA)))

							.unique(new Gun(G3SG1.getInstance(), ChatColor.LIGHT_PURPLE + "Subwoofer", "SUBWOOFER", Color.fromRGB(255, 0, 127))).unique(new Gun(Negev.getInstance(), ChatColor.YELLOW + "Retro", "RETRO", Color.YELLOW))

							.rare(new Gun(FAMAS.getInstance(), ChatColor.WHITE + "Tuxedo", "TUXEDO", Color.WHITE)).rare(new Gun(USPS.getInstance(), ChatColor.AQUA + "Candy", "CANDY", Color.AQUA))

							.valuable(new Gun(GalilAR.getInstance(), ChatColor.GOLD + "Tigris", "TIGRIS", Color.ORANGE)).valuable(new Gun(P90.getInstance(), ChatColor.AQUA + "Lightning", "LIGHTNING", Color.AQUA))

							.common(new Gun(Bizon.getInstance(), ChatColor.GREEN + "Poison", "POISON", Color.fromRGB(0, 255, 127))).common(new Gun(TEC9.getInstance(), ChatColor.DARK_GREEN + "Reptile", "REPTILE", Color.fromRGB(0, 128, 0))).common(new Gun(SawedOff.getInstance(), ChatColor.BLUE + "Lapis", "LAPIS", Color.BLUE))

							.build();
				}
			};

	private final String                                                         name;
	private       FilledHashMap<CaseContentRarity, FilledArrayList<CaseContent>> lazyContents;

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
