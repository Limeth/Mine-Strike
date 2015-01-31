package cz.minestrike.me.limeth.minestrike.scene.games.team.defuse;

import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.Region;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent.SceneQuitReason;
import cz.minestrike.me.limeth.minestrike.events.GameSpawnEvent;
import cz.minestrike.me.limeth.minestrike.listeners.msPlayer.MSSceneListener;
import cz.minestrike.me.limeth.minestrike.scene.games.*;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RadarView;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.Round.RoundPhase;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.projectsurvive.limeth.dynamicdisplays.DynamicDisplays;
import cz.projectsurvive.limeth.dynamicdisplays.PlayerDisplay;
import cz.projectsurvive.limeth.dynamicdisplays.TimedPlayerDisplay;
import cz.projectsurvive.me.limeth.Title;
import ftbastler.HeadsUpDisplay;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.Set;
import java.util.function.Predicate;

public class DefuseGame extends TeamGame
{
	public static final String CUSTOM_DATA_BALANCE = "MineStrike.game.balance";
	public static final int MONEY_CAP = 10000, REQUIRED_ROUNDS = 1/*TODO 8*/,
			XP_KILL = 100, XP_MATCH_WIN = 200, XP_MATCH_LOSE = 50;
	private int tScore, ctScore;
	private int                         winsInRow;
	private Team                        lastWinner;
	private Block                       bombBlock;
	private boolean                     bombGiven;
	private MSSceneListener<DefuseGame> defuseGameListener;
	private MSRewardListener<DefuseGame> defuseRewardListener;

	public DefuseGame(String id, String name, MSPlayer owner, boolean open, String lobby, String menu, FilledArrayList<String> maps)
	{
		super(GameType.DEFUSE, id, name, owner, open, lobby, menu, maps);
	}

	public DefuseGame(String id, String name)
	{
		this(id, name, null, true, "lobby_global_defuse", "menu_global_defuse", new FilledArrayList<>());
	}

	@Override
	public DefuseGame setup()
	{
		super.setup();
		defuseGameListener = new DefuseGameListener(this);
		defuseRewardListener = new DefuseRewardListener(this);

		return this;
	}

	@Override
	public void start()
	{
		super.start();

		Round round = new Round(this);
		winsInRow = 0;
		lastWinner = null;

		for(MSPlayer player : getPlayingPlayers())
		{
			player.clearContainers();
			setBalance(player, MoneyAward.START_CASUAL.getAmount());
		}

		setBombBlock(null);
		setScore(0, 0);
		setPhase(round);
	}

	@Override
	public RadarView createRadarView()
	{
		return new DefuseRadarView(this);
	}

	@Override
	public void redirect(Event event, MSPlayer msPlayer)
	{
		super.redirect(event, msPlayer);
		defuseGameListener.redirect(event, msPlayer);
		defuseRewardListener.redirect(event, msPlayer);

		if(getPhaseType() == GamePhaseType.RUNNING)
			getRound().redirect(event, msPlayer);
	}

	public boolean roundPrepare()
	{
		clearBombsites();
		clearDrops();

		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Player player = msPlayer.getPlayer();

			player.setWalkSpeed(0);
			setDead(msPlayer, false);
			spawnAndEquip(msPlayer, false);
			showWitherBar(msPlayer);
		}

		bombGiven = false;
		giveBomb();
		updateTabHeadersAndFooters();

