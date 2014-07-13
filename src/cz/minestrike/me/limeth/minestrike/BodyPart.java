package cz.minestrike.me.limeth.minestrike;

public enum BodyPart
{
	HEAD(4, "headshot", "bhit_helmet"), CHEST(1, "damage", "kevlar"), ABDOMEN(1.25, "damage", "kevlar"), LEGS(0.75, "damage", null);
	
	private static final String HIT_SOUND_PREFIX = "projectsurvive:counterstrike.player.";
	private final double damageModifier;
	private final String hitSound;
	private final String hitSoundArmored;
	
	private BodyPart(double damageModifier, String hitSound, String hitSoundArmored)
	{
		this.damageModifier = damageModifier;
		this.hitSound = HIT_SOUND_PREFIX + hitSound;
		this.hitSoundArmored = HIT_SOUND_PREFIX + hitSoundArmored;
	}

	public double getDamageModifier()
	{
		return damageModifier;
	}
	
	public double modifyDamage(double damage)
	{
		return damage * damageModifier;
	}
	
	public String getHitSound()
	{
		return hitSound;
	}

	public String getHitSoundArmored()
	{
		return hitSoundArmored;
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
