package cz.minestrike.me.limeth.minestrike.scene.games.team;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.GameSpawnEvent;
import cz.minestrike.me.limeth.minestrike.events.SneakPacketEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.VoiceSound;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;

public class TeamGameListener extends MSSceneListener<TeamGame>
{
	public TeamGameListener(TeamGame game)
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
		
		TeamGame game = getScene();
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
			if(!msVictim.hasCooldown(VoiceSound.FRIENDLY_FIRE, 1000, true))
			{
				String sound = VoiceSound.FRIENDLY_FIRE.getAbsoluteName(victimTeam);
				Location victimLoc = victim.getLocation();
				
				SoundManager.play(sound, victimLoc, Float.MAX_VALUE, damager);
			}
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event, MSPlayer msPlayer)
	{
		String message = event.getMessage();
		
		if(message.length() <= 0 || message.charAt(0) != '@')
			return;
		
		TeamGame game = getScene();
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
	
	@EventHandler
	public void onGameSpawn(GameSpawnEvent event)
	{
		MSPlayer msPlayer = event.getMSPlayer();
		TeamGame game = getScene();
		
		if(!game.isPlayerPlaying().test(msPlayer))
			return;
		
		Player player = msPlayer.getPlayer();
		Set<Player> viewers = game.getBukkitPlayers();
		
		SneakPacketEvent.update(player, viewers.toArray(new Player[viewers.size()]));
	}
	
	@EventHandler
	public void onSneakPacket(SneakPacketEvent event, MSPlayer msViewer)
	{
		TeamGame game = getScene();
		
		if(!game.isPlayerPlaying().test(msViewer))
			return;
		
		MSPlayer msSneaking = event.getSneakingPlayer();
		
		if(!game.isPlayerPlaying().test(msSneaking))
			return;
		
		Team viewerTeam = game.getTeam(msViewer);
		Team sneakingTeam = game.getTeam(msSneaking);
		boolean sneaking = viewerTeam != sneakingTeam;
		
		if(event.isSneaking() == sneaking)
			event.setCancelled(true);
		else
			event.setSneaking(sneaking);
	}
}
