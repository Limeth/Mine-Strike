package cz.minestrike.me.limeth.minestrike.scene;

import com.google.common.base.Preconditions;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.events.SceneJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.ScenePostSpawnEvent;
import cz.minestrike.me.limeth.minestrike.events.ScenePreSpawnEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSListenerRedirector;
import cz.minestrike.me.limeth.minestrike.scene.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.scene.listeners.UniversalShotMSListener;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public abstract class Scene implements MSListenerRedirector
{
    private UniversalShotMSListener universalShotListener;

    public abstract void broadcast(String message);

    public abstract Set<MSPlayer> getPlayers();

    public abstract Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> condition);

    public abstract Set<Player> getBukkitPlayers();

    public abstract Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> condition);

    public abstract String getTabHeader(MSPlayer msPlayer);

    public abstract String getTabFooter(MSPlayer msPlayer);

    protected abstract Optional<Location> doSpawn(MSPlayer msPlayer, boolean teleport);

    public Scene setup()
    {
        this.universalShotListener = new UniversalShotMSListener();

        return this;
    }

    @Override
    public void redirect(Event event, MSPlayer msPlayer)
    {
        universalShotListener.redirect(event, msPlayer);
    }

    public final Location spawn(MSPlayer msPlayer, boolean teleport)
    {
        ScenePreSpawnEvent preEvent = new ScenePreSpawnEvent(this, msPlayer, teleport);
        PluginManager pm = Bukkit.getPluginManager();

        pm.callEvent(preEvent);

        if(preEvent.isCancelled())
            return preEvent.getLocation();

        teleport = preEvent.isTeleport();

        Optional<Location> location = doSpawn(msPlayer, teleport);

        Preconditions.checkNotNull(location, "The Optional<Location> return value of method doSpawn must not be null. (" + this + ")");

        Location resultLocation = location.isPresent() ? location.get() : msPlayer.getPlayer().getLocation().getWorld().getSpawnLocation();

        if(teleport)
            msPlayer.teleport(resultLocation);

        ScenePostSpawnEvent postEvent = new ScenePostSpawnEvent(this, msPlayer, teleport, resultLocation);

        pm.callEvent(postEvent);

        return resultLocation;
    }

    public boolean onJoin(MSPlayer msPlayer)
    {
        Preconditions.checkNotNull(msPlayer, "The player cannot be null!");
        Preconditions.checkArgument(!hasJoined(msPlayer),
                                    "Player '" + msPlayer.getName() + "' has already joined this scene ('" + this +
                                            "').");

        SceneJoinEvent event = new SceneJoinEvent(this, msPlayer);

        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    public boolean onQuit(MSPlayer msPlayer, SceneQuitReason reason, boolean teleport)
    {
        Preconditions.checkArgument(hasJoined(msPlayer),
                                    "Player '" + msPlayer + "' has not joined this scene ('" + this + "').");

        SceneQuitEvent event = new SceneQuitEvent(this, msPlayer, reason);

        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    public boolean hasJoined(MSPlayer msPlayer)
    {
        return this.equals(msPlayer.getScene()) && getPlayers().contains(msPlayer);
    }

    public void equip(MSPlayer msPlayer, boolean force)
    {
        Player player = msPlayer.getPlayer();
        PlayerInventory inv = player.getInventory();

        for(int rel = 0; rel < PlayerUtil.INVENTORY_WIDTH * 3; rel++)
        {
            int abs = rel + PlayerUtil.INVENTORY_WIDTH;

            inv.setItem(abs, MSConstant.ITEM_BACKGROUND);
        }
    }

    public String getPrefix(MSPlayer msPlayer)
    {
        return null;
    }

    public String getSuffix(MSPlayer msPlayer)
    {
        return null;
    }

    public void playSound(String path, double x, double y, double z, float volume, float pitch,
                          Predicate<MSPlayer> predicate)
    {
        PacketPlayOutNamedSoundEffect packet = SoundManager.buildPacket(path, x, y, z, volume, pitch);

        for(MSPlayer msPlayer : (predicate != null ? getPlayers(predicate) : getPlayers()))
        {
            Player player = msPlayer.getPlayer();

            SoundManager.play(packet, player);
        }
    }

    public void playSound(String path, float volume, float pitch, Predicate<MSPlayer> predicate)
    {
        for(MSPlayer msPlayer : (predicate != null ? getPlayers(predicate) : getPlayers()))
        {
            Player player = msPlayer.getPlayer();
            Location loc = player.getLocation();
            PacketPlayOutNamedSoundEffect packet = SoundManager.buildPacket(path, loc, volume, pitch);

            SoundManager.play(packet, player);
        }
    }

    public void playSound(String path, Location location, float volume, float pitch, Predicate<MSPlayer> predicate)
    {
        if(location == null)
            playSound(path, volume, pitch, predicate);
        else
            playSound(path, location.getX(), location.getY(), location.getZ(), volume, pitch, predicate);
    }

    public void playSound(String path, Location loc, float volume, Predicate<MSPlayer> predicate)
    {
        playSound(path, loc, volume, 1, predicate);
    }

    public void playSound(String path, double x, double y, double z, float volume, Predicate<MSPlayer> predicate)
    {
        playSound(path, x, y, z, volume, 1, predicate);
    }

    public void playSound(String path, Location loc, float volume)
    {
        playSound(path, loc, volume, 1, null);
    }

    public void playSound(String path, double x, double y, double z, float volume)
    {
        playSound(path, x, y, z, volume, 1, null);
    }

    public void playSound(String path, Location loc, Predicate<MSPlayer> predicate)
    {
        playSound(path, loc, 1, 1, predicate);
    }

    public void playSound(String path, double x, double y, double z, Predicate<MSPlayer> predicate)
    {
        playSound(path, x, y, z, 1, 1, predicate);
    }

    public void playSound(String path, Location loc)
    {
        playSound(path, loc, 1, 1, null);
    }

    public void playSound(String path, double x, double y, double z)
    {
        playSound(path, x, y, z, 1, 1, null);
    }

    public void playSound(String path, float volume, Predicate<MSPlayer> predicate)
    {
        playSound(path, null, volume, 1, predicate);
    }

    public void playSound(String path, float volume)
    {
        playSound(path, null, volume, 1, null);
    }

    public void playSound(String path, Predicate<MSPlayer> predicate)
    {
        playSound(path, null, 1, 1, predicate);
    }

    public void playSound(String path)
    {
        playSound(path, null, 1, 1, null);
    }
}
