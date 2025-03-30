package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Vault;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.events.GameEndEvent;
import com.shanebeestudios.hg.events.GameStartEvent;
import com.shanebeestudios.hg.events.PlayerJoinGameEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * General game object
 */
@SuppressWarnings("unused")
public class Game {

    final HungerGames plugin;
    final Language lang;

    // Managers
    KitManager kitManager;
    private final MobManager mobManager;
    private final PlayerManager playerManager;

    // Task ID's here!
    private SpawnerTask spawner;
    private FreeRoamTask freeRoamTask;
    private StartingTask startingTask;
    private TimerTask timer;
    private ChestDropTask chestDrop;

    // Data Objects
    final GameArenaData gameArenaData;
    final GameScoreboard gameScoreboard;
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
     * @param gameRegion      Bounding region of this game
     * @param spawns     List of spawns for this game
     * @param lobbySign  Lobby sign block
     * @param timer      Length of the game (in seconds)
     * @param minPlayers Minimum players to be able to start the game
     * @param maxPlayers Maximum players that can join this game
     * @param roam       Roam time for this game
     * @param isReady    If the game is ready to start
     * @param cost       Cost of this game
     */
    public Game(String name, GameRegion gameRegion, List<Location> spawns, Sign lobbySign, int timer, int minPlayers, int maxPlayers, int roam, boolean isReady, int cost) {
        this(name, gameRegion, timer, minPlayers, maxPlayers, roam, cost);
        gameArenaData.spawns.addAll(spawns);
        this.gameBlockData.setSign(lobbySign);

        // If lobby signs are not properly setup, game is not ready
        if (!this.gameBlockData.setLobbyBlock(lobbySign)) {
            isReady = false;
        }
        this.gameArenaData.setStatus(isReady ? Status.READY : Status.BROKEN);
        this.gameBlockData.updateLobbyBlock();

        this.kitManager = plugin.getKitManager();
    }

