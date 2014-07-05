package cz.minestrike.me.limeth.minestrike.games.team;

import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.GameType;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class TeamGame<Lo extends GameLobby, Me extends TeamGameMenu, Ma extends GameMap, EM extends EquipmentManager> extends Game<Lo, Me, Ma, EM>
{
	public static final String CUSTOM_DATA_TEAM = "MineStrike.game.team";
	private TeamGameListener teamGameListener;
	
	public TeamGame(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps)
	{
		super(gameType, id, name, owner, open, lobbyId, menuId, maps);
	}
	
	@Override
	public boolean quit(MSPlayer msPlayer, GameQuitReason reason, boolean teleport)
	{
		boolean quit = super.quit(msPlayer, reason, teleport);
		
		if(quit)
			msPlayer.updateNameTag();
		
		return quit;
	}
	
	public boolean joinArena(MSPlayer msPlayer, Team team)
	{		
		msPlayer.updateNameTag();
		return true;
	}
	
	public void quitArena(MSPlayer msPlayer)
	{
		super.quitArena(msPlayer);
		msPlayer.updateNameTag();
	}
	
	public Game<Lo, Me, Ma, EM> setup()
	{
		super.setup();
		teamGameListener = new TeamGameListener(this);
		return this;
	}
	
	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		super.redirect(event, msPlayer);
		teamGameListener.redirect(event, msPlayer);
	}
	
	@Override
	public String getPrefix(MSPlayer msPlayer)
	{
		if(!isPlayerPlaying().test(msPlayer))
			return null;
		
		Team team = getTeam(msPlayer);
		
		return team == null ? null : team.getChatColor().toString();
	}
	
	public boolean hasTeam(MSPlayer msPlayer)
	{
		return getTeam(msPlayer) != null;
	}
	
	public Team getTeam(MSPlayer msPlayer)
	{
		return msPlayer.getCustomData(Team.class, CUSTOM_DATA_TEAM);
	}
	
	public void setTeam(MSPlayer msPlayer, Team team)
	{
		msPlayer.setCustomData(CUSTOM_DATA_TEAM, team);
	}
}
