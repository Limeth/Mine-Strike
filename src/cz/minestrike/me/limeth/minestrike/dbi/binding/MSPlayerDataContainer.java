package cz.minestrike.me.limeth.minestrike.dbi.binding;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cz.minestrike.me.limeth.minestrike.equipment.containers.InventoryContainer;

import java.util.List;

/**
 * @author Limeth
 */
public class MSPlayerDataContainer implements ComparisonGenerating<MSPlayerDataContainer>
{
	private MSPlayerData data;
	private InventoryContainer inventory;

	public MSPlayerDataContainer(MSPlayerData data, InventoryContainer inventory)
	{
		Preconditions.checkNotNull(data);
		Preconditions.checkNotNull(inventory);

		this.data = data;
		this.inventory = inventory;
	}

	public MSPlayerData getData()
	{
		return data;
	}

	public void setData(MSPlayerData data)
	{
		Preconditions.checkNotNull(data);

		this.data = data;
	}

	public InventoryContainer getInventory()
	{
		return inventory;
	}

	public void setInventory(InventoryContainer inventory)
	{
		Preconditions.checkNotNull(inventory);

		this.inventory = inventory;
	}

	public List<String> generateComparison(MSPlayerDataContainer other)
	{
		List<String> list = Lists.newArrayList();

		list.addAll(data.generateComparison(other.data));
		list.addAll(inventory.generateComparison(other.inventory));

		return list;
	}
}
