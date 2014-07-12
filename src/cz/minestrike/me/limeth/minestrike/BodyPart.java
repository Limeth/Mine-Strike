package cz.minestrike.me.limeth.minestrike;

public enum BodyPart
{
	HEAD(4), CHEST(1), ABDOMEN(1.25), LEGS(0.75);
	
	private final double damageModifier;
	
	private BodyPart(double damageModifier)
	{
		this.damageModifier = damageModifier;
	}

	public double getDamageModifier()
	{
		return damageModifier;
	}
	
	public double modifyDamage(double damage)
	{
		return damage * damageModifier;
	}
	
	public static BodyPart getByY(double y)
	{
		if(y > 1.4)
			return HEAD;
		else if(y > 1.05)
			return CHEST;
		else if(y > 0.7)
			return ABDOMEN;
		else
			return LEGS;
	}
}
