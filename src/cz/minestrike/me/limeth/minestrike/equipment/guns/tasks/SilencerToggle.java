package cz.minestrike.me.limeth.minestrike.equipment.guns.tasks;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.equipment.guns.Gun;
import cz.minestrike.me.limeth.minestrike.equipment.guns.GunTask;
import cz.minestrike.me.limeth.minestrike.equipment.guns.type.DoubleModeGunType;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import org.bukkit.Bukkit;

public class SilencerToggle extends GunTask
{
	private static final long DURATION_TICKS = 40L;
	private String sound;
	private Integer taskId;
	
	public SilencerToggle(MSPlayer msPlayer, Gun gun, String sound)
	{
		super(msPlayer, gun);
		
		this.sound = sound;
	}
	
	@Override
	protected void stopLoop()
	{
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	@Override
	public GunTask startLoop()
	{
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineStrike.getInstance(), this, DURATION_TICKS);
		
		SoundManager.play(sound, getMSPlayer().getPlayer());
		
		return this;
	}
	
	@Override
	protected boolean execute()
	{
		Gun gun = getGun();
		MSPlayer msPlayer = getMSPlayer();
		HotbarContainer container = msPlayer.getHotbarContainer();

		DoubleModeGunType.toggleSecondMode(gun);
		container.apply(msPlayer, gun);

		return false; //Remove task
	}

	public String getSound()
	{
		return sound;
	}

	public void setSound(String sound)
	{
		this.sound = sound;
	}
}
