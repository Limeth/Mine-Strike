package cz.minestrike.me.limeth.minestrike.scene.lobby;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.lobby.MSLobbyInventoryListener.LobbyButton;
import cz.minestrike.me.limeth.minestrike.util.PlayerUtil;

public class Lobby extends Scene
{
	private static final Lobby INSTANCE = new Lobby().setup();
	private MSSceneListener<Lobby> interactionListener;
	private MSSceneListener<Lobby> inventoryListener;
	
	private Lobby() {}
	
	public static Lobby getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public Lobby setup()
	{
		interactionListener = new MSLobbyInteractionListener(this);
		inventoryListener = new MSLobbyInventoryListener(this);
		
		return this;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		interactionListener.redirect(event, msPlayer);
		inventoryListener.redirect(event, msPlayer);
	}
	
	@Override
	public void equip(MSPlayer msPlayer, boolean force)
	{
		msPlayer.clearContainers();
		msPlayer.clearInventory();
		
		Player player = msPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		for(int rel = 0; rel < PlayerUtil.INVENTORY_WIDTH * 3; rel++)
		{
			int abs = rel + PlayerUtil.INVENTORY_WIDTH;
			
			inv.setItem(abs, MSConstant.ITEM_BACKGROUND);
		}
		
		LobbyButton[] buttons = LobbyButton.values();
		
		for(int i = 0; i < buttons.length; i++)
		{
			LobbyButton button = buttons[i];
			ItemStack item = button.newItemStack();
			
			PlayerUtil.setItem(inv, 1 + i, 1, item);
		}
	}
	
	@Override
	public Location spawn(MSPlayer msPlayer, boolean teleport)
	{
		Location loc = MSConfig.getWorld().getSpawnLocation();
		
		if(teleport)
			msPlayer.teleport(loc);
		
		return loc;
	}
	
	@Override
	public void broadcast(String message)
	{
		for(MSPlayer msPlayer : getPlayers())
			msPlayer.sendMessage(message);
	}
	
	@Override
	public Set<MSPlayer> getPlayers()
	{
		return MSPlayer.getOnlinePlayers(p -> { return p.getScene() instanceof Lobby; });
	}
	
	@Override
	public Set<MSPlayer> getPlayers(Predicate<? super MSPlayer> predicate)
	{
		return MSPlayer.getOnlinePlayers(p -> { return p.getScene() instanceof Lobby && predicate.test(p); });
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