package cz.minestrike.me.limeth.minestrike.equipment;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;

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
			customization.apply(equipment, is);
		
		return is;
	}
	
	@Override
	public Equipment getSource()
	{
		return equipment.getSource();
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
	public boolean purchase(MSPlayer msPlayer) throws EquipmentPurchaseException
	{
		return equipment.purchase(msPlayer);
	}
}