		return true;
	}

	public boolean roundStart()
	{
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Player player = msPlayer.getPlayer();
			Team team = getTeam(msPlayer);
			String sound = VoiceSound.LOCK_AND_LOAD.getAbsoluteName(team);

			showWitherBar(msPlayer);
			msPlayer.updateMovementSpeed();
			SoundManager.play(sound, player);
		}

		return true;
	}

	public MSPlayer giveBomb()
	{
		Set<MSPlayer> terrorists = getPlayingPlayers(p -> getTeam(p) == Team.TERRORISTS);
		int terroristsAmount = terrorists.size();

		if(terroristsAmount > 0)
		{
			int randomIndex = MSConstant.RANDOM.nextInt(terroristsAmount);
			DefuseEquipmentProvider mgr = getEquipmentProvider();
			int i = 0;
			MSPlayer carrier = null;
			
			for(MSPlayer terrorist : terrorists)
			{
				if(i >= randomIndex)
				{
					carrier = terrorist;
					break;
				}
				
				i++;
			}
			
			Player player = carrier.getPlayer();
			
			bombGiven = true;
			mgr.equipBomb(carrier);
			player.updateInventory();
			carrier.sendMessage(Translation.GAME_BOMB_RECEIVED.getMessage());
			
			return carrier;
		}
		else
			bombGiven = false;
		
		return null;
	}
	
	public void plant(Block block)
	{
		Validate.notNull(block, "The block cannot be null!");
		
		bombBlock = block;
		Round round = getRound();
		
		round.setRanAt(System.currentTimeMillis());
		round.setPhase(RoundPhase.PLANTED);
		round.startExplodeRunnable();
		getPlayingPlayers().forEach(this::showWitherBar);
		playSound("projectsurvive:counterstrike.radio.bombpl");
		
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Team team = getTeam(msPlayer);
			Player player = msPlayer.getPlayer();
			String content;
			
			if(team == Team.TERRORISTS)
				content = Translation.ACTIONBAR_GAME_DEFUSE_BOMB_PLANTED_T.getMessage();
			else if(team == Team.COUNTER_TERRORISTS)
				content = Translation.ACTIONBAR_GAME_DEFUSE_BOMB_PLANTED_CT.getMessage();
			else
				continue;
			
			Title.send(player, null, content);
		}
	}
	
	public void defuse()
	{
		Round round = getRound();
		
		clearBombsites();
		round.cancel();
		playSound("projectsurvive:counterstrike.radio.bombdef");
		broadcast(Translation.GAME_BOMB_DEFUSED.getMessage());
		
		if(!getRound().hasEnded())
			roundEnd(RoundEndReason.DEFUSED);
	}
	
	public void explode()
	{
		double x = bombBlock.getX() + 0.5;
		double y = bombBlock.getY() + 0.5;
		double z = bombBlock.getZ() + 0.5;
		World world = bombBlock.getWorld();
		
		clearBombsites();
		world.createExplosion(x, y, z, MSConstant.BOMB_POWER, false, false);
	}
	
	public void clearBombsites()
	{
		bombBlock = null;
		Structure<? extends DefuseGameMap> structure = getMapStructure();
		DefuseGameMap scheme = structure.getScheme();
		World world = MSConfig.getWorld();
		RegionList bombSites = scheme.getBombSites();
		
		for(Region bombSite : bombSites)
			for(Point relativePoint : bombSite)
			{
				Point absolutePoint = structure.getAbsolutePoint(relativePoint);
				Block block = absolutePoint.getBlock(world);
				Material material = block.getType();
				
				if(material == DefuseEquipmentProvider.BOMB.getOriginalItemStack().getType())
					block.setType(Material.AIR);
			}
	}
	
	@Override
	public boolean onJoin(MSPlayer msPlayer)
	{
		if(!super.onJoin(msPlayer))
			return false;
		
		msPlayer.updateTabHeaderAndFooter();
		return true;
	}
	
	public void showWitherBar(MSPlayer msPlayer)
	{
		Player player = msPlayer.getPlayer();
		GamePhaseType phaseType = getPhaseType();
		
		if(phaseType == GamePhaseType.RUNNING)
		{
			Round round = getRound();
			RoundPhase roundPhase = round.getPhase();
			Long time = null;
			
			if(roundPhase == RoundPhase.PREPARING)
				time = Round.SPAWN_TIME;
			else if(roundPhase == RoundPhase.STARTED)
				time = Round.ROUND_TIME;
			else if(roundPhase == RoundPhase.PLANTED)
				time = Round.BOMB_TIME;
			
			if(time != null)
			{
				long nowMillis = System.currentTimeMillis();
				long ranAtMillis = round.getRanAt();
				long differenceMillis = nowMillis - ranAtMillis;
				double difference = differenceMillis * 20D / 1000D;
				
				HeadsUpDisplay.displayLoadingBar(getWitherTitle(), player, difference, time, false, () -> HeadsUpDisplay.displayTextBar(getWitherTitle(), player));
				
				return;
			}
		}
		
		HeadsUpDisplay.displayTextBar(getWitherTitle(), player);
	}
	
	public void removeWitherBar(MSPlayer msPlayer)
	{
		HeadsUpDisplay.removeBar(msPlayer.getPlayer());
	}
	
	public String getWitherTitle()
	{
		String middle;
		
		if(isBombPlaced())
			middle = ChatColor.DARK_GRAY + " | " + Translation.GAME_BOMB_PLANTED.getMessage() + ChatColor.DARK_GRAY + " | ";
		else
			middle = ChatColor.DARK_GRAY + " | ";
		
		return ChatColor.BLUE + "" + ctScore + middle + ChatColor.GOLD + tScore;
	}
	
	public void roundEnd(RoundEndReason reason)
	{
		if(reason == RoundEndReason.TIME_OUT)
		{
			addBalance(Team.COUNTER_TERRORISTS, reason.getCounterTerroristsReward());
			
			for(MSPlayer msPlayer : getPlayingPlayers(p -> getTeam(p) == Team.TERRORISTS && isDead(p)))
				addBalance(msPlayer, reason.getTerroristsReward());
		}
		else
		{
			for(Team team : Team.values())
			{
				int reward = reason.getReward(team);
				
				addBalance(team, reward);
			}
			
			Team victorTeam = reason.getVictorTeam();
			Team lostTeam = victorTeam.getOppositeTeam();
			MoneyAward lostAward;
			
			if(lostTeam != lastWinner)
				winsInRow++;
			else
				winsInRow = 1;
			
			switch(winsInRow)
			{
			case 1: lostAward = MoneyAward.LOSS_FIRST; break;
			case 2: lostAward = MoneyAward.LOSS_SECOND; break;
			case 3: lostAward = MoneyAward.LOSS_THIRD; break;
			case 4: lostAward = MoneyAward.LOSS_FOURTH; break;
			default: lostAward = MoneyAward.LOSS_FIFTH; break;
			}
			
			addBalance(lostTeam, lostAward.getAmount());
		}
		
		if(reason == RoundEndReason.EXPLODED)
			explode();
		
		Team victorTeam = reason.getVictorTeam();
		int newScore = addScore(victorTeam, 1);
		Round round = getRound();
		
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Player player = msPlayer.getPlayer();
			
			player.setWalkSpeed(0);
		}
		
		String winSound = victorTeam.getWinSound();
		
		playSound(winSound);
		updateTabHeadersAndFooters();
		
		if(newScore >= REQUIRED_ROUNDS)
			matchEnd(victorTeam);
		else
		{
			for(MSPlayer msPlayer : getPlayingPlayers(p -> p.getPlayerState() == PlayerState.JOINED_GAME))
			{
				Player player = msPlayer.getPlayer();
				String endMessage = Translation.GAME_ROUND_END.getMessage(victorTeam.getColoredName());
				PlayerDisplay display = new TimedPlayerDisplay(player)
						.startCountdown(Round.END_TIME).setLines(endMessage)
						.setDistance(2);
				
				DynamicDisplays.setDisplay(player, display);
			}
			
			round.setPhase(RoundPhase.ENDED);
			round.startNextRunnable();
		}
	}
	
	public void matchEnd(Team victorTeam)
	{
		Round round = getRound();
		Team loserTeam = victorTeam.getOppositeTeam();
		
		for(MSPlayer msPlayer : getPlayingPlayers())
		{
			Team team = getTeam(msPlayer);
			
			if(team == victorTeam)
				msPlayer.addXP(XP_MATCH_WIN);
			else if(team == loserTeam)
				msPlayer.addXP(XP_MATCH_LOSE);
		}

		defuseRewardListener.rewardPlayers();
		
		for(MSPlayer msPlayer : getPlayingPlayers(p -> p.getPlayerState() == PlayerState.JOINED_GAME))
		{
			Player player = msPlayer.getPlayer();
			String[] endMessages = {
						ChatColor.DARK_GRAY + "× × ×",
						Translation.GAME_MATCH_END_1.getMessage(victorTeam.getColoredName()),
						Translation.GAME_MATCH_END_2.getMessage(victorTeam.getColoredName()),
						ChatColor.DARK_GRAY + "× × ×",
					};
			PlayerDisplay display = new TimedPlayerDisplay(player)
					.startCountdown(Round.VOTE_TIME).setLines(endMessages[1], endMessages[2], endMessages)
					.setDistance(2);
			
			DynamicDisplays.setDisplay(player, display);
		}
		
		round.setPhase(RoundPhase.ENDED);
		round.startVoteRunnable();
	}
	
	public void roundNext()
	{
		getRound().start();
	}
	
	public boolean isDeadAfterJoin(MSPlayer msPlayer, Team team)
	{
		GamePhase<? extends Game> gamePhase = getPhase();

		if(!(gamePhase instanceof Round))
			return true;
		
		Round round = (Round) gamePhase;
		RoundPhase roundPhase = round.getPhase();
		int tPlayers = 0, ctPlayers = 0;
		
		for(MSPlayer playingPlayer : getPlayingPlayers())
		{
			Team playingTeam = getTeam(playingPlayer);
			
			if(playingTeam == Team.TERRORISTS)
				tPlayers++;
			else if(playingTeam == Team.COUNTER_TERRORISTS)
				ctPlayers++;
		}
		
		return roundPhase != RoundPhase.PREPARING && tPlayers > 0 && ctPlayers > 0;
	}

	@Override
	public boolean joinArena(MSPlayer msPlayer, Team team)
	{
		ArenaJoinEvent event = new ArenaJoinEvent(this, msPlayer, team);
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.callEvent(event);
		
		if(event.isCancelled())
			return false;
		
		if(!hasPhase())
			start();
		
		boolean dead = isDeadAfterJoin(msPlayer, team);
		
		setDead(msPlayer, dead);
		setTeam(msPlayer, team);
		msPlayer.setPlayerStructure(getMapStructure());
		msPlayer.setPlayerState(PlayerState.JOINED_GAME);
		
		Location spawnLoc = spawnAndEquip(msPlayer, true);
		
		if(spawnLoc == null)
			return false;
		
		setBalance(msPlayer, MoneyAward.START_CASUAL.getAmount());
		showWitherBar(msPlayer);
		
		if(team != null)
		{
			if(!isBombGiven() && team == Team.TERRORISTS)
				giveBomb();
			
			updateTabHeadersAndFooters();
		}
		
		msPlayer.updateNameTag();
		msPlayer.sendMessage(Team.getJoinMessage(team).getMessage());
		return true;
	}
	
	@Override
	public final Predicate<MSPlayer> isPlayerPlaying()
	{
		return (MSPlayer p) -> p.getPlayerState() == PlayerState.JOINED_GAME && getTeam(p) != null;
	}
	
	public Round getRound()
	{
		GamePhase<? extends Game> phase = getPhase();
		
		if(!(phase instanceof Round))
			throw new RuntimeException("The current phase isn't an instance of Round");
		
		return (Round) phase;
	}
	
	public void addBalance(Team team, int difference)
	{
		Validate.notNull(team, "The team cannot be null!");
		
		for(MSPlayer msPlayer : getPlayingPlayers(p -> getTeam(p) == team))
			addBalance(msPlayer, difference);
	}
	
	public void updateBalance(MSPlayer msPlayer)
	{
		int balance = getBalance(msPlayer);
		Player player = msPlayer.getPlayer();
		
		player.setLevel(balance);
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
		updateBalance(msPlayer);
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
	
	public Location spawnAndEquip(MSPlayer msPlayer, boolean force)
	{
		equip(msPlayer, force);
		
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
		Point spawnPoint;
		
		if(playerState == PlayerState.LOBBY_GAME)
		{
			Structure<? extends GameLobby> lobbyStructure = getLobbyStructure();
			GameLobby lobby = lobbyStructure.getScheme();
			spawnPoint = lobbyStructure.getAbsolutePoint(lobby.getSpawnLocation());
		}
		else if(playerState == PlayerState.MENU_GAME)
		{
			Structure<? extends GameMenu> menuStructure = getMenuStructure();
			GameMenu menu = menuStructure.getScheme();
			spawnPoint = menuStructure.getAbsolutePoint(menu.getSpawnPoint());
		}
		else if(playerState == PlayerState.JOINED_GAME)
		{
			Team team = getTeam(msPlayer);
			Structure<? extends GameMap> mapStructure = getMapStructure();
			GameMap map = mapStructure.getScheme();
			
			if(team != null && !isDead(msPlayer))
			{
				RegionList spawnRegion = map.getSpawn(team);
				Point base = map.getBase();
				spawnPoint = mapStructure.getAbsolutePoint(spawnRegion.getRandomSpawnablePoint(base, MSConstant.RANDOM));
				
				msPlayer.showRankInfo(Round.SPAWN_TIME);
				
				if(spawnPoint == null)
				{
					msPlayer.sendMessage(ChatColor.RED + "Spawnpoint obscured!");
					return null;
				}
			}
			else
			{
				spawnPoint = mapStructure.getAbsolutePoint(map.getSpectatorSpawn());
			}
		}
		else
		{
			msPlayer.quitScene(SceneQuitReason.ERROR_INVALID_PLAYER_STATE, true);
			return null;
		}
		
		Location spawnLocation = spawnPoint.getLocation(MSConfig.getWorld(), 0.5, 0, 0.5);
		
		if(teleport)
			msPlayer.teleport(spawnLocation);
		
		return spawnLocation;
	}
	
	@Override
	public void equip(MSPlayer msPlayer, boolean force)
	{
		if(force || isDead(msPlayer))
		{
			PlayerState state = msPlayer.getPlayerState();
			
			msPlayer.clearInventory();
			msPlayer.getPlayer().updateInventory();
			
			if(state != PlayerState.JOINED_GAME || !hasTeam(msPlayer))
				return;
		}
		
		DefuseEquipmentProvider ep = getEquipmentProvider();
		
		ep.removeBomb(msPlayer);
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
	
	@Override
	public String getTabHeader(MSPlayer msPlayer)
	{
		return Translation.TAB_HEADER.getMessage();
	}
	
	@Override
	public String getTabFooter(MSPlayer msPlayer)
	{
		int ctAlive = 0;
		int tAlive = 0;
		
		for(MSPlayer currentPlayer : getPlayers())
		{
			if(isDead(currentPlayer))
				continue;
			
			Team team = getTeam(currentPlayer);
			
			if(team == Team.COUNTER_TERRORISTS)
				ctAlive++;
			else if(team == Team.TERRORISTS)
				tAlive++;
		}
		
		return Translation.TAB_GAME_DEFUSE_FOOTER.getMessage(ctScore, tScore, ctAlive, tAlive);
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
	
	public boolean isBombPlaced()
	{
		return bombBlock != null;
	}
	
	public Block getBombBlock()
	{
		return bombBlock;
	}

	public void setBombBlock(Block bombBlock)
	{
		this.bombBlock = bombBlock;
	}
	
	public int getWinsInRow()
	{
		return winsInRow;
	}

	public void setWinsInRow(int winsInRow)
	{
		this.winsInRow = winsInRow;
	}

	public Team getLastWinner()
	{
		return lastWinner;
	}

	public void setLastWinner(Team lastWinner)
	{
		this.lastWinner = lastWinner;
	}
	
	public boolean isBombGiven()
	{
		return bombGiven;
	}
	
	@Override
	public int getXPForKill(MSPlayer msVictim, MSPlayer msKiller)
	{
		return XP_KILL;
	}
	
	@Override
	public DefuseEquipmentProvider getEquipmentProvider()
	{
		return (DefuseEquipmentProvider) super.getEquipmentProvider();
	}
	
	@Override
	public Structure<? extends GameMap> setMap(GameMap map)
	{
		if(!(map instanceof DefuseGameMap))
			throw new IllegalArgumentException("The map must be an instance of DefuseGameMap.");
		
		return super.setMap(map);
	}
	
	@Override
	public void setMapStructure(Structure<GameMap> mapStructure)
	{
		GameMap map = mapStructure.getScheme();
		
		if(!(map instanceof DefuseGameMap))
			throw new IllegalArgumentException("The map must be an instance of DefuseGameMap.");
		
		super.setMapStructure(mapStructure);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Structure<? extends DefuseGameMap> getMapStructure()
	{
		return (Structure<? extends DefuseGameMap>) super.getMapStructure();
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
