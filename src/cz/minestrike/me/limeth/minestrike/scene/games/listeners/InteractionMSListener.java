package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import cz.minestrike.me.limeth.minestrike.DamageRecord;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.events.ArenaPostDeathEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaPreDeathEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;

public class InteractionMSListener extends SceneMSListener<Game>
{
    public static final char CHARACTER_SKULL = '〙';
	public static final char CHARACTER_PENETRATED = '〚';
	public static final char CHARACTER_HEADSHOT = '〛';

	public InteractionMSListener(Game game)
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

        DamageRecord lastDamageRecord = msPlayer.getLastDamageRecord();
		String message;
		
		if(lastDamageRecord != null)
		{
            MSPlayer msKiller = lastDamageRecord.getDamager();

			msPlayer.removeReceivedDamage(msKiller);
			
			MSPlayer assistant = msPlayer.getPlayerAssistedInKill();
			Equipment weapon = lastDamageRecord.getWeapon();
            String deathIcons = getDeathIcons(lastDamageRecord);
			
			if(assistant != null)
			{
                if(msKiller.equals(msPlayer))
                    message = Translation.GAME_DEATH_SUICIDE_ASSIST.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), deathIcons, assistant.getNameTag());
                else
                {
                    message = Translation.GAME_DEATH_WEAPONSOURCE_ASSIST.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), deathIcons, msKiller.getNameTag(), assistant.getNameTag());

                    msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
                    msKiller.addKills(1);
                }

				int xp = game.getXPForAssist(msPlayer, assistant);

				assistant.addXP(xp);
				assistant.addAssists(1);
			}
			else
			{
                if(msKiller.equals(msPlayer))
                    message = Translation.GAME_DEATH_SUICIDE_SOLO.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), deathIcons);
                else
                {
                    message = Translation.GAME_DEATH_WEAPONSOURCE_SOLO.getMessage(msPlayer.getNameTag(), weapon.getDisplayName(), deathIcons, msKiller.getNameTag());

                    msKiller.addXP(game.getXPForKill(msPlayer, msKiller));
                    msKiller.addKills(1);
                }
			}

			msPlayer.setLastDamageRecord(null);
		}
		else
			message = Translation.GAME_DEATH_UNKNOWN.getMessage(msPlayer.getNameTag());

		ArenaPostDeathEvent arenaPostEvent = new ArenaPostDeathEvent(game, msPlayer);

		msPlayer.clearReceivedDamage();
		msPlayer.addDeaths(1);
		//game.setDead(msPlayer, true); Moved to DefuseGameMSListener
		game.broadcast(message);
		pm.callEvent(arenaPostEvent);
	}

    private static String getDeathIcons(DamageRecord lastDamageRecord)
    {
        String result = "";

        if(lastDamageRecord.isPenetrated())
            result += CHARACTER_PENETRATED;
        if(lastDamageRecord.isHeadshot())
            result += CHARACTER_HEADSHOT;

        return result.length() == 0 ? "" : (" " + result);
    }
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, MSPlayer msVictim)
	{
		Game game = getScene();

		if(game.isPlayerPlaying(msVictim))
		{
			event.setCancelled(true);
			return;
		}

		Entity victimEntity = msVictim.getPlayer();
		Entity damagerEntity = event.getDamager();
		
		if(!(damagerEntity instanceof Player))
			return;

		Player damager = (Player) damagerEntity;
		MSPlayer msDamager = MSPlayer.get(damager);

		if(game.isPlayerPlaying(msDamager))
		{
			event.setCancelled(true);
			return;
		}
		
		DamageCause cause = event.getCause();
		
		if(cause == DamageCause.ENTITY_ATTACK)
			event.setCancelled(true);
	}
}
