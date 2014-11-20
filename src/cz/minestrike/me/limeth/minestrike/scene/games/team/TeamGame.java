package cz.minestrike.me.limeth.minestrike.scene.games.team;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.scene.games.Game;
import cz.minestrike.me.limeth.minestrike.scene.games.GameType;
import cz.minestrike.me.limeth.minestrike.scene.games.Team;
import cz.minestrike.me.limeth.minestrike.scene.games.VoiceSound;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public abstract class TeamGame extends Game
{
	public static final String CUSTOM_DATA_TEAM = "MineStrike.game.team";
	private TeamGameListener teamGameListener;
	
	public TeamGame(GameType gameType, String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps)
	{
		super(gameType, id, name, owner, open, lobbyId, menuId, maps);
	}
	
	/**
	 * @param msPlayer
	 * @param team
	 * @return True if passed
	 */
	public abstract boolean joinArena(MSPlayer msPlayer, Team team);
	
	/**
	 * @param msPlayer
	 * @return True if passed
	 */
	public boolean quitArena(MSPlayer msPlayer)
	{
		boolean success = super.quitArena(msPlayer);
		
		if(!success)
			return false;
		
		msPlayer.updateNameTag();
		return true;
	}
	
	public Game setup()
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
	
	public void playRadioSound(MSPlayer msPlayer, VoiceSound sound)
	{
		Player player = msPlayer.getPlayer();
		Team team = getTeam(msPlayer);
		Location location = player.getLocation();
		
		playRadioSound(team, location, sound);
	}
	
	public void playRadioSound(Team team, Location location, VoiceSound sound)
	{
		String soundName = sound.getAbsoluteName(team);
		
		playSound(soundName, location, Float.MAX_VALUE, 1, p -> { return getTeam(p) == team; });
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Structure<? extends TeamGameMenu> getMenuStructure()
	{
		return (Structure<? extends TeamGameMenu>) super.getMenuStructure();
	}
	
	@Override
	public void setMenuStructure(Structure<? extends GameMenu> menuStructure)
	{
		GameMenu menu = menuStructure.getScheme();
		
		if(!(menu instanceof TeamGameMenu))
			throw new IllegalArgumentException("The menu must be an instance of TeamGameMenu.");
		
		super.setMenuStructure(menuStructure);
	}
}
