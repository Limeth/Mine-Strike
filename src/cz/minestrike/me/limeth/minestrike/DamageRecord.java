package cz.minestrike.me.limeth.minestrike;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;

/**
 * Created by limeth on 10.8.15.
 */
public class DamageRecord
{
    private final MSPlayer damager;
    private final Equipment weapon;
    private final BodyPart bodyPart;
    private final boolean penetrated;
    private final double damage;

    public DamageRecord(MSPlayer damager, Equipment weapon, BodyPart bodyPart, boolean penetrated, double damage)
    {
        Preconditions.checkNotNull(damager, "The damager must not be null!");
        Preconditions.checkNotNull(weapon, "The weapon must not be null!");
        Preconditions.checkArgument(damage > 0, "The damage must be larger than 0!");

        this.damager = damager;
        this.weapon = weapon;
        this.bodyPart = bodyPart;
        this.penetrated = penetrated;
        this.damage = bodyPart != null ? bodyPart.modifyDamage(damage) : damage;
    }

    public DamageRecord(MSPlayer damager, Equipment weapon, boolean penetrated, double damage)
    {
        this(damager, weapon, null, penetrated, damage);
    }

    public DamageRecord(MSPlayer damager, BodyPart bodyPart, boolean penetrated, double damage)
    {
        this(damager, damager.getEquipmentInHand(), bodyPart, penetrated, damage);
    }

    public DamageRecord(MSPlayer damager, boolean penetrated, double damage)
    {
        this(damager, damager.getEquipmentInHand(), null, penetrated, damage);
    }

    public MSPlayer getDamager()
    {
        return damager;
    }

    public Equipment getWeapon()
    {
        return weapon;
    }

    public BodyPart getBodyPart()
    {
        return bodyPart;
    }

    public boolean isPenetrated()
    {
        return penetrated;
    }

    /**
     * @return bodypart-modified damage
     */
    public double getDamage()
    {
        return damage;
    }

    public boolean isHeadshot()
    {
        return bodyPart == BodyPart.HEAD;
    }

    public DamageRecord setDamager(MSPlayer damager)
    {
        return new DamageRecord(damager, weapon, bodyPart, penetrated, damage);
    }

    public DamageRecord setWeapon(Equipment weapon)
    {
        return new DamageRecord(damager, weapon, bodyPart, penetrated, damage);
    }

    public DamageRecord setBodyPart(BodyPart bodyPart)
    {
        return new DamageRecord(damager, weapon, bodyPart, penetrated, damage);
    }

    public DamageRecord setPenetrated(boolean penetrated)
    {
        return new DamageRecord(damager, weapon, bodyPart, penetrated, damage);
    }

    public DamageRecord setDamage(double damage)
    {
        return new DamageRecord(damager, weapon, bodyPart, penetrated, damage);
    }
}
