package cz.minestrike.me.limeth.minestrike.scene.games.team.deathmatch;

import com.google.common.collect.Maps;
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
import cz.minestrike.me.limeth.minestrike.events.GameQuitEvent;
import cz.minestrike.me.limeth.minestrike.events.GameSpawnEvent;
import cz.minestrike.me.limeth.minestrike.scene.games.*;
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

import java.util.HashMap;
import java.util.function.Predicate;

public class DeathMatchGame extends TeamGame
{
    public static final int HEALTH_ASSIST_OFFSET = -25, XP_KILL = 20, XP_MATCH_WIN = 150, XP_MATCH_LOSE = 40;
    private final HashMap<String, Integer> score = Maps.newHashMap();

    public DeathMatchGame(String id, String name, MSPlayer owner, boolean open, String lobbyId, String menuId, FilledArrayList<String> maps) {
        super(GameType.DEATH_MATCH, id, name, owner, open, lobbyId, menuId, maps);
    }

    public DeathMatchGame(String id, String name) {
        this(id, name, null, true, "lobby_global_deathmatch", "menu_global_deathmatch", new FilledArrayList<>());
    }

    @Override
    public DeathMatchGame setup()
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

        DeathMatchRound round = new DeathMatchRound(this);

        getPlayingPlayers().forEach(cz.minestrike.me.limeth.minestrike.MSPlayer::clearTemporaryContainers);
        clearScore();
        setPhase(round);
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
        defuseGameListener.redirect(event, msPlayer);
        defuseRewardListener.redirect(event, msPlayer);

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
            setDead(msPlayer, false);
            spawnAndEquip(msPlayer, false);
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
            DefuseRound round = getRound();
            DefuseRound.DefuseRoundPhase roundPhase = round.getPhase();
            Long time = null;

            if(roundPhase == DefuseRound.DefuseRoundPhase.PREPARING)
                time = DefuseRound.SPAWN_TIME;
            else if(roundPhase == DefuseRound.DefuseRoundPhase.STARTED)
                time = DefuseRound.ROUND_TIME;
            else if(roundPhase == DefuseRound.DefuseRoundPhase.PLANTED)
                time = DefuseRound.BOMB_TIME;

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

    public void roundEnd()
    {
        int newScore = addScore(victorTeam, 1);
        DefuseRound round = getRound();

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
                        .startCountdown(DefuseRound.END_TIME).setLines(endMessage)
                        .setDistance(2);

                DynamicDisplays.setDisplay(player, display);
            }

            round.setPhase(DefuseRound.DefuseRoundPhase.ENDED);
            round.startNextRunnable();
        }
    }

    public void matchEnd()
    {
        DefuseRound round = getRound();
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
                    .startCountdown(DefuseRound.VOTE_TIME).setLines(endMessages[1], endMessages[2], endMessages)
                    .setDistance(2);

            DynamicDisplays.setDisplay(player, display);
        }

        round.setPhase(DefuseRound.DefuseRoundPhase.ENDED);
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

        Location spawnLoc = spawnAndEquip(msPlayer, true);

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

    public DefuseRound getRound()
    {
        GamePhase<? extends Game> phase = getPhase();

        if(!(phase instanceof DefuseRound))
            throw new RuntimeException("The current phase isn't an instance of DefuseRound");

        return (DefuseRound) phase;
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

    public void clearScore()
    {
        score.clear();
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
            Structure<? extends DeathMatchGameMap> mapStructure = getMapStructure();
            DeathMatchGameMap map = mapStructure.getScheme();
            RegionList spawnRegion = map.getSpawnZones();
            Point base = map.getBase();
            spawnPoint = mapStructure.getAbsolutePoint(spawnRegion.getRandomSpawnablePoint(base, MSConstant.RANDOM));

            msPlayer.showRankInfo(DefuseRound.SPAWN_TIME);

            if(spawnPoint == null)
            {
                msPlayer.sendMessage(ChatColor.RED + "Spawnpoint obscured!");
                return null;
            }
        }
        else
        {
            msPlayer.quitScene(GameQuitEvent.SceneQuitReason.ERROR_INVALID_PLAYER_STATE, true);
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
            Team team = getTeam(currentPlayer);

            if(team == Team.COUNTER_TERRORISTS)
                ctAlive++;
            else if(team == Team.TERRORISTS)
                tAlive++;
        }

        return Translation.TAB_GAME_DEFUSE_FOOTER.getMessage(ctScore, tScore, ctAlive, tAlive);
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
    public DefuseEquipmentProvider getEquipmentProvider()
    {
        return (DefuseEquipmentProvider) super.getEquipmentProvider();
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
