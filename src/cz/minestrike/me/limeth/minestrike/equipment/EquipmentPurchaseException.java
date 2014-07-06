package cz.minestrike.me.limeth.minestrike.equipment;

public class EquipmentPurchaseException extends Exception
{
	private static final long serialVersionUID = 1L;
	private final EquipmentType equipment;
	
	public EquipmentPurchaseException(EquipmentType equipment)
	{
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(EquipmentType equipment, String message)
	{
		super(message);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(EquipmentType equipment, Throwable cause)
	{
		super(cause);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(EquipmentType equipment, String message, Throwable cause)
	{
		super(message, cause);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(EquipmentType equipment, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
		this.equipment = equipment;
	}

	public EquipmentType getEquipment()
	{
		return equipment;
	}
}
