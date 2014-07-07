package cz.minestrike.me.limeth.minestrike.equipment.guns;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentType;
import cz.minestrike.me.limeth.minestrike.games.Game;

public abstract class GunTask implements Runnable
{
	protected final MSPlayer msPlayer;
	protected final int slotId;
	protected final GunType gunType;
	
	public GunTask(@Nonnull MSPlayer msPlayer, int slotId, @Nonnull GunType gunType)
	{
		Preconditions.checkNotNull(msPlayer, "MSPlayer cannot be null!");
		Preconditions.checkNotNull(gunType, "GunType cannot be null!");
		
		this.msPlayer = msPlayer;
		this.slotId = slotId;
		this.gunType = gunType;
	}
	
	public GunTask startLoop() { return this; }
	public Integer getLoopId() { return null; };
	/**
	 * This method will be executed on every repeating task iteration
	 * @return Continue in loop
	 */
	protected boolean execute() { return false; };
	
	@Override
	public void run()
	{
		boolean cont = execute();
		
		if(!cont)
			remove();
	}
	
	protected Gun parseGunIfSelected()
	{
		Game<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game = msPlayer.getGame();
		EquipmentProvider ep = game.getEquipmentManager();
		Equipment<? extends EquipmentType> equipment = ep.getCurrentlyEquipped(msPlayer);
		
		if(equipment == null || !(equipment instanceof Gun))
			return null;
		
		Gun gun = (Gun) equipment;
		GunType gunType = gun.getType();
		
		if(gunType != this.gunType)
			return null;
		
		return gun;
	}
	
	protected void stopLoop()
	{
		Integer loopId = getLoopId();
		
		if(loopId == null)
			return;
		
		Bukkit.getScheduler().cancelTask(loopId);
	}

	public void remove()
	{
		stopLoop();
		msPlayer.setGunTask(null);
	}
	
	public void cancel()
	{
		remove();
	}

	public MSPlayer getMSPlayer()
	{
		return msPlayer;
	}

	public int getSlotId()
	{
		return slotId;
	}

	public GunType getGunType()
	{
		return gunType;
	}
}
