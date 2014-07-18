package cz.minestrike.me.limeth.minestrike.scene.games;

import org.bukkit.event.EventHandler;
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
		
		MSPlayer msKiller = msPlayer.getLastDamageSource();
		String message;
		
		if(msKiller != null)
		{
			Equipment weapon = msPlayer.getLastDamageWeapon();
			
			if(weapon != null)
			{
				message = Translation.GAME_DEATH_WEAPONSOURCE.getMessage(msPlayer.getNameTag(), msKiller.getNameTag(), weapon.getDisplayName());
				
				msPlayer.setLastDamageWeapon(null);
			}
			else
				message = Translation.GAME_DEATH_SOURCE.getMessage(msPlayer.getNameTag(), msKiller.getNameTag());
			
			msPlayer.setLastDamageSource(null);
		}
		else
			message = Translation.GAME_DEATH_UNKNOWN.getMessage(msPlayer.getNameTag());
		
		scene.broadcast(message);
	}
}