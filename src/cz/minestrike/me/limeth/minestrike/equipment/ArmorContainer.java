package cz.minestrike.me.limeth.minestrike.equipment;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.BodyPart;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Helmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;


public class ArmorContainer implements Container
{
	private Equipment kevlar, helmet;
	private float kevlarDurability;

	@Override
	public int getSize()
	{
		return 2;
	}

	@Override
	public void clear()
	{
		kevlar = null;
		helmet = null;
		kevlarDurability = 0;
	}

	@Override
	public Equipment[] getContents()
	{
		return new Equipment[] {
				kevlar,
				helmet
		};
	}

	@Override
	public void setItem(int index, Equipment equipment)
	{
		switch(index)
		{
		case 0: setKevlar(equipment); return;
		case 1: setHelmet(equipment); return;
		default: throw new IllegalArgumentException("Index out of bouds. Size: 2");
		}
	}

	@Override
	public Equipment getItem(int index)
	{
		switch(index)
		{
		case 0: return kevlar;
		case 1: return helmet;
		default: throw new IllegalArgumentException("Index out of bouds. Size: 2");
		}
	}

	@Override
	public void apply(Inventory inv, MSPlayer msPlayer)
	{
		if(!(inv instanceof PlayerInventory))
			throw new IllegalArgumentException("The inventory must be a PlayerInventory.");
		
		PlayerInventory pInv = (PlayerInventory) inv;
		Player player = msPlayer.getPlayer();
		
		pInv.setChestplate(kevlar != null ? kevlar.newItemStack(msPlayer) : null);
		pInv.setHelmet(helmet != null ? helmet.newItemStack(msPlayer) : null);
		player.setExp(kevlarDurability > 1 ? 1 : kevlarDurability);
	}

	@Override
	public void apply(MSPlayer msPlayer)
	{
		apply(msPlayer.getPlayer().getInventory(), msPlayer);
	}
	
	public double reduceDamage(double damage, Equipment equipment, BodyPart bodyPart, boolean damageKevlar)
	{
		Float weaponArmorRatio = getWeaponArmorRatio(equipment, bodyPart);
		
		if(weaponArmorRatio != null)
		{
			if(damageKevlar && (bodyPart == BodyPart.CHEST || bodyPart == BodyPart.ABDOMEN))
			{
				float armorCost = weaponArmorRatio / 2;
				
				decreaseKevlarDurability(armorCost);
			}
			
			damage *= weaponArmorRatio;
		}
		
		return damage;
	}
	
	public Float getWeaponArmorRatio(Equipment equipment, BodyPart bodyPart)
	{
		Float weaponArmorRatio = null;
		
		if(bodyPart == null || hasArmor(bodyPart))
		{
			Equipment source = equipment.getSource();
			
			if(source instanceof GunType)
			{
				GunType gunType = (GunType) source;
				weaponArmorRatio = gunType.getWeaponArmorRatio();
			}
		}
		
		return weaponArmorRatio;
	}
	
	public boolean hasArmor(BodyPart bodyPart)
	{
		switch(bodyPart)
		{
		case CHEST:
		case ABDOMEN:
			return hasKevlar();
		case HEAD:
			return hasHelmet();
		default:
			return false;
		}
	}
	
	public boolean hasKevlar()
	{
		return kevlar != null;
	}
	
	public Equipment getKevlar()
	{
		return kevlar;
	}
	
	public void setKevlar(Equipment kevlar)
	{
		if(kevlar != null)
		{
			Equipment source = kevlar.getSource();
			
			if(!(source instanceof Kevlar))
				throw new IllegalArgumentException("The equipment's source must be Kevlar.");
			
			kevlarDurability = 1;
		}
		else
			kevlarDurability = 0;
		
		this.kevlar = kevlar;
	}
	
	public boolean hasHelmet()
	{
		return helmet != null;
	}

	public Equipment getHelmet()
	{
		return helmet;
	}

	public void setHelmet(Equipment helmet)
	{
		Equipment source = helmet.getSource();
		
		if(!(source instanceof Helmet))
			throw new IllegalArgumentException("The equipment's source must be Helmet.");
		
		this.helmet = helmet;
	}
	
	public float increaseKevlarDurability(float amount)
	{
		setKevlarDurability(kevlarDurability + amount);
		
		return kevlarDurability;
	}
	
	public float decreaseKevlarDurability(float amount)
	{
		setKevlarDurability(kevlarDurability - amount);
		
		return kevlarDurability;
	}

	public float getKevlarDurability()
	{
		return kevlarDurability;
	}

	public void setKevlarDurability(float kevlarDurability)
	{
		if(kevlarDurability < 0)
			kevlarDurability = 0;
		
		if(kevlarDurability == 0)
			kevlar = null;
		
		this.kevlarDurability = kevlarDurability;
	}
}
