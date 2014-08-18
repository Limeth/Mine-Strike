package cz.minestrike.me.limeth.minestrike.scene.games;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.Scene;

public class MSInteractionListener<T extends Game<?, ?, ?, ?>> extends MSSceneListener<T>
{
	public MSInteractionListener(T game)
	{
		super(game);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event, MSPlayer msPlayer)
	{
		Scene scene = msPlayer.getScene();
		
		if(!(scene instanceof Game))
			throw new RuntimeException("The scene " + scene + " isn't an instance of game.");
		
		Game<?, ?, ?, ?> game = (Game<?, ?, ?, ?>) scene;
		
		if(!game.isPlayerPlaying().test(msPlayer))
			return;
		
		MSPlayer msKiller = msPlayer.getLastDamageSource();
		String message;
		
		if(msKiller != null)
		{
			msPlayer.removeReceivedDamage(msKiller);
			
			MSPlayer assister = msPlayer.getPlayerAssistedInKill();
			Equipment weapon = msPlayer.getLastDamageWeapon();
			
			if(assister != null)
			{
				if(weapon != null)
				{
					if(msKiller.equals(msPlayer))
						message = Translation.GAME_DEATH_SUICIDE_ASSIST.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), assister.getNameTag());
					else
					{
						message = Translation.GAME_DEATH_WEAPONSOURCE_ASSIST.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), weapon.getDisplayName(), assister.getNameTag());
						
						msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
						msKiller.addKills(1);
					}
					
					msPlayer.setLastDamageWeapon(null);
				}
				else
				{
					message = Translation.GAME_DEATH_SOURCE_ASSIST.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), assister.getNameTag());
					
					msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
					msKiller.addKills(1);
				}
				
				double dmg = msPlayer.getReceivedDamage(assister);
				int xp = (int) Math.ceil((dmg * 5) - 25);
				
				assister.addXP(xp);
				assister.addAssists(1);
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
					
					msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
					msKiller.addKills(1);
				}
			}
			
			msPlayer.clearReceivedDamage();
			msPlayer.setLastDamageSource(null);
		}
		else
			message = Translation.GAME_DEATH_UNKNOWN.getMessage(msPlayer.getNameTag());
		
		msPlayer.addDeaths(1);
		scene.broadcast(message);
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
