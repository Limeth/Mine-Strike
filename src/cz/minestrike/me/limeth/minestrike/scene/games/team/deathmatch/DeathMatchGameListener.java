package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.MineStrike;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.Scheme;
import cz.minestrike.me.limeth.minestrike.areas.schemes.TeamGameMap;
import cz.minestrike.me.limeth.minestrike.equipment.Equipment;
import cz.minestrike.me.limeth.minestrike.equipment.containers.HotbarContainer;
import cz.minestrike.me.limeth.minestrike.events.*;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.games.RoundPhase;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseEquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGame.RoundEndReason;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseGameMap;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.DefuseRound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DeathMatchGameListener extends MSSceneListener<DeathMatchGame>
{
    private CheckMovedRunnable checkMovedRunnable;

	public DeathMatchGameListener(DeathMatchGame game)
	{
		super(game);

        checkMovedRunnable = new CheckMovedRunnable(game);
	}

    public DeathMatchGameListener start()
    {
        if(!checkMovedRunnable.isRunning())
            checkMovedRunnable.start();

        return this;
    }

    public void resetMovedRunnable(MSPlayer msPlayer)
    {
        checkMovedRunnable.reset(msPlayer);
    }

	@EventHandler
	public void onShopOpen(ShopOpenEvent event, MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
        DeathMatchGame game = getScene();

        if(game.hasMoved(msPlayer))
        {
            event.setCancelled(true);
            player.sendMessage(Translation.GAME_SHOP_ERROR_MOVED.getMessage());
        }
	}

    @EventHandler
    public void onEquipmentDrop(EquipmentDropEvent event, MSPlayer msPlayer)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event, MSPlayer msVictim)
    {
        DeathMatchGame game = getScene();

        if(!game.hasMoved(msVictim))
        {
            event.setCancelled(true);
            return;
        }

        Entity damagerEntity = event.getDamager();

        if(!(damagerEntity instanceof Player))
            return;

        Player victim = msVictim.getPlayer();

        if(victim.equals(damagerEntity))
            return;

        Player damager = (Player) damagerEntity;
        MSPlayer msDamager = MSPlayer.get(damager);

        if(!game.hasMoved(msDamager))
        {
            event.setCancelled(true);
            return;
        }

        GamePhaseType gamePhaseType = game.getPhaseType();

        if(gamePhaseType == GamePhaseType.RUNNING)
        {
            DeathMatchRound round = game.getRound();
            RoundPhase roundPhase = round.getPhase();

            if(roundPhase == DeathMatchRound.PHASE_PREPARATION)
                event.setCancelled(true);
        }
    }

    @Override
    public void redirect(Event event, MSPlayer msPlayer)
    {
        checkMovedRunnable.redirect(event, msPlayer);
        super.redirect(event, msPlayer);
    }
}
