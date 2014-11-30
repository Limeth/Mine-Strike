package cz.minestrike.me.limeth.minestrike.equipment.containers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.BodyPart;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunType;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Helmet;
import cz.minestrike.me.limeth.minestrike.equipment.simple.Kevlar;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;


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
		
		pInv.setChestplate(kevlar != null ? kevlar.newItemStack(msPlayer) : null);
		pInv.setHelmet(helmet != null ? helmet.newItemStack(msPlayer) : null);
		applyKevlarDurability(msPlayer);
	}
	
	@Override
	public boolean apply(Inventory inv, MSPlayer msPlayer, Equipment equipment)
	{
		Validate.notNull(equipment, "The equipment must not be null!");
		
		if(!(inv instanceof PlayerInventory))
			throw new IllegalArgumentException("The inventory must be a PlayerInventory.");
		
		PlayerInventory pInv = (PlayerInventory) inv;
		boolean found = false;
		
		if(equipment == kevlar)
		{
			pInv.setChestplate(kevlar.newItemStack(msPlayer));
			applyKevlarDurability(msPlayer);
			
			found = true;
		}
		else if(equipment == helmet)
		{
			pInv.setHelmet(helmet.newItemStack(msPlayer));
			
			found = true;
		}
		
		return found;
	}

	@Override
	public void apply(MSPlayer msPlayer)
	{
		apply(msPlayer.getPlayer().getInventory(), msPlayer);
	}

	@Override
	public boolean apply(MSPlayer msPlayer, Equipment equipment)
	{
		return apply(msPlayer.getPlayer().getInventory(), msPlayer, equipment);
	}
	
	public void applyKevlarDurability(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		
		player.setExp(kevlarDurability > 1 ? 1 : kevlarDurability);
	}
	
	public double reduceDamage(MSPlayer msPlayer, double damage, Equipment equipment, BodyPart bodyPart, boolean damageArmor)
	{
		Float weaponArmorRatio = getWeaponArmorRatio(equipment, bodyPart);
		
		if(weaponArmorRatio != null)
		{
			if(damageArmor)
			{
				Location eyeLoc = msPlayer.getPlayer().getEyeLocation();
				String hitSound = bodyPart.getHitSoundArmored();
				
				if(bodyPart == BodyPart.CHEST || bodyPart == BodyPart.ABDOMEN)
				{
					float armorCost = weaponArmorRatio / 2;
					
					decreaseKevlarDurability(armorCost);
					applyKevlarDurability(msPlayer);
				}
				
				SoundManager.play(hitSound, eyeLoc, Bukkit.getOnlinePlayers());
			}
			
			damage *= weaponArmorRatio;
		}
		else if(damageArmor)
		{
			Location eyeLoc = msPlayer.getPlayer().getEyeLocation();
			String hitSound = bodyPart != null ? bodyPart.getHitSound() : BodyPart.CHEST.getHitSound();
			
			SoundManager.play(hitSound, eyeLoc, Bukkit.getOnlinePlayers());
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
		if(helmet != null)
		{
			Equipment source = helmet.getSource();
			
			if(!(source instanceof Helmet))
				throw new IllegalArgumentException("The equipment's source must be Helmet.");
		}
		
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
	
	@Override
	public Iterator<Equipment> iterator()
	{
		return new Iterator<Equipment>() {
			private Boolean kevlar;
			
			@Override
			public Equipment next()
			{
				if(kevlar == null)
				{
					if(ArmorContainer.this.kevlar != null)
					{
						kevlar = true;
						return ArmorContainer.this.kevlar;
					}
					else if(ArmorContainer.this.helmet != null)
					{
						kevlar = false;
						return ArmorContainer.this.helmet;
					}
				}
				else if(kevlar && ArmorContainer.this.helmet != null)
				{
					kevlar = false;
					return ArmorContainer.this.helmet;
				}
				
				throw new NoSuchElementException();
			}
			
			@Override
			public boolean hasNext()
			{
				return kevlar == null && (ArmorContainer.this.kevlar != null || ArmorContainer.this.helmet != null)
						|| kevlar == true && ArmorContainer.this.helmet != null;
			}
			
			@Override
			public void remove()
			{
				if(kevlar == null)
					throw new NoSuchElementException();
				else if(kevlar)
					setKevlar(null);
				else
					setHelmet(null);
			}
		};
	}
}
