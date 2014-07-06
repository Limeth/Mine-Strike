package cz.minestrike.me.limeth.minestrike.games.team;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSGameListener;

public class TeamGameListener extends MSGameListener<TeamGame<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider>>
{
	public TeamGameListener(TeamGame<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game)
	{
		super(game);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, MSPlayer msVictim)
	{
		Entity damagerEntity = event.getDamager();
		
		if(!(damagerEntity instanceof Player))
			return;
		
		TeamGame<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game = getGame();
		Player damager = (Player) damagerEntity;
		MSPlayer msDamager = MSPlayer.get(damager);
		Team damagerTeam = game.getTeam(msDamager);
		Team victimTeam = game.getTeam(msVictim);
		
		if(damagerTeam == null || victimTeam == null)
		{
			event.setCancelled(true);
		}
		else if(damagerTeam == victimTeam)
		{
			event.setCancelled(true);
		}
	}
}