    /**
     * Create a new game
     * <p>Internally used when creating a game with the <b>/hg create</b> command</p>
     *
     * @param name       Name of this game
     * @param gameRegion      Bounding region of this game
     * @param timer      Length of the game (in seconds)
     * @param minPlayers Minimum players to be able to start the game
     * @param maxPlayers Maximum players that can join this game
     * @param roam       Roam time for this game
     * @param cost       Cost of this game
     */
    public Game(String name, GameRegion gameRegion, int timer, int minPlayers, int maxPlayers, int roam, int cost) {
        this.plugin = HungerGames.getPlugin();
        this.lang = plugin.getLang();
        this.gameArenaData = new GameArenaData(this, name, gameRegion, timer, minPlayers, maxPlayers, roam, cost);
        this.gameArenaData.status = Status.NOT_READY;
        this.gameScoreboard = new GameScoreboard(this);
        this.playerManager = HungerGames.getPlugin().getPlayerManager();
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
     * Get an instance of the GameScoreboard
     *
     * @return Instance of GameScoreboard
     */
    public GameScoreboard getGameScoreboard() {
        return this.gameScoreboard;
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
        return this.startingTask;
    }

    /**
     * Get the location of the lobby for this game
     *
     * @return Location of the lobby sign
     */
    public Location getLobbyLocation() {
        return this.gameBlockData.getSign().getLocation();
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
     * Initialize the waiting period of the game
     * <p>This will be called when a player first joins</p>
     */
    public void startWaitingPeriod() {
        this.gameArenaData.setStatus(Status.WAITING);
        this.gameBlockData.updateLobbyBlock();
        // TODO Broadcast?!?!?
    }

    /**
     * Start the pregame countdown
     */
    public void startPreGameCountdown() {
        // Call the GameStartEvent
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        this.gameArenaData.status = Status.COUNTDOWN;
        this.startingTask = new StartingTask(this);
        this.gameBlockData.updateLobbyBlock();
    }

    /**
     * Start the free roam state of the game
     */
    public void startFreeRoam() {
        this.gameArenaData.status = Status.FREE_ROAM;
        this.gameBlockData.updateLobbyBlock();
        this.gameArenaData.gameRegion.removeEntities();
        this.freeRoamTask = new FreeRoamTask(this);
        this.gameCommandData.runCommands(CommandType.START, null);
    }

    /**
     * Start running the game
     */
    public void startRunningGame() {
        this.gameArenaData.status = Status.RUNNING;
        if (Config.MOBS_SPAWN_ENABLED) spawner = new SpawnerTask(this);
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
        if (startingTask != null) startingTask.stop();
        if (freeRoamTask != null) freeRoamTask.stop();
        if (chestDrop != null) chestDrop.shutdown();
    }

    /**
     * Join a player to the game
     *
     * @param player Player to join the game
     */
    public boolean joinGame(Player player) {
        return joinGame(player, false);
    }

    /**
     * Join a player to the game
     *
     * @param player               Player to join the game
     * @param savePreviousLocation Whether to save the player's previous location
     */
    public boolean joinGame(Player player, boolean savePreviousLocation) {
        if (this.playerManager.isInGame(player)) {
            Util.sendPrefixedMessage(player, this.lang.cmd_join_already_in_game);
            return false;
        }
        // Call PlayerJoinGameEvent
        PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
        // If cancelled, stop the player from joining the game
        if (!event.callEvent()) return false;

        String arenaName = this.gameArenaData.getName();

        Status status = gameArenaData.getStatus();
        switch (status) {
            case NOT_READY, ROLLBACK, STOPPED, BROKEN -> {
                Util.sendPrefixedMessage(player, this.lang.arena_not_ready);
                return false;
            }
            case RUNNING, FREE_ROAM -> {
                Util.sendPrefixedMessage(player, this.lang.game_running.replace("<arena>", arenaName), arenaName);
                if (Config.spectateEnabled) {
                    Util.sendPrefixedMessage(player, this.lang.arena_spectate.replace("<arena>", arenaName));
                }
                return false;
            }
            case READY -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player);
                startWaitingPeriod();
            }
            case WAITING -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player);
                if (this.gamePlayerData.getPlayers().size() >= this.gameArenaData.getMinPlayers()) {
                    startPreGameCountdown();
                }
            }
            case COUNTDOWN -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player);
            }
        }

        this.gamePlayerData.putPlayerIntoArena(player, savePreviousLocation);
        return true;
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
            this.gameBorderData.resetBorder();
        }
        this.gameArenaData.gameRegion.removeEntities();
        this.gameScoreboard.resetBoards();

        // TODO win list should be players
        List<UUID> win = new ArrayList<>();
        cancelTasks();
        for (Player player : this.gamePlayerData.getPlayers()) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = this.playerManager.getPlayerData(uuid);
            Location previousLocation = playerData.getPreviousLocation();

            this.gamePlayerData.heal(player);
            playerData.restore(player);
            win.add(uuid);
            this.gamePlayerData.exit(player, previousLocation);
            this.playerManager.removePlayerData(uuid);
        }

        for (Player spectator : this.gamePlayerData.getSpectators()) {
            this.gamePlayerData.leaveSpectate(spectator);
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
                                Util.sendMessage(p, msg.replace("<player>", p.getName()));
                        }
                    }
                    if (Config.cash != 0) {
                        Vault.economy.depositPlayer(Bukkit.getServer().getOfflinePlayer(u), db);
                        Util.sendMessage(p, lang.winning_amount.replace("<amount>", String.valueOf(db)));
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
        gameCommandData.runCommands(CommandType.STOP, null);

        // Call GameEndEvent
        Collection<Player> winners = new ArrayList<>();
        for (UUID uuid : win) {
            winners.add(Bukkit.getPlayer(uuid));
        }

        // Game has ended, we can clear all players now
        gamePlayerData.clearPlayers();
        gamePlayerData.clearSpectators();
        this.gameScoreboard.clearGameTeams();
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this, winners, death));
    }

    void updateAfterDeath(Player player, boolean death) {
        Status status = gameArenaData.status;
        if (status == Status.RUNNING || status == Status.FREE_ROAM || status == Status.COUNTDOWN) {
            if (isGameOver()) {
                if (!death) {
                    for (Player player1 : this.gamePlayerData.getPlayers()) {
                        if (this.gamePlayerData.kills.get(player1) >= 1) {
                            death = true;
                        }
                    }
                }
                boolean finalDeath = death;
                if (plugin.isEnabled()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        stop(finalDeath);
                        gameBlockData.updateLobbyBlock();
                        this.gameScoreboard.updateBoards();
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
        this.gameScoreboard.updateBoards();
    }

    boolean isGameOver() {
        if (this.gamePlayerData.getPlayers().size() <= 1) return true;
        for (Player player : this.gamePlayerData.getPlayers()) {
            PlayerData playerData = this.playerManager.getPlayerData(player);
            GameTeam gameTeam = playerData.getTeam();

            if (gameTeam != null && (gameTeam.getPlayers().size() >= gamePlayerData.players.size())) {
                for (Player player1 : this.gamePlayerData.getPlayers()) {
                    if (!gameTeam.getPlayers().contains(player1.getUniqueId())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    boolean canJoin(Player player) {
        if (this.gamePlayerData.getPlayers().size() >= this.getGameArenaData().getMaxPlayers()) {
            Util.sendPrefixedMessage(player, this.lang.game_full);
            return false;
        }
        return vaultCheck(player);
    }

    boolean vaultCheck(Player player) {
        if (Config.economy) {
            int cost = this.getGameArenaData().getCost();
            if (Vault.economy.getBalance(player) >= cost) {
                Vault.economy.withdrawPlayer(player, cost);
                return true;
            } else {
                Util.sendMessage(player, lang.prefix + lang.cmd_join_no_money.replace("<cost>", String.valueOf(cost)));
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Game{name='" + this.gameArenaData.getName() + '\'' + ", bound=" + this.gameArenaData.getBound() + '}';
    }

}
