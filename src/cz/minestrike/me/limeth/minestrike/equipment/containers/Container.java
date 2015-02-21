package cz.minestrike.me.limeth.minestrike.equipment.containers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.dbi.binding.ComparisonGenerating;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public interface Container extends Iterable<Equipment>, ComparisonGenerating<Container>
{
	static List<String> generateComparsion(Container thiz, Container other)
	{
		Map<Equipment, Integer> mapThis = Maps.newHashMap();
		Map<Equipment, Integer> mapOther = Maps.newHashMap();

		for (Equipment temp : thiz)
		{
			Integer count = mapThis.get(temp);
			mapThis.put(temp, (count == null) ? 1 : count + 1);
			mapOther.put(temp, 0);
		}

		for (Equipment temp : other)
		{
			Integer count = mapOther.get(temp);
			mapOther.put(temp, (count == null) ? 1 : count + 1);

			if(!mapThis.containsKey(temp))
				mapThis.put(temp, 0);
		}

		List<String> result = Lists.newArrayList();

		for(Map.Entry<Equipment, Integer> entryThis : mapThis.entrySet())
		{
			Equipment equipment = entryThis.getKey();
			int delta = entryThis.getValue() - (int) mapOther.get(equipment);

			if(delta != 0)
			{
				String prefix = delta < 0 ? ChatColor.RED + ChatColor.BOLD.toString() + delta
				                          : ChatColor.GREEN + ChatColor.BOLD.toString() + "+" + delta;

				result.add(prefix + " " + equipment.getDisplayName());
			}
		}

		return result;
	}

	public int getSize();
	public void clear();
	public Equipment[] getContents();
	public void setItem(int index, Equipment equipment);
	public Equipment getItem(int index);
	public void apply(Inventory inv, MSPlayer msPlayer);
	public void apply(MSPlayer msPlayer);
	public boolean apply(Inventory inv, MSPlayer msPlayer, Equipment equipment);
	public boolean apply(MSPlayer msPlayer, Equipment equipment);
}
