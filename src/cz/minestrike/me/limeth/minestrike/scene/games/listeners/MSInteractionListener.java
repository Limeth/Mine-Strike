package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.events.ArenaPostDeathEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaPreDeathEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;

public class MSInteractionListener extends MSSceneListener<Game>
{
	public MSInteractionListener(Game game)
	{
		super(game);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event, MSPlayer msPlayer)
	{
		Game game = getScene();
		
		if(!game.isPlayerPlaying(msPlayer) || game.isDead(msPlayer))
			return;

		ArenaPreDeathEvent arenaPreEvent = new ArenaPreDeathEvent(game, msPlayer);
		PluginManager pm = Bukkit.getPluginManager();

		pm.callEvent(arenaPreEvent);

		if(arenaPreEvent.isCancelled())
			return;

		MSPlayer msKiller = msPlayer.getLastDamageSource();
		String message;
		
		if(msKiller != null)
		{
			msPlayer.removeReceivedDamage(msKiller);
			
			MSPlayer assistant = msPlayer.getPlayerAssistedInKill();
			Equipment weapon = msPlayer.getLastDamageWeapon();
			
			if(assistant != null)
			{
				if(weapon != null)
				{
					if(msKiller.equals(msPlayer))
						message = Translation.GAME_DEATH_SUICIDE_ASSIST.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), assistant.getNameTag());
					else
					{
						message = Translation.GAME_DEATH_WEAPONSOURCE_ASSIST.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), weapon.getDisplayName(), assistant.getNameTag());
						
						msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
						msKiller.addKills(1);
					}
					
					msPlayer.setLastDamageWeapon(null);
				}
				else
				{
					message = Translation.GAME_DEATH_SOURCE_ASSIST.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), assistant.getNameTag());
					
					msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
					msKiller.addKills(1);
				}

				int xp = game.getXPForAssist(msPlayer, assistant);

				assistant.addXP(xp);
				assistant.addAssists(1);
			}
			else
			{
				if(weapon != null)
				{
					if(msKiller.equals(msPlayer))
						message = Translation.GAME_DEATH_SUICIDE_SOLO.getMessage(msPlayer.getNameTag(), weapon.getDisplayName());
					else
					{
						message = Translation.GAME_DEATH_WEAPONSOURCE_SOLO.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), weapon.getDisplayName());
						
						msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
						msKiller.addKills(1);
					}
					
					msPlayer.setLastDamageWeapon(null);
				}
				else
				{
					message = Translation.GAME_DEATH_SOURCE_SOLO.getMessage(msPlayer.getNameTag(), msKiller.getNameTag());
					int xp = game.getXPForKill(msPlayer, msKiller);

					msKiller.addXP(xp);
					msKiller.addKills(1);
				}
			}

			msPlayer.setLastDamageSource(null);
		}
		else
			message = Translation.GAME_DEATH_UNKNOWN.getMessage(msPlayer.getNameTag());

		ArenaPostDeathEvent arenaPostEvent = new ArenaPostDeathEvent(game, msPlayer);

		msPlayer.clearReceivedDamage();
		msPlayer.addDeaths(1);
		game.setDead(msPlayer, true);
		game.broadcast(message);
		pm.callEvent(arenaPostEvent);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, MSPlayer msVictim)
	{
		Entity victimEntity = event.getEntity();
		Entity damagerEntity = event.getDamager();
		
		if(!(victimEntity instanceof Player) || !(damagerEntity instanceof Player))
			return;
		
		DamageCause cause = event.getCause();
		
		if(cause == DamageCause.ENTITY_ATTACK)
			event.setCancelled(true);
	}
}
