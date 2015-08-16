package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.minestrike.me.limeth.minestrike.MSConfig;
import cz.minestrike.me.limeth.minestrike.MSConstant;
import cz.minestrike.me.limeth.minestrike.MSPlayer;
import cz.minestrike.me.limeth.minestrike.Translation;
import cz.minestrike.me.limeth.minestrike.areas.Point;
import cz.minestrike.me.limeth.minestrike.areas.RegionList;
import cz.minestrike.me.limeth.minestrike.areas.Structure;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameLobby;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMap;
import cz.minestrike.me.limeth.minestrike.areas.schemes.GameMenu;
import cz.minestrike.me.limeth.minestrike.events.ArenaJoinEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.SceneSpawnEvent;
import cz.minestrike.me.limeth.minestrike.scene.Scene;
import cz.minestrike.me.limeth.minestrike.scene.games.*;
import cz.minestrike.me.limeth.minestrike.scene.games.listeners.RewardMSListener;
import cz.minestrike.me.limeth.minestrike.scene.games.team.RadarView;
import cz.minestrike.me.limeth.minestrike.scene.games.team.TeamGame;
import cz.minestrike.me.limeth.minestrike.scene.games.team.defuse.*;
import cz.minestrike.me.limeth.minestrike.util.SoundManager;
import cz.minestrike.me.limeth.minestrike.util.collections.FilledArrayList;
import cz.projectsurvive.limeth.dynamicdisplays.DynamicDisplays;
import cz.projectsurvive.limeth.dynamicdisplays.PlayerDisplay;
import cz.projectsurvive.limeth.dynamicdisplays.TimedPlayerDisplay;
import ftbastler.HeadsUpDisplay;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DeathMatchGame extends TeamGame
{
    public static final String CUSTOM_DATA_MOVED = "MineStrike.game.moved";
    public static final int HEALTH_ASSIST_OFFSET = -25, XP_KILL = 20, XP_MATCH_WIN = 150, XP_MATCH_LOSE = 40;
    private Map<String, Integer>             score;
    private DeathMatchGameMSListener         gameListener;
    private RewardMSListener<DeathMatchGame> rewardListener;

    public DeathMatchGame(String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId,
                          FilledArrayList<String> maps)
    {
        super(GameType.DEATHMATCH, id, name, owner, open, lobbyId, menuId, maps);
    }

    public DeathMatchGame(String id, String name)
    {
        this(id, name, null, true, "lobby_global_deathmatch", "menu_global_deathmatch", new FilledArrayList<>());
    }

    @Override
    public DeathMatchGame setup()
    {
        super.setup();

        score = Maps.newHashMap();
        gameListener = new DeathMatchGameMSListener(this);
        rewardListener = new DeathMatchRewardMSListener(this);

        return this;
    }

    @Override
    public void start()
    {
        super.start();

        DeathMatchRound round = new DeathMatchRound(this);

        getPlayingPlayers().forEach(cz.minestrike.me.limeth.minestrike.MSPlayer::clearTemporaryContainers);
        clearScore();
        setPhase(round);
        gameListener.start();
    }

    @Override
    public RadarView createRadarView()
    {
        return new RadarView(this); //TODO possibly implement a separate radar view for this gamemode?
    }

    @Override
    public void redirect(Event event, MSPlayer msPlayer)
    {
        super.redirect(event, msPlayer);
        gameListener.redirect(event, msPlayer);
        rewardListener.redirect(event, msPlayer);

        if(getPhaseType() == GamePhaseType.RUNNING)
            getRound().redirect(event, msPlayer);
    }

    public boolean roundPrepare()
    {
        clearDrops();
        repairDamagedBlocks();

        for(MSPlayer msPlayer : getPlayingPlayers())
        {
            Player player = msPlayer.getPlayer();

            player.setWalkSpeed(0);
            msPlayer.clearReceivedDamage();
            spawn(msPlayer, true); //spawnAndEquip(msPlayer, false);
            showWitherBar(msPlayer);
        }

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
            DeathMatchRound round = getRound();
            RoundPhase roundPhase = round.getPhase();
            long time = roundPhase.getDuration();
            long nowMillis = System.currentTimeMillis();
            long ranAtMillis = round.getRanAt();
            long differenceMillis = nowMillis - ranAtMillis;
            double difference = differenceMillis * 20D / 1000D;

            HeadsUpDisplay.displayLoadingBar(getWitherTitle(), player, difference, time, false, () -> HeadsUpDisplay.displayTextBar(getWitherTitle(), player));

            return;
        }

        HeadsUpDisplay.displayTextBar(getWitherTitle(), player);
    }

    public void removeWitherBar(MSPlayer msPlayer)
    {
        HeadsUpDisplay.removeBar(msPlayer.getPlayer());
    }

    public Map<MSPlayer, Integer> getScoreOfPlayingPlayers()
    {
        Map<MSPlayer, Integer> result = Maps.newHashMap();

        for(Map.Entry<String, Integer> entry : score.entrySet())
        {
            String playerName = entry.getKey();
            Player player = Bukkit.getPlayerExact(playerName);

            if(player == null)
                continue;

            MSPlayer msPlayer = MSPlayer.get(player);

            if(msPlayer == null)
                continue;

            Scene scene = msPlayer.getScene();

            if(scene != this || !isPlayerPlaying(msPlayer))
                continue;

            int individualScore = entry.getValue();

            result.put(msPlayer, individualScore);
        }

        return result;
    }

    public Map<String, Integer> getScore()
    {
        return Maps.newHashMap(score);
    }

    public String getWitherTitle()
    {
        return ChatColor.RED + "WITHER TITLE | NEDOKONCENO";
    }

    private Set<MSPlayer> computeWinners()
    {
        Map<MSPlayer, Integer> activeScore = getScoreOfPlayingPlayers();

        if(Sets.newHashSet(activeScore.values()).size() > 1)
        {
            Set<MSPlayer> winners = Sets.newHashSet();
            int highestScore = 0;

            for(Map.Entry<MSPlayer, Integer> entry : activeScore.entrySet())
            {
                int individualScore = entry.getValue();

                if(individualScore > highestScore)
                {
                    winners.clear();

                    highestScore = individualScore;
                }

                if(individualScore >= highestScore)
                {
                    MSPlayer msPlayer = entry.getKey();

                    winners.add(msPlayer);
                }
            }

            return winners;
        }

        return Sets.newHashSet();
    }

    public void roundEnd()
    {
        DeathMatchRound round = getRound();

        for(MSPlayer msPlayer : getPlayingPlayers())
        {
            Player player = msPlayer.getPlayer();

            player.setWalkSpeed(0);
        }

        updateTabHeadersAndFooters();

        Set<MSPlayer> winners = computeWinners();
        String firstMessage, secondMessage;

        if(winners.size() > 0)
        {
            boolean plural = winners.size() > 1;
            String winnersString = Joiner.on(ChatColor.DARK_GRAY + ", " + ChatColor.RESET).join(
                    winners.stream().map(MSPlayer::getNameTag).collect(Collectors.toSet())
            );

            if(plural)
            {
                firstMessage = Translation.GAME_MATCH_END_1_PLURAL.getMessage(winnersString);
                secondMessage = Translation.GAME_MATCH_END_2_PLURAL.getMessage(winnersString);
            }
            else
            {
                firstMessage = Translation.GAME_MATCH_END_1_SINGULAR.getMessage(winnersString);
                secondMessage = Translation.GAME_MATCH_END_2_SINGULAR.getMessage(winnersString);
            }
        }
        else
        {
            firstMessage = Translation.GAME_MATCH_END_1_NONE.getMessage();
            secondMessage = Translation.GAME_MATCH_END_2_NONE.getMessage();
        }

        for (MSPlayer msPlayer : getPlayingPlayers(p -> p.getPlayerState() == PlayerState.JOINED_GAME))
        {
            Player player = msPlayer.getPlayer();
            String decor = "× × ×";
            PlayerDisplay display = new TimedPlayerDisplay(player)
                    .startCountdown(DefuseRound.PHASE_POLL.getDuration()).setLines(firstMessage, secondMessage, new String[]{decor, firstMessage, secondMessage, decor})
                    .setDistance(2);

            DynamicDisplays.setDisplay(player, display);
        }

        rewardListener.rewardPlayers();
        round.setPhase(DeathMatchRound.PHASE_END);
        round.startVoteRunnable();
    }

    public void roundNext()
    {
        getRound().start();
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

        msPlayer.clearReceivedDamage();
        setTeam(msPlayer, team);
        msPlayer.setPlayerStructure(getMapStructure());
        msPlayer.setPlayerState(PlayerState.JOINED_GAME);

        Location spawnLoc = spawn(msPlayer, true); //spawnAndEquip(msPlayer, true);

        if(spawnLoc == null)
            return false;

        showWitherBar(msPlayer);

        if(team != null)
            updateTabHeadersAndFooters();

        msPlayer.updateNameTag();
        msPlayer.sendMessage(Team.getJoinMessage(team).getMessage());
        return true;
    }

    @Override
    public final boolean isPlayerPlaying(MSPlayer p)
    {
        return p.getPlayerState() == PlayerState.JOINED_GAME && getTeam(p) != null;
    }

    @Override
    public boolean isPlayerSpectating(MSPlayer msPlayer)
    {
        return msPlayer.getPlayerState() == PlayerState.JOINED_GAME && getTeam(msPlayer) == null;
    }

    public DeathMatchRound getRound()
    {
        GamePhase<? extends Game> phase = getPhase();

        if(!(phase instanceof DeathMatchRound))
            throw new RuntimeException("The current phase isn't an instance of DeathMatchRound");

        return (DeathMatchRound) phase;
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

/*    public Location spawnAndEquip(MSPlayer msPlayer, boolean force)
    {
        equip(msPlayer, force);

        return spawn(msPlayer, true);
    }*/

    public void clearScore()
    {
        score.clear();
    }

    public boolean hasMoved(MSPlayer msPlayer)
    {
        return msPlayer.getCustomData(CUSTOM_DATA_MOVED, false);
    }

    public void setMoved(MSPlayer msPlayer, boolean moved)
    {
        msPlayer.setCustomData(CUSTOM_DATA_MOVED, moved);
    }

    @Override
    protected Optional<Location> doSpawn(MSPlayer msPlayer, boolean teleport)
    {
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
            Structure<? extends DeathMatchGameMap> mapStructure = getMapStructure();
            DeathMatchGameMap map = mapStructure.getScheme();
            RegionList spawnRegion = map.getSpawnZones();
            Point base = map.getBase();
            spawnPoint = mapStructure.getAbsolutePoint(spawnRegion.getRandomSpawnablePoint(base, MSConstant.RANDOM));

            msPlayer.showRankInfo(DefuseRound.PHASE_PREPARATION.getDuration());

            if(spawnPoint == null)
            {
                msPlayer.sendMessage(ChatColor.RED + "Spawnpoint obscured!");
                return Optional.empty();
            }
        }
        else
        {
            msPlayer.quitScene(SceneQuitEvent.SceneQuitReason.ERROR_INVALID_PLAYER_STATE, true);
            return Optional.empty();
        }

        Location spawnLocation = spawnPoint.getLocation(MSConfig.getWorld(), 0.5, 0, 0.5);

        equip(msPlayer, false);
        setMoved(msPlayer, false);
        gameListener.resetMovedRunnable(msPlayer);

        return Optional.of(spawnLocation);
    }

    @Override
    public void equip(MSPlayer msPlayer, boolean force)
    {
        if(force)
        {
            PlayerState state = msPlayer.getPlayerState();

            msPlayer.clearInventory();
            msPlayer.getPlayer().updateInventory();

            if(state != PlayerState.JOINED_GAME || !hasTeam(msPlayer))
                return;
        }

        super.equip(msPlayer, force);
    }

    @Override
    public String getTabHeader(MSPlayer msPlayer)
    {
        return Translation.TAB_HEADER.getMessage();
    }

    @Override
    public String getTabFooter(MSPlayer msPlayer)
    {
        return "TAB FOOTER | NEDOKONCENO";
    }

    @Override
    public int getXPForKill(MSPlayer msVictim, MSPlayer msKiller)
    {
        return XP_KILL;
    }

    @Override
    public int getXPForAssist(MSPlayer msVictim, MSPlayer msAssistant)
    {
        double dmg = msVictim.getReceivedDamage(msAssistant);

        return (int) Math.ceil((dmg * 5) + HEALTH_ASSIST_OFFSET);
    }

    @Override
    public DeathMatchEquipmentProvider getEquipmentProvider()
    {
        return (DeathMatchEquipmentProvider) super.getEquipmentProvider();
    }

    @Override
    public Structure<? extends GameMap> setMap(GameMap map)
    {
        if(!(map instanceof DeathMatchGameMap))
            throw new IllegalArgumentException("The map must be an instance of DeathMatchGameMap.");

        return super.setMap(map);
    }

    @Override
    public void setMapStructure(Structure<GameMap> mapStructure)
    {
        GameMap map = mapStructure.getScheme();

        if(!(map instanceof DeathMatchGameMap))
            throw new IllegalArgumentException("The map must be an instance of DeathMatchGameMap.");

        super.setMapStructure(mapStructure);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Structure<? extends DeathMatchGameMap> getMapStructure()
    {
        return (Structure<? extends DeathMatchGameMap>) super.getMapStructure();
    }

    @Deprecated
    @Override
    public void setDead(MSPlayer msPlayer, boolean value)
    {
        throw new IllegalStateException("Cannot set whether a player is dead in death match.");
    }

    @Deprecated
    @Override
    public boolean isDead(MSPlayer msPlayer)
    {
        return false;
    }

    @Deprecated
    @Override
    public boolean isDead(Team team)
    {
        return false;
    }
}
