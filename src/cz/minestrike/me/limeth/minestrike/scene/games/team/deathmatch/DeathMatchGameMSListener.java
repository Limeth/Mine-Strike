package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.events.*;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.GamePhaseType;
import cz.minestrike.me.limeth.minestrike.scene.games.RoundPhase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DeathMatchGameMSListener extends SceneMSListener<DeathMatchGame>
{
    private CheckMovedRunnable checkMovedRunnable;

	public DeathMatchGameMSListener(DeathMatchGame game)
	{
		super(game);

        checkMovedRunnable = new CheckMovedRunnable(game);
	}

    public DeathMatchGameMSListener start()
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
