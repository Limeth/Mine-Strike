package cz.minestrike.me.limeth.minestrike.equipment.guns.extensions;

import net.minecraft.server.v1_7_R1.Blocks;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.PacketPlayOutSetSlot;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;

public class ScopeExtension extends ZoomExtension
{
	public ScopeExtension(Gun gun)
	{
		super(gun);
	}
	
	@Override
	public void onLeftClick(MSPlayer msPlayer)
	{
		boolean zoomedBefore = isZoomed();
		
		super.onLeftClick(msPlayer);
		
		boolean zoomedAfter = isZoomed();
		
		if(zoomedAfter == zoomedBefore)
			return;
		
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
	}
}
