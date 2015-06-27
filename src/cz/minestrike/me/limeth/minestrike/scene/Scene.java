package cz.minestrike.me.limeth.minestrike.scene;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.UniversalShotListener;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;
import java.util.function.Predicate;

public abstract class Scene implements MSListenerRedirector
{
	private UniversalShotListener universalShotListener;

	public abstract Location spawn(MSPlayer msPlayer, boolean teleport);
	public abstract void broadcast(String message);
	public abstract Set<MSPlayer> getPlayers();
	public abstract Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> condition);
	public abstract Set<Player> getBukkitPlayers();
	public abstract Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> condition);
	public abstract boolean onJoin(MSPlayer msPlayer);
	public abstract boolean onQuit(MSPlayer msPlayer, SceneQuitReason reason, boolean teleport);
	public abstract String getTabHeader(MSPlayer msPlayer);
	public abstract String getTabFooter(MSPlayer msPlayer);

	public Scene setup()
	{
		this.universalShotListener = new UniversalShotListener();

		return this;
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		universalShotListener.redirect(event, msPlayer);
	}

	public void equip(MSPlayer msPlayer, boolean force)
	{
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();

		for(int rel = 0; rel < PlayerUtil.INVENTORY_WIDTH * 3; rel++)
		{
			int abs = rel + PlayerUtil.INVENTORY_WIDTH;

			inv.setItem(abs, MSConstant.ITEM_BACKGROUND);
		}
	}
	
	public String getPrefix(MSPlayer msPlayer)
	{
		return null;
	}
	
	public String getSuffix(MSPlayer msPlayer)
	{
		return null;
	}
}
