package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import net.minecraft.server.v1_7_R4.Blocks;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

@Deprecated
public class ScopeExtension extends ZoomExtension
{
	public ScopeExtension(Gun gun)
	{
		super(gun);
	}
	
	@Override
	public boolean onLeftClick(MSPlayer msPlayer)
	{
		boolean zoomedBefore = isZoomed();
		
		super.onLeftClick(msPlayer);
		
		boolean zoomedAfter = isZoomed();
		
		if(zoomedAfter == zoomedBefore)
			return true;
		
		if(zoomedAfter)
		{
			CraftPlayer craftPlayer = ((CraftPlayer) msPlayer.getPlayer());
			EntityPlayer nmsPlayer = craftPlayer.getHandle();
			ItemStack nmsItem = new ItemStack(Blocks.PUMPKIN);
			
			PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, 5, nmsItem);
			nmsPlayer.playerConnection.sendPacket(packet);
		}
		else
			msPlayer.updateInventory();
		
		return true;
	}
	
	@Override
	public void unzoom(MSPlayer msPlayer)
	{
		super.unzoom(msPlayer);
		msPlayer.updateInventory();
	}
}
