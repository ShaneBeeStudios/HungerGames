package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.events.GameEndEvent;
import com.shanebeestudios.hg.events.GameStartEvent;
import com.shanebeestudios.hg.game.GameCommandData.CommandType;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.managers.MobManager;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.tasks.ChestDropTask;
import com.shanebeestudios.hg.tasks.FreeRoamTask;
import com.shanebeestudios.hg.tasks.Rollback;
import com.shanebeestudios.hg.tasks.SpawnerTask;
import com.shanebeestudios.hg.tasks.StartingTask;
import com.shanebeestudios.hg.tasks.TimerTask;
import com.shanebeestudios.hg.util.Util;
import com.shanebeestudios.hg.util.Vault;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * General game object
 */
@SuppressWarnings("unused")
public class Game {

    final HG plugin;
    final Language lang;

    // Managers
    KitManager kitManager;
    private final MobManager mobManager;
    private final PlayerManager playerManager;

    // Task ID's here!
    private SpawnerTask spawner;
    private FreeRoamTask freeRoam;
    private StartingTask starting;
    private TimerTask timer;
    private ChestDropTask chestDrop;

    // Data Objects
    final GameArenaData gameArenaData;
    final GameBarData bar;
    final GamePlayerData gamePlayerData;
    final GameBlockData gameBlockData;
    final GameItemData gameItemData;
    final GameCommandData gameCommandData;
    final GameBorderData gameBorderData;

    /**
     * Create a new game
     * <p>Internally used when loading from config on server start</p>
     *
     * @param name       Name of this game
     * @param bound      Bounding region of this game
     * @param spawns     List of spawns for this game
     * @param lobbySign  Lobby sign block
     * @param timer      Length of the game (in seconds)
     * @param minPlayers Minimum players to be able to start the game
     * @param maxPlayers Maximum players that can join this game
     * @param roam       Roam time for this game
     * @param isReady    If the game is ready to start
     * @param cost       Cost of this game
     */
    public Game(String name, Bound bound, List<Location> spawns, Sign lobbySign, int timer, int minPlayers, int maxPlayers, int roam, boolean isReady, int cost) {
        this(name, bound, timer, minPlayers, maxPlayers, roam, cost);
        gameArenaData.spawns.addAll(spawns);
        this.gameBlockData.sign1 = lobbySign;

        // If lobby signs are not properly setup, game is not ready
        if (!this.gameBlockData.setLobbyBlock(lobbySign)) {
            isReady = false;
        }
        gameArenaData.setStatus(isReady ? Status.READY : Status.BROKEN);

        this.kitManager = plugin.getKitManager();
    }

    /**
     * Create a new game
     * <p>Internally used when creating a game with the <b>/hg create</b> command</p>
     *
     * @param name       Name of this game
     * @param bound      Bounding region of this game
     * @param timer      Length of the game (in seconds)
     * @param minPlayers Minimum players to be able to start the game
     * @param maxPlayers Maximum players that can join this game
     * @param roam       Roam time for this game
     * @param cost       Cost of this game
     */
    public Game(String name, Bound bound, int timer, int minPlayers, int maxPlayers, int roam, int cost) {
        this.plugin = HG.getPlugin();
        this.gameArenaData = new GameArenaData(this, name, bound, timer, minPlayers, maxPlayers, roam, cost);
        this.gameArenaData.status = Status.NOTREADY;
        this.playerManager = HG.getPlugin().getPlayerManager();
        this.lang = plugin.getLang();
        this.kitManager = plugin.getKitManager();
        this.mobManager = new MobManager(this);
        this.bar = new GameBarData(this);
        this.gamePlayerData = new GamePlayerData(this);
        this.gameBlockData = new GameBlockData(this);
        this.gameItemData = new GameItemData(this);
        this.gameCommandData = new GameCommandData(this);
        this.gameBorderData = new GameBorderData(this);
        this.gameBorderData.setBorderSize(Config.borderFinalSize);
        this.gameBorderData.setBorderTimer(Config.borderCountdownStart, Config.borderCountdownEnd);
    }

    /**
     * Get an instance of the GameArenaData
     *
     * @return Instance of GameArenaData
     */
    public GameArenaData getGameArenaData() {
        return gameArenaData;
    }

