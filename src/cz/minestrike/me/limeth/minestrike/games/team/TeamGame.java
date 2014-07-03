package cz.minestrike.me.limeth.minestrike.games.team;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.equipment.EquipmentManager;
import cz.minestrike.me.limeth.minestrike.games.Game;
import cz.minestrike.me.limeth.minestrike.games.GameType;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class TeamGame<Lo extends GameLobby, Me extends GameMenu, Ma extends GameMap, EM extends EquipmentManager> extends Game<Lo, Me, Ma, EM>
{
	public static final String CUSTOM_DATA_TEAM = "MineStrike.game.team";
	
	public TeamGame(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps)
	{
		super(gameType, id, name, owner, open, lobbyId, menuId, maps);
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
