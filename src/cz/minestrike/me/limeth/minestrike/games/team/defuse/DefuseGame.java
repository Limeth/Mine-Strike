package cz.minestrike.me.limeth.minestrike.games.team.defuse;

import java.util.function.Predicate;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.GameQuitReason;
import cz.minestrike.me.limeth.minestrike.events.GameSpawnEvent;
import cz.minestrike.me.limeth.minestrike.games.GamePhase;
import cz.minestrike.me.limeth.minestrike.games.GameType;
import cz.minestrike.me.limeth.minestrike.games.MSGameListener;
import cz.minestrike.me.limeth.minestrike.games.MoneyAward;
import cz.minestrike.me.limeth.minestrike.games.PlayerState;
import cz.minestrike.me.limeth.minestrike.games.Team;
import cz.minestrike.me.limeth.minestrike.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSShoppingListener;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;

public class DefuseGame extends TeamGame<GameLobby, GameMenu, DefuseGameMap, DefuseEquipmentManager>
{
	public static final String CUSTOM_DATA_DEAD = "MineStrike.game.dead", CUSTOM_DATA_BALANCE = "MineStrike.game.balance";
	public static final int MONEY_CAP = 10000, REQUIRED_ROUNDS = 2;//8; TODO
	private int tScore, ctScore;
	private MSGameListener<DefuseGame> defuseGameListener;
	private MSGameListener<DefuseGame> shoppingListener;
	
	public DefuseGame(String id, String name, MSPlayer owner, boolean open, String lobby, String menu, FilledArrayList<String> maps)
	{
		super(GameType.DEFUSE, id, name, owner, open, lobby, menu, maps);
	}
	
	public DefuseGame(String id, String name)
	{
		this(id, name, null, true, "lobby_global_defuse", "menu_global_defuse", new FilledArrayList<String>());
	}
	
	@Override
	public DefuseGame setup()
	{
		super.setup();
		defuseGameListener = new DefuseGameListener(this);
		shoppingListener = new MSShoppingListener<DefuseGame>(this);
		
		return this;
	}
	
	@Override
	public void start()
	{
		Round round = new Round(this);
		
		setScore(0, 0);
		setPhase(round);
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		super.redirect(event, msPlayer);
		defuseGameListener.redirect(event, msPlayer);
		shoppingListener.redirect(event, msPlayer);
	}
	
	public boolean roundPrepare()
	{
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			spawnAndEquip(msPlayer, false);
			
			Player player = msPlayer.getPlayer();
			
			player.setWalkSpeed(0);
		}
		
		broadcast("Preparing round...");
		
