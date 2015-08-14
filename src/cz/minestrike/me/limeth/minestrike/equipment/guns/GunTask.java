package cz.minestrike.me.limeth.minestrike.equipment.guns;

import javax.annotation.Nonnull;

import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListener;
import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import org.bukkit.entity.Player;

public abstract class GunTask extends MSListener implements Runnable
{
	private final MSPlayer msPlayer;
	private final Gun gun;
	
	public GunTask(@Nonnull MSPlayer msPlayer, Gun gun)
	{
		Preconditions.checkNotNull(msPlayer, "MSPlayer cannot be null!");
		Preconditions.checkNotNull(gun, "Gun cannot be null!");
		
		this.msPlayer = msPlayer;
		this.gun = gun;
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

	public Gun getGun()
	{
		return gun;
	}
}
