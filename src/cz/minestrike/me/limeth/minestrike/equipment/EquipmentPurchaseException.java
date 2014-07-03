package cz.minestrike.me.limeth.minestrike.equipment;

public class EquipmentPurchaseException extends Exception
{
	private static final long serialVersionUID = 1L;
	private final Equipment equipment;
	
	public EquipmentPurchaseException(Equipment equipment)
	{
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(Equipment equipment, String message)
	{
		super(message);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(Equipment equipment, Throwable cause)
	{
		super(cause);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(Equipment equipment, String message, Throwable cause)
	{
		super(message, cause);
		this.equipment = equipment;
	}

	public EquipmentPurchaseException(Equipment equipment, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
		this.equipment = equipment;
	}

	public Equipment getEquipment()
	{
		return equipment;
	}
}
