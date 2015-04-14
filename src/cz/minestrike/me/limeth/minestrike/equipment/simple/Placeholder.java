package cz.minestrike.me.limeth.minestrike.equipment.simple;

import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.*;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Limeth
 */
public class Placeholder implements Equipment
{
	private String            id;
	private EquipmentCategory category;
	private boolean           tradable;
	private final JsonElement data;
	private final String      dataString;

	private Placeholder(String id, JsonElement data, String dataString, boolean tradable, EquipmentCategory category)
	{
		this.id = id;
		this.data = data;
		this.dataString = dataString;
		this.tradable = tradable;
		this.category = category;
	}

	public static Placeholder parse(String dataString, boolean tradable, EquipmentCategory category)
	{
		JsonElement data = new JsonParser().parse(dataString);
		String id = data.isJsonPrimitive() ? data.getAsString() : data.getAsJsonObject().get("id").getAsString();

		return new Placeholder(id, data, dataString, tradable, category);
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Class<? extends Equipment> getEquipmentClass()
	{
		return Placeholder.class;
	}

	public JsonElement getData()
	{
		return data;
	}

	@Override
	public Equipment getSource()
	{
		return this;
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
	public ItemStack newItemStack(MSPlayer msPlayer)
	{
		ItemStack is = new ItemStack(Material.DEAD_BUSH);
		ItemMeta im = is.getItemMeta();

		im.setDisplayName(getDisplayName());
		im.setLore(Lists.newArrayList(ChatColor.GRAY + dataString));
		is.setItemMeta(im);

		return is;
	}

	@Override
	public String getDisplayName()
	{
		return Translation.EQUIPMENT_PLACEHOLDER_NAME.getMessage();
	}

	@Override
	public String getSoundDrawing()
	{
		return null;
	}

	@Override
	public Integer getPrice(MSPlayer msPlayer)
	{
		return null;
	}

	@Override
	public float getMovementSpeed(MSPlayer msPlayer)
	{
		return 0;
	}

	@Override
	public FilledArrayList<ItemButton> getSelectionButtons(MSPlayer msPlayer)
	{
		return null;
	}

	@Override
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		return false;
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
	public String getDefaultSkin(MSPlayer msPlayer)
	{
		return null;
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
	public EquipmentCategory getCategory()
	{
		return category;
	}

	@Override
	public boolean isTradable()
	{
		return tradable;
	}
}
