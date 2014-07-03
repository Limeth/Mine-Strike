package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.events.ShopOpenEvent;
import cz.minestrike.me.limeth.minestrike.games.MSGameListener;
import cz.minestrike.me.limeth.minestrike.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.games.team.defuse.DefuseGame.RoundEndReason;

public class DefuseGameListener extends MSGameListener<DefuseGame>
{
	public DefuseGameListener(DefuseGame game)
	{
		super(game);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event, MSPlayer msPlayer)
	{
		DefuseGame game = getGame();
		PlayerState state = msPlayer.getPlayerState();
		
		if(state != PlayerState.JOINED_GAME || !game.hasTeam(msPlayer) || game.isDead(msPlayer) || !(game.getPhase() instanceof Round))
			return;
		
		Team team = game.getTeam(msPlayer);
		
		game.setDead(msPlayer, true);
		
		if(game.isDead(team))
		{
			RoundEndReason endReason = team == Team.TERRORISTS ? RoundEndReason.T_KILLED : RoundEndReason.CT_KILLED;
			
			game.roundEnd(endReason);
		}
	}
	
	@EventHandler
	public void onShopOpen(ShopOpenEvent event, MSPlayer msPlayer)
	{
		Structure<? extends Scheme> structure = msPlayer.getPlayerStructure();
		Scheme scheme = structure.getScheme();
		Player player = msPlayer.getPlayer();
		
		if(!(scheme instanceof GameMap))
		{
			event.setCancelled(true);
			player.sendMessage(Translation.GAME_SHOP_ERROR_SCHEME.getMessage());
			return;
		}
		
		GameMap map = (GameMap) scheme;
		RegionList shoppingZones = map.getShoppingZones();
		
		if(shoppingZones.size() <= 0)
		{
			event.setCancelled(true);
			player.sendMessage(Translation.GAME_SHOP_ERROR_UNAVAILABLE_MAP.getMessage());
			return;
		}
		
		Location loc = player.getLocation();
		loc = structure.getRelativeLocation(loc);
		
		if(!shoppingZones.isInside(loc))
		{
			event.setCancelled(true);
			player.sendMessage(Translation.GAME_SHOP_ERROR_AWAY.getMessage());
			return;
		}
	}
}
