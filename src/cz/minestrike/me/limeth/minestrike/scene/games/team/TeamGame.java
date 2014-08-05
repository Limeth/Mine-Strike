package cz.minestrike.me.limeth.minestrike.scene.games.team;

import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.scene.games.EquipmentProvider;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GameType;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class TeamGame<Lo extends GameLobby, Me extends TeamGameMenu, Ma extends GameMap, EM extends EquipmentProvider> extends Game<Lo, Me, Ma, EM>
{
	public static final String CUSTOM_DATA_TEAM = "MineStrike.game.team";
	private TeamGameListener teamGameListener;
	
	public TeamGame(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps)
	{
		super(gameType, id, name, owner, open, lobbyId, menuId, maps);
	}
	
	public boolean joinArena(MSPlayer msPlayer, Team team)
	{		
		msPlayer.updateNameTag();
		return true;
	}
	
	public boolean quitArena(MSPlayer msPlayer)
	{
		boolean success = super.quitArena(msPlayer);
		
		if(!success)
			return false;
		
		msPlayer.updateNameTag();
		return true;
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
