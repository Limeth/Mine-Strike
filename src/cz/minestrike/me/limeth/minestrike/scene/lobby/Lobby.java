package cz.minestrike.me.limeth.minestrike.scene.lobby;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.SceneMSListener;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.lobby.LobbyInventoryMSListener.LobbyButton;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Lobby extends Scene
{
	private static final Lobby INSTANCE = new Lobby().setup();
	private SceneMSListener<Lobby> interactionListener;
	private SceneMSListener<Lobby> inventoryListener;
	
	private Lobby() {}
	
	public static Lobby getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public Lobby setup()
	{
		super.setup();

		interactionListener = new LobbyInteractionMSListener(this);
		inventoryListener = new LobbyInventoryMSListener(this);
		
		return this;
	}
	
	@Override
	public boolean onJoin(MSPlayer msPlayer)
	{
		Location spawn = MSConfig.getSpawnLocation();
		
		msPlayer.teleport(spawn);
		equip(msPlayer, true);
		msPlayer.updateNameTag();
		msPlayer.updateTabHeaderAndFooter();
		
		return true;
	}
	
	@Override
	public boolean onQuit(MSPlayer msPlayer, SceneQuitReason reason, boolean teleport)
	{
		return true;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		super.redirect(event, msPlayer);

		interactionListener.redirect(event, msPlayer);
		inventoryListener.redirect(event, msPlayer);
	}
	
	@Override
	public void equip(MSPlayer msPlayer, boolean force)
	{
		super.equip(msPlayer, force);
		msPlayer.clearTemporaryContainers();
		
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		LobbyButton[] buttons = LobbyButton.values();
		
		for(int i = 0; i < buttons.length; i++)
		{
			LobbyButton button = buttons[i];
			ItemStack item = button.newItemStack();
			
			PlayerUtil.setItem(inv, 1 + i, 1, item);
		}
	}
	
	@Override
	public Optional<Location> doSpawn(MSPlayer msPlayer, boolean teleport)
	{
		Location loc = MSConfig.getWorld().getSpawnLocation();
		
		if(teleport)
			msPlayer.teleport(loc);
		
		return Optional.of(loc);
	}
	
	@Override
	public void broadcast(String message)
	{
		for(MSPlayer msPlayer : getPlayers())
			msPlayer.sendMessage(message);
	}
	
	@Override
	public String getTabHeader(MSPlayer msPlayer)
	{
		return Translation.TAB_HEADER.getMessage();
	}
	
	@Override
	public String getTabFooter(MSPlayer msPlayer)
	{
		return Translation.TAB_LOBBY_FOOTER.getMessage();
	}
	
	@Override
	public Set<MSPlayer> getPlayers()
	{
		return MSPlayer.getOnlinePlayers().stream().filter(p -> p.getScene() instanceof Lobby).collect(Collectors.toSet());
	}
	
	@Override
	public Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> predicate)
	{
		return MSPlayer.getOnlinePlayers().stream().filter(p -> p.getScene() instanceof Lobby && predicate.test(p)).collect(Collectors.toSet());
	}
	
	@Override
	public Set<Player> getBukkitPlayers()
	{
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers())
			players.add(player.getPlayer());
		
		return players;
	}
	
	@Override
	public Set<Player> getBukkitPlayers(Predicate<? super MSPlayer> predicate)
	{
		HashSet<Player> players = new HashSet<Player>();
		
		for(MSPlayer player : getPlayers(predicate))
			players.add(player.getPlayer());
		
		return players;
	}
}
