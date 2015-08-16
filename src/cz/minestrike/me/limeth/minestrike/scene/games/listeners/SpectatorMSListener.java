package cz.minestrike.me.limeth.minestrike.scene.games.listeners;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ArenaQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ScenePostSpawnEvent;
import cz.minestrike.me.limeth.minestrike.events.ScenePreSpawnEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by limeth on 16.8.15.
 */
public class SpectatorMSListener extends SceneMSListener<Game>
{
    public SpectatorMSListener(Game scene)
    {
        super(scene);
    }

    @EventHandler
    public void onScenePreSpawn(ScenePreSpawnEvent event, MSPlayer msPlayer)
    {
        Game game = getScene();
        Player player = msPlayer.getPlayer();

        if(game.isPlayerSpectating(msPlayer))
        {
            player.setAllowFlight(true);
            player.setFlying(true);

            Structure<? extends GameMap> mapStructure = game.getMapStructure();
            GameMap map = mapStructure.getScheme();
            Point relativeSpawnPoint = map.getSpectatorSpawn();
            Point spawnPoint = mapStructure.getAbsolutePoint(relativeSpawnPoint);
            Location spawnLocation = spawnPoint.getLocation(MSConfig.getWorld()).add(0.5, 0.5, 0.5);

            event.setLocation(spawnLocation);

            if(event.isTeleport())
                msPlayer.teleport(spawnLocation);

            event.setCancelled(true);
        }
        else
        {
            player.setFlying(false);
            player.setAllowFlight(false);
        }

        updateVisibility(msPlayer);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event, MSPlayer msPlayer)
    {
        Game game = getScene();

        event.setCancelled(game.isPlayerSpectating(msPlayer) ^ event.isFlying());
    }

    @EventHandler
    public void onSceneJoin(SceneJoinEvent event, MSPlayer msPlayer)
    {
        updateVisibilityOfOthers(msPlayer);
    }

    @EventHandler
    public void onSceneQuit(SceneQuitEvent event, MSPlayer msPlayer)
    {
        updateVisibilityOfOthers(msPlayer);
    }

    @EventHandler
    public void onArenaJoin(ArenaJoinEvent event, MSPlayer msPlayer)
    {
        updateVisibilityOfOthers(msPlayer);
    }

    @EventHandler
    public void onArenaQuit(ArenaQuitEvent event, MSPlayer msPlayer)
    {
        updateVisibilityOfOthers(msPlayer);
    }

    public boolean isVisible(MSPlayer msViewer, MSPlayer msViewed)
    {
        Game game = getScene();

        return !game.isPlayerSpectating(msViewer) || game.isPlayerSpectating(msViewed);
    }

    public void updateVisibility(MSPlayer msViewer, MSPlayer msViewed)
    {
        Player viewer = msViewer.getPlayer();
        Player viewed = msViewed.getPlayer();

        if(isVisible(msViewer, msViewed))
            viewer.showPlayer(viewed);
        else
            viewer.hidePlayer(viewed);
    }

    public void updateVisibilityOf(MSPlayer msPlayer)
    {
        Game game = getScene();

        game.getPlayers().forEach(msOther -> updateVisibility(msOther, msPlayer));
    }

    public void updateVisibilityOfOthers(MSPlayer msPlayer)
    {
        Game game = getScene();

        game.getPlayers().forEach(msOther -> updateVisibility(msPlayer, msOther));
    }

    public void updateVisibility(MSPlayer msPlayer)
    {
        updateVisibilityOf(msPlayer);
        updateVisibilityOfOthers(msPlayer);
    }
}