    /**
     * Get an instance of the GameBarData
     *
     * @return Instance of GameBarData
     */
    public GameBarData getGameBarData() {
        return bar;
    }

    /**
     * Get an instance of the GamePlayerData
     *
     * @return Instance of GamePlayerData
     */
    public GamePlayerData getGamePlayerData() {
        return gamePlayerData;
    }

    /**
     * Get an instance of the GameBlockData
     *
     * @return Instance of GameBlockData
     */
    public GameBlockData getGameBlockData() {
        return gameBlockData;
    }

    /**
     * Get an instance of the GameItemData
     *
     * @return Instance of GameItemData
     */
    public GameItemData getGameItemData() {
        return gameItemData;
    }

    /**
     * Get an instance of the GameCommandData
     *
     * @return Instance of GameCommandData
     */
    public GameCommandData getGameCommandData() {
        return gameCommandData;
    }

    /**
     * Get an instance of the GameBorderData
     *
     * @return Instance of GameBorderData
     */
    public GameBorderData getGameBorderData() {
        return gameBorderData;
    }

    public StartingTask getStartingTask() {
        return this.starting;
    }

    /**
     * Get the location of the lobby for this game
     *
     * @return Location of the lobby sign
     */
    public Location getLobbyLocation() {
        return gameBlockData.sign1.getLocation();
    }

    /**
     * Get the kits for this game
     *
     * @return The KitManager kit for this game
     */
    public KitManager getKitManager() {
        return this.kitManager;
    }

    /**
     * Set the kits for this game
     *
     * @param kit The KitManager kit to set
     */
    @SuppressWarnings("unused")
    public void setKitManager(KitManager kit) {
        this.kitManager = kit;
    }

    /**
     * Get this game's MobManager
     *
     * @return MobManager for this game
     */
    public MobManager getMobManager() {
        return this.mobManager;
    }

    /**
     * Start the pregame countdown
     */
    public void startPreGame() {
        // Call the GameStartEvent
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        gameArenaData.status = Status.COUNTDOWN;
        starting = new StartingTask(this);
        gameBlockData.updateLobbyBlock();
    }

    /**
     * Start the free roam state of the game
     */
    public void startFreeRoam() {
        gameArenaData.status = Status.BEGINNING;
        gameBlockData.updateLobbyBlock();
        gameArenaData.bound.removeEntities();
        freeRoam = new FreeRoamTask(this);
        gameCommandData.runCommands(CommandType.START, null);
    }

    /**
     * Start the game
     */
    public void startGame() {
        gameArenaData.status = Status.RUNNING;
        if (Config.spawnmobs) spawner = new SpawnerTask(this, Config.spawnmobsinterval);
        if (Config.randomChest) chestDrop = new ChestDropTask(this);
        gameBlockData.updateLobbyBlock();
        if (Config.bossbar) {
            bar.createBossbar(gameArenaData.timer);
        }
        if (Config.borderEnabled && Config.borderOnStart) {
            gameBorderData.setBorder(gameArenaData.timer);
        }
        timer = new TimerTask(this, gameArenaData.timer);
    }

    public void cancelTasks() {
        if (spawner != null) spawner.stop();
        if (timer != null) timer.stop();
        if (starting != null) starting.stop();
        if (freeRoam != null) freeRoam.stop();
        if (chestDrop != null) chestDrop.shutdown();
    }

    /**
     * Stop the game
     */
    public void stop() {
        stop(false);
    }

    /**
     * Stop the game
     *
     * @param death Whether the game stopped after the result of a death (false = no winnings payed out)
     */
    public void stop(Boolean death) {
        if (Config.borderEnabled) {
            gameBorderData.resetBorder();
        }
        gameArenaData.bound.removeEntities();
        List<UUID> win = new ArrayList<>();
        cancelTasks();
        for (UUID uuid : gamePlayerData.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                PlayerData playerData = playerManager.getPlayerData(uuid);
                Location previousLocation = playerData.getPreviousLocation();

                gamePlayerData.heal(player);
                playerData.restore(player);
                win.add(uuid);
                gamePlayerData.exit(player, previousLocation);
                playerManager.removePlayerData(uuid);
            }
        }

        for (UUID uuid : gamePlayerData.getSpectators()) {
            Player spectator = Bukkit.getPlayer(uuid);
            if (spectator != null) {
                gamePlayerData.leaveSpectate(spectator);
            }
        }

