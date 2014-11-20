package cz.minestrike.me.limeth.minestrike.listeners.clan;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Rank;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.projectsurvive.me.limeth.psclans.events.ClanCreateEvent;

public class ClanListener implements Listener
{
	@EventHandler
	public void onClanCreate(ClanCreateEvent event)
	{
		Player player = event.getInteractingPlayer();
		MSPlayer msPlayer = MSPlayer.get(player);
		Rank rank = msPlayer.getRank();
		
		if(!player.hasPermission("MineStrike.clan.creationBypass") && (rank == null || rank.ordinal() < 10))
		{
			player.sendMessage(Translation.CLAN_CREATE_ERROR_LOWRANK.getMessage());
			event.setCancelled(true);
		}
	}
}
