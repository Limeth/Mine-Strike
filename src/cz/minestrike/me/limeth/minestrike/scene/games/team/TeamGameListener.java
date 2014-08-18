package cz.minestrike.me.limeth.minestrike.scene.games.team;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;

public class TeamGameListener extends MSSceneListener<TeamGame<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider>>
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
		
		Player victim = msVictim.getPlayer();
		
		if(victim.equals(damagerEntity))
			return;
		
		TeamGame<? extends GameLobby, ? extends GameMenu, ? extends GameMap, ? extends EquipmentProvider> game = getScene();
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
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event, MSPlayer msPlayer)
	{
		String message = event.getMessage();
		
		if(message.length() <= 0 || message.charAt(0) != '@')
			return;
		
		TeamGame<?, ?, ?, ?> game = getScene();
		Team team = game.getTeam(msPlayer);
		
		if(team == null)
			return;
		
		Set<Player> teammates = game.getBukkitPlayers(p -> { return game.getTeam(p) == team && p.getPlayerState() == PlayerState.JOINED_GAME; });
		Set<Player> recipients = event.getRecipients();
		Iterator<Player> recipientIterator = recipients.iterator();
		
		while(recipientIterator.hasNext())
		{
			Player recipient = recipientIterator.next();
			
			if(!teammates.contains(recipient))
				recipientIterator.remove();
		}
		
		event.setMessage(team.getChatColor() + event.getMessage());
	}
}