        if (gameArenaData.status == Status.RUNNING) {
            bar.clearBar();
        }

        if (!win.isEmpty() && death) {
            double db = (double) Config.cash / win.size();
            for (UUID u : win) {
                if (Config.giveReward) {
                    Player p = Bukkit.getPlayer(u);
                    assert p != null;
                    if (!Config.rewardCommands.isEmpty()) {
                        for (String cmd : Config.rewardCommands) {
                            if (!cmd.equalsIgnoreCase("none"))
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<player>", p.getName()));
                        }
                    }
                    if (!Config.rewardMessages.isEmpty()) {
                        for (String msg : Config.rewardMessages) {
                            if (!msg.equalsIgnoreCase("none"))
                                Util.scm(p, msg.replace("<player>", p.getName()));
                        }
                    }
                    if (Config.cash != 0) {
                        Vault.economy.depositPlayer(Bukkit.getServer().getOfflinePlayer(u), db);
                        Util.scm(p, lang.winning_amount.replace("<amount>", String.valueOf(db)));
                    }
                }
                plugin.getLeaderboard().addStat(u, Leaderboard.Stats.WINS);
                plugin.getLeaderboard().addStat(u, Leaderboard.Stats.GAMES);
            }
        }
        gameBlockData.clearChests();
        String winner = Util.translateStop(Util.convertUUIDListToStringList(win));

        // Broadcast wins
        if (death) {
            String broadcast = lang.player_won.replace("<arena>", gameArenaData.name).replace("<winner>", winner);
            if (Config.broadcastWinMessages) {
                Util.broadcast(broadcast);
            } else {
                gamePlayerData.msgAllPlayers(broadcast);
            }
        }
        if (gameBlockData.requiresRollback()) {
            if (plugin.isEnabled()) {
                new Rollback(this);
            } else {
                // Force rollback if server is stopping
                gameBlockData.forceRollback();
            }
        } else {
            gameArenaData.status = Status.READY;
            gameBlockData.updateLobbyBlock();
        }
        gameArenaData.updateBoards();
        gameCommandData.runCommands(CommandType.STOP, null);

        // Call GameEndEvent
        Collection<Player> winners = new ArrayList<>();
        for (UUID uuid : win) {
            winners.add(Bukkit.getPlayer(uuid));
        }

        // Game has ended, we can clear all players now
        gamePlayerData.clearPlayers();
        gamePlayerData.clearSpectators();
        gamePlayerData.clearTeams();
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this, winners, death));
    }

    void updateAfterDeath(Player player, boolean death) {
        Status status = gameArenaData.status;
        if (status == Status.RUNNING || status == Status.BEGINNING || status == Status.COUNTDOWN) {
            if (isGameOver()) {
                if (!death) {
                    for (UUID uuid : gamePlayerData.players) {
                        if (gamePlayerData.kills.get(Bukkit.getPlayer(uuid)) >= 1) {
                            death = true;
                        }
                    }
                }
                boolean finalDeath = death;
                if (plugin.isEnabled()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        stop(finalDeath);
                        gameBlockData.updateLobbyBlock();
                        gameArenaData.updateBoards();
                    }, 20);
                } else {
                    stop(finalDeath);
                }

            }
        } else if (status == Status.WAITING) {
            gamePlayerData.msgAll(lang.player_left_game
                    .replace("<arena>", gameArenaData.getName())
                    .replace("<player>", player.getName()) +
                    (gameArenaData.minPlayers - gamePlayerData.players.size() <= 0 ? "!" : ": " + lang.players_to_start
                            .replace("<amount>", String.valueOf((gameArenaData.minPlayers - gamePlayerData.players.size())))));
        }
        gameBlockData.updateLobbyBlock();
        gameArenaData.updateBoards();
    }

    boolean isGameOver() {
        if (gamePlayerData.players.size() <= 1) return true;
        for (UUID uuid : gamePlayerData.players) {
            Team team = playerManager.getPlayerData(uuid).getTeam();

            if (team != null && (team.getPlayers().size() >= gamePlayerData.players.size())) {
                for (UUID u : gamePlayerData.players) {
                    if (!team.getPlayers().contains(u)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Game{name='" + gameArenaData.name + '\'' + ", bound=" + gameArenaData.bound + '}';
    }

}