		return true;
	}
	
	public boolean roundStart()
	{
		for(MSPlayer msPlayer : getPlayingPlayers())
			msPlayer.updateMovementSpeed();
		
		broadcast("Round started.");
		
		return true;
	}
	
	public void roundEnd(RoundEndReason reason)
	{
		if(reason == RoundEndReason.TIME_OUT)
		{
			addBalance(Team.COUNTER_TERRORISTS, reason.getCounterTerroristsReward());
			
			for(MSPlayer msPlayer : getPlayingPlayers(p -> { return getTeam(p) == Team.TERRORISTS && isDead(p); }))
				addBalance(msPlayer, reason.getTerroristsReward());
		}
		else
		{
			for(Team team : Team.values())
			{
				int reward = reason.getReward(team);
				
				addBalance(team, reward);
			}
		}
		
		Team victorTeam = reason.getVictorTeam();
		int newScore = addScore(victorTeam, 1);
		
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Player player = msPlayer.getPlayer();
			
			player.setWalkSpeed(0);
		}
		
		broadcast("Round ended. Victor team: " + victorTeam);
		
		if(newScore >= REQUIRED_ROUNDS)
		{
			broadcast(victorTeam.getColoredName() + " have won!");
			getRound().startVoteRunnable();
			return;
		}
		
		getRound().startNextRunnable();
	}
	
	public void roundNext()
	{
		getRound().start();
	}

	@Override
	public boolean joinArena(MSPlayer msPlayer, Team team)
	{
		Structure<?> previousStructure = msPlayer.getPlayerStructure();
		PlayerState previousState = msPlayer.getPlayerState();
		
		setTeam(msPlayer, team);
		msPlayer.setPlayerStructure(getMapStructure());
		msPlayer.setPlayerState(PlayerState.JOINED_GAME);
		
		ArenaJoinEvent event = new ArenaJoinEvent(this, msPlayer);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(event);
		
		if(event.isCancelled())
		{
			msPlayer.setPlayerStructure(previousStructure);
			msPlayer.setPlayerState(previousState);
			return false;
		}
		
		Location spawnLoc = spawnAndEquip(msPlayer, true);
		
		if(spawnLoc == null)
		{
			msPlayer.setPlayerStructure(previousStructure);
			msPlayer.setPlayerState(previousState);
			return false;
		}
		
		if(!hasPhase())
			start();
		
		msPlayer.setCustomData(CUSTOM_DATA_BALANCE, MoneyAward.START_CASUAL.getAmount());
		msPlayer.sendMessage(ChatColor.GRAY + "You have joined the " + team.getColoredName() + ChatColor.GRAY + ".");
		return true;
	}
	
	@Override
	public final Predicate<MSPlayer> isPlayerPlaying()
	{
		return (MSPlayer p) -> { return p.getPlayerState() == PlayerState.JOINED_GAME && getTeam(p) != null; };
	}
	
	public Round getRound()
	{
		GamePhase<GameLobby, GameMenu, DefuseGameMap, DefuseEquipmentManager> phase = getPhase();
		
		if(!(phase instanceof Round))
			throw new RuntimeException("The current phase isn't an instance of Round");
		
		return (Round) phase;
	}
	
	public void addBalance(Team team, int difference)
	{
		Validate.notNull(team, "The team cannot be null!");
		
		for(MSPlayer msPlayer : getPlayingPlayers(p -> { return getTeam(p) == team; }))
			addBalance(msPlayer, difference);
	}
	
	public void addBalance(MSPlayer msPlayer, int difference)
	{
		int balance = getBalance(msPlayer);
		balance += difference;
		
		setBalance(msPlayer, balance);
	}
	
	public void setBalance(MSPlayer msPlayer, int balance)
	{
		if(balance < 0)
			balance = 0;
		else if(balance > MONEY_CAP)
			balance = MONEY_CAP;
		
		msPlayer.setCustomData(CUSTOM_DATA_BALANCE, balance);
	}
	
	public int getBalance(MSPlayer msPlayer)
	{
		Integer balance = msPlayer.getCustomData(Integer.class, CUSTOM_DATA_BALANCE);
		
		return balance != null ? balance : 0;
	}
	
	public void broadcast(String text)
	{
		for(MSPlayer msPlayer : getPlayers())
			msPlayer.getPlayer().sendMessage(ChatColor.BOLD + text);
	}
	
	public void broadcast(String text, Predicate<MSPlayer> predicate)
	{
		for(MSPlayer msPlayer : getPlayers(predicate))
			msPlayer.getPlayer().sendMessage(ChatColor.BOLD + text);
	}
	
	public boolean isSpectating(MSPlayer msPlayer)
	{
		return !hasTeam(msPlayer);
	}
	
	public boolean isDead(Team team)
	{
		Validate.notNull(team, "The team cannot be null!");
		
		return getPlayingPlayers(p -> { return getTeam(p) == team && !isDead(p); }).size() <= 0;
	}
	
	public boolean isDead(MSPlayer msPlayer)
	{
		Boolean dead = msPlayer.getCustomData(Boolean.class, CUSTOM_DATA_DEAD);
		
		return dead != null ? dead : false;
	}
	
	public void setDead(MSPlayer msPlayer, boolean value)
	{
		msPlayer.setCustomData(CUSTOM_DATA_DEAD, value);
	}
	
	public Location spawnAndEquip(MSPlayer msPlayer, boolean force)
	{
		equip(msPlayer, force);
		setDead(msPlayer, false);
		
		return spawn(msPlayer, true);
	}

	@Override
	public Location spawn(MSPlayer msPlayer, boolean teleport)
	{
		GameSpawnEvent event = new GameSpawnEvent(this, msPlayer, teleport);		
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(event);
		
		if(event.isCancelled())
			return null;
		
		teleport = event.isTeleport();
		PlayerState playerState = msPlayer.getPlayerState();
		Location spawnLocation;
		
		if(playerState == PlayerState.LOBBY_GAME)
		{
			Structure<GameLobby> lobbyStructure = getLobbyStructure();
			GameLobby lobby = lobbyStructure.getScheme();
			spawnLocation = lobbyStructure.getAbsoluteLocation(lobby.getSpawnLocation());
		}
		else if(playerState == PlayerState.MENU_GAME)
		{
			Structure<GameMenu> menuStructure = getMenuStructure();
			GameMenu menu = menuStructure.getScheme();
			spawnLocation = menuStructure.getAbsoluteLocation(menu.getSpawnPoint());
		}
		else if(playerState == PlayerState.JOINED_GAME)
		{
			Team team = getTeam(msPlayer);
			Structure<DefuseGameMap> mapStructure = getMapStructure();
			DefuseGameMap map = mapStructure.getScheme();
			
			if(team != null && !isDead(msPlayer))
			{
				RegionList spawnRegion = map.getSpawn(team);
				Point base = map.getBase();
				spawnLocation = mapStructure.getAbsoluteLocation(spawnRegion.getRandomSpawnablePoint(base, MSConstant.RANDOM));
				
				if(spawnLocation == null)
				{
					msPlayer.sendMessage(ChatColor.RED + "Spawnpoint obscured!");
					return null;
				}
			}
			else
				spawnLocation = mapStructure.getAbsoluteLocation(map.getSpectatorSpawn());
		}
		else
		{
			quit(msPlayer, GameQuitReason.ERROR_INVALID_PLAYER_STATE, true);
			return null;
		}
		
		Player player = msPlayer.getPlayer();
		
		if(teleport)
			player.teleport(spawnLocation);
		
		return spawnLocation;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void equip(MSPlayer msPlayer, boolean force)
	{
		if(force || isDead(msPlayer))
		{
			PlayerState state = msPlayer.getPlayerState();
			
			msPlayer.clearInventory();
			
			if(state != PlayerState.JOINED_GAME || !hasTeam(msPlayer))
			{
				msPlayer.getPlayer().updateInventory();
				return;
			}
		}
		
		super.equip(msPlayer, force);
	}
	
	public int addScore(Team team, int amount)
	{
		if(team == Team.TERRORISTS)
			return addTScore(amount);
		else if(team == Team.COUNTER_TERRORISTS)
			return addCTScore(amount);
		else
			throw new IllegalArgumentException("Invalid team " + team);
	}
	
	public int getScore(Team team)
	{
		if(team == Team.TERRORISTS)
			return getTScore();
		else if(team == Team.COUNTER_TERRORISTS)
			return getCTScore();
		else
			throw new IllegalArgumentException("Invalid team " + team);
	}
	
	public void setScore(int tScore, int ctScore)
	{
		this.tScore = tScore;
		this.ctScore = ctScore;
	}
	
	public void setScore(Team team, int score)
	{
		if(team == Team.TERRORISTS)
			setTScore(score);
		else if(team == Team.COUNTER_TERRORISTS)
			setCTScore(score);
		else
			throw new IllegalArgumentException("Invalid team " + team);
	}
	
	public int addTScore(int amount)
	{
		return tScore += amount;
	}
	
	public int getTScore()
	{
		return tScore;
	}

	public void setTScore(int tScore)
	{
		this.tScore = tScore;
	}
	
	public int addCTScore(int amount)
	{
		return ctScore += amount;
	}

	public int getCTScore()
	{
		return ctScore;
	}

	public void setCTScore(int ctScore)
	{
		this.ctScore = ctScore;
	}

	public static enum RoundEndReason
	{
		TIME_OUT(Team.COUNTER_TERRORISTS, 0, MoneyAward.WIN_DEFUSE_TIME.getAmount()),
		EXPLODED(Team.TERRORISTS, MoneyAward.WIN_BOMB_EXPLODE.getAmount(), 0),
		DEFUSED(Team.COUNTER_TERRORISTS, MoneyAward.LOSS_BOMB_DEFUSE.getAmount(), MoneyAward.WIN_BOMB_DEFUSE.getAmount()),
		T_KILLED(Team.COUNTER_TERRORISTS, 0, MoneyAward.WIN_DEFUSE_ELIMINATION.getAmount()),
		CT_KILLED(Team.TERRORISTS, MoneyAward.WIN_DEFUSE_ELIMINATION.getAmount(), 0);
		
		private final Team victorTeam;
		private final int tReward, ctReward;
		
		private RoundEndReason(Team victorTeam, int tReward, int ctReward)
		{
			this.victorTeam = victorTeam;
			this.tReward = tReward;
			this.ctReward = ctReward;
		}

		public Team getVictorTeam()
		{
			return victorTeam;
		}
		
		public int getTerroristsReward()
		{
			return tReward;
		}
		
		public int getCounterTerroristsReward()
		{
			return ctReward;
		}
		
		public int getReward(Team team)
		{
			if(team == Team.TERRORISTS)
				return tReward;
			else if(team == Team.COUNTER_TERRORISTS)
				return ctReward;
			else
				throw new IllegalArgumentException("Unknown team " + team);
		}
	}
}
