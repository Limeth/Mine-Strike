package cz.minestrike.me.limeth.minestrike.events;

import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;

public class ArenaJoinEvent extends MSPlayerEvent implements Cancellable, GameEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Game<?, ?, ?, ?> game;
	private boolean cancelled;
	private Team team;
	
	public ArenaJoinEvent(Game<?, ?, ?, ?> game, MSPlayer msPlayer, Team team)
	{
		super(msPlayer);
		
		Validate.notNull(game, "The game cannot be null!");
		Validate.notNull(msPlayer, "The player cannot be null!");
		
		this.game = game;
	}
	
	public HandlerList getHandlers()
	{
	    return handlers;
	}
	 
	public static HandlerList getHandlerList()
	{
	    return handlers;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team team)
	{
		this.team = team;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	@Override
	public Game<?, ?, ?, ?> getGame()
	{
		return game;
	}
}
