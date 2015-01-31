package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.ShopOpenEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame.RoundEndReason;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.Round.RoundPhase;

public class DefuseGameListener extends MSSceneListener<DefuseGame>
{
	public DefuseGameListener(DefuseGame game)
	{
		super(game);
	}
	
	@EventHandler
	public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
	{
		checkLoss(msPlayer);
		msPlayer.clearTemporaryContainers();
	}
	
	@EventHandler
	public void onGameQuit(GameQuitEvent event, MSPlayer msPlayer)
	{
		checkLoss(msPlayer);
		msPlayer.clearTemporaryContainers();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event, MSPlayer msPlayer)
	{
		checkLoss(msPlayer);
		msPlayer.clearTemporaryContainers();
	}
	
	public void checkLoss(MSPlayer msPlayer)
	{
		DefuseGame game = getScene();
		PlayerState state = msPlayer.getPlayerState();
		
		if(state != PlayerState.JOINED_GAME || !game.hasTeam(msPlayer) || game.isDead(msPlayer) || !(game.getPhase() instanceof Round))
			return;
		
		Team team = game.getTeam(msPlayer);
		HotbarContainer hotbarContainer = msPlayer.getHotbarContainer();

		for(Equipment equipment : hotbarContainer)
			if(equipment.isDroppedOnDeath())
				game.drop(equipment, msPlayer, false);
		
		if(team != Team.TERRORISTS || !game.isBombPlaced())
			if(game.isDead(team))
			{
				RoundEndReason endReason = team == Team.TERRORISTS ? RoundEndReason.T_KILLED : RoundEndReason.CT_KILLED;
				
				if(!game.getRound().hasEnded())
				{
					game.roundEnd(endReason);
					return;
				}
			}
		
		game.updateTabHeadersAndFooters();
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event, MSPlayer msPlayer)
	{
		ItemStack item = event.getItemInHand();
		ItemStack bomb = DefuseEquipmentProvider.BOMB.getOriginalItemStack();
		Material type = item.getType();
		Material bombType = bomb.getType();
		
		if(type != bombType)
			return;
		
		DefuseGame game = getScene();
		Block block = event.getBlock();
		Location loc = block.getLocation();
		Structure<? extends DefuseGameMap> structure = game.getMapStructure();
		DefuseGameMap map = structure.getScheme();
		Location relLoc = structure.getRelativeLocation(loc);
		RegionList bombSites = map.getBombSites();
		
		if(!bombSites.isInside(relLoc))
		{
			Player player = msPlayer.getPlayer();
			
			player.updateInventory();
			msPlayer.sendMessage(Translation.GAME_BOMB_INVALIDPLACEMENT.getMessage());
			return;
		}
		
		DefuseEquipmentProvider ep = game.getEquipmentProvider();
		Round round = game.getRound();
		RoundPhase roundPhase = round.getPhase();
		
		ep.removeBomb(msPlayer);
		
		if(roundPhase != RoundPhase.PLANTED && roundPhase != RoundPhase.ENDED)
			game.plant(block);
		
		event.setCancelled(false);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event, MSPlayer msPlayer)
	{
		DefuseGame game = getScene();
		
		if(!game.isBombPlaced())
			return;
		
		Block bombBlock = game.getBombBlock();
		Block placedBlock = event.getBlock();
		
		if(!bombBlock.equals(placedBlock))
			return;
		
		game.defuse();
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
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, MSPlayer msVictim)
	{
		DefuseGame game = getScene();
		
		if(game.isDead(msVictim))
		{
			event.setCancelled(true);
			return;
		}
		
		Entity entityDamager = event.getDamager();
		
		if(!(entityDamager instanceof Player))
			return;
		
		Player damager = (Player) entityDamager;
		MSPlayer msDamager = MSPlayer.get(damager);
		
		if(game.isDead(msDamager))
		{
			event.setCancelled(true);
			return;
		}
	}
}
