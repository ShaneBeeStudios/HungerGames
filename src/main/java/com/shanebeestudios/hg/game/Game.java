package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.events.GameEndEvent;
import com.shanebeestudios.hg.api.events.GameStartEvent;
import com.shanebeestudios.hg.api.events.PlayerJoinGameEvent;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Vault;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.GameCommandData.CommandType;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.managers.MobManager;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import com.shanebeestudios.hg.plugin.tasks.ChestDropTask;
import com.shanebeestudios.hg.plugin.tasks.ChestRefillRepeatTask;
import com.shanebeestudios.hg.plugin.tasks.NearestPlayerCompassTask;
import com.shanebeestudios.hg.plugin.tasks.FreeRoamTask;
import com.shanebeestudios.hg.plugin.tasks.GameTimerTask;
import com.shanebeestudios.hg.plugin.tasks.MobSpawnerTask;
import com.shanebeestudios.hg.plugin.tasks.RollbackTask;
import com.shanebeestudios.hg.plugin.tasks.StartingTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    // Tasks here!
    private MobSpawnerTask mobSpawnerTask;
    private FreeRoamTask freeRoamTask;
    private StartingTask startingTask;
    private GameTimerTask gameTimerTask;
    private ChestRefillRepeatTask chestRefillRepeatTask;
    private ChestDropTask chestDropTask;
    private NearestPlayerCompassTask nearestPlayerCompassTask;

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
     * @param name          Name of this game
     * @param gameRegion    Bounding region of this game
     * @param spawns        List of spawns for this game
     * @param lobbySign     Lobby sign block
     * @param gameTimerTask Length of the game (in seconds)
     * @param minPlayers    Minimum players to be able to start the game
     * @param maxPlayers    Maximum players that can join this game
     * @param roam          Roam time for this game
     * @param isReady       If the game is ready to start
     * @param cost          Cost of this game
     */
    public Game(String name, GameRegion gameRegion, List<Location> spawns, Location lobbySign, int gameTimerTask, int minPlayers, int maxPlayers, int roam, boolean isReady, int cost) {
        this.plugin = HungerGames.getPlugin();
        this.lang = plugin.getLang();
        this.gameArenaData = new GameArenaData(this, name, gameRegion, gameTimerTask, minPlayers, maxPlayers, roam, cost);
        this.gamePlayerData = new GamePlayerData(this);
        this.gameBlockData = new GameBlockData(this);
        this.gameScoreboard = new GameScoreboard(this);
        this.playerManager = HungerGames.getPlugin().getPlayerManager();
        this.kitManager = plugin.getKitManager();
        this.mobManager = new MobManager(this);
        this.bar = new GameBarData(this);
        this.gameItemData = new GameItemData(this);
        this.gameCommandData = new GameCommandData(this);
        this.gameBorderData = new GameBorderData(this);
        this.gameArenaData.spawns.addAll(spawns);

        // If lobby signs are not properly setup, game is not ready
        if (!this.gameBlockData.setLobbyBlock(lobbySign)) {
            isReady = false;
        }
        if (!this.plugin.getGameManager().checkGame(this, null)) {
            isReady = false;
        }
        this.gameArenaData.setStatus(isReady ? Status.READY : Status.BROKEN);

        this.kitManager = plugin.getKitManager();
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
        return this.gameBlockData.getSignLocation();
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

    public HungerGames getPlugin() {
        return this.plugin;
    }

    public int getRemainingTime() {
        if (this.gameTimerTask != null) return this.gameTimerTask.getRemainingTime();
        return 0;
    }

    /**
     * Initialize the waiting period of the game
     * <p>This will be called when a player first joins</p>
     */
    public void startWaitingPeriod() {
        this.gameArenaData.setStatus(Status.WAITING);
        // TODO Broadcast?!?!?
        long start = System.currentTimeMillis();
        this.gameBlockData.logBlocksForRollback();
        long fin = System.currentTimeMillis() - start;
        Util.log("Logged blocks in %s ms", fin);
    }

    /**
     * Start the pregame countdown
     */
    public void startPreGameCountdown() {
        // Call the GameStartEvent
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        this.gameArenaData.setStatus(Status.COUNTDOWN);
        this.startingTask = new StartingTask(this);
    }

    /**
     * Start the free roam state of the game
     */
    public void startFreeRoam() {
        this.gameArenaData.setStatus(Status.FREE_ROAM);
        this.gameArenaData.getGameRegion().removeEntities();
        this.freeRoamTask = new FreeRoamTask(this);
        this.gameCommandData.runCommands(CommandType.START, null);
    }

    /**
     * Start running the game
     */
    public void startRunningGame() {
        this.gameArenaData.setStatus(Status.RUNNING);
        if (Config.MOBS_SPAWN_ENABLED) this.mobSpawnerTask = new MobSpawnerTask(this);
        if (Config.CHESTS_CHEST_DROP_ENABLED) this.chestDropTask = new ChestDropTask(this);
        this.gameBlockData.updateLobbyBlock();
        if (Config.bossbar) {
            this.bar.createBossBar(gameArenaData.timer);
        }
        if (Config.WORLD_BORDER_ENABLED) {
            this.gameBorderData.initialize();
        }
        this.gameTimerTask = new GameTimerTask(this, this.gameArenaData.getTimer());
        if (this.gameArenaData.getChestRefillRepeat() > 0) {
            this.chestRefillRepeatTask = new ChestRefillRepeatTask(this);
        }
        this.nearestPlayerCompassTask = new NearestPlayerCompassTask(this);
    }

    public void cancelTasks() {
        if (this.startingTask != null) this.startingTask.stop();
        if (this.freeRoamTask != null) this.freeRoamTask.stop();
        if (this.gameTimerTask != null) this.gameTimerTask.stop();
        if (this.mobSpawnerTask != null) this.mobSpawnerTask.stop();
        if (this.chestRefillRepeatTask != null) this.chestRefillRepeatTask.stop();
        if (this.chestDropTask != null) this.chestDropTask.stop();
        if (this.nearestPlayerCompassTask != null) this.nearestPlayerCompassTask.stop();
    }

    /**
     * Join a player to the game
     *
     * @param player Player to join the game
     */
    @SuppressWarnings("UnusedReturnValue")
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
        if (this.gameArenaData.getStatus() == Status.BROKEN) {
            if (Permissions.COMMAND_CREATE.has(player)) {
                String name = this.gameArenaData.getName();
                Util.sendPrefixedMessage(player, this.lang.arena_debug_broken_debug.replace("<arena>", name));
                Util.sendPrefixedMessage(player, this.lang.arena_debug_broken_debug_2.replace("<arena>", name));
            }
            return false;
        }

        if (this.playerManager.isInGame(player)) {
            Util.sendPrefixedMessage(player, this.lang.command_join_already_in_game);
            return false;
        }
        // Call PlayerJoinGameEvent
        PlayerJoinGameEvent event = new PlayerJoinGameEvent(this, player);
        // If cancelled, stop the player from joining the game
        if (!event.callEvent()) return false;

        String arenaName = this.gameArenaData.getName();

        switch (this.gameArenaData.getStatus()) {
            case NOT_READY, ROLLBACK, STOPPED, BROKEN -> {
                Util.sendPrefixedMessage(player, this.lang.arena_not_ready);
                return false;
            }
            case RUNNING, FREE_ROAM -> {
                Util.sendPrefixedMessage(player, this.lang.game_running.replace("<arena>", arenaName), arenaName);
                if (Config.SPECTATE_ENABLED) {
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
        if (Config.WORLD_BORDER_ENABLED) {
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
            assert playerData != null;
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

        if (gameArenaData.getStatus() == Status.RUNNING) {
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
        this.gameBlockData.clearChests();
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
        if (this.gameBlockData.requiresRollback()) {
            if (this.plugin.isEnabled()) {
                new RollbackTask(this);
            } else {
                // Force rollback if server is stopping
                this.gameBlockData.forceRollback();
            }
        } else {
            this.gameArenaData.setStatus(Status.READY);
        }
        this.gameCommandData.runCommands(CommandType.STOP, null);

        // Call GameEndEvent
        Collection<Player> winners = new ArrayList<>();
        for (UUID uuid : win) {
            winners.add(Bukkit.getPlayer(uuid));
        }

        // Game has ended, we can clear all players now
        this.gamePlayerData.clearPlayers();
        this.gamePlayerData.clearSpectators();
        this.gameScoreboard.clearGameTeams();
        new GameEndEvent(this, winners, death).callEvent();
    }

    void updateAfterDeath(Player player, boolean death) {
        Status status = this.gameArenaData.getStatus();
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
                if (this.plugin.isEnabled()) {
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        stop(finalDeath);
                        this.gameScoreboard.updateBoards();
                    }, 20);
                } else {
                    stop(finalDeath);
                }

            }
        } else if (status == Status.WAITING) {
            this.gamePlayerData.msgAll(lang.player_left_game
                .replace("<arena>", this.gameArenaData.getName())
                .replace("<player>", player.getName()) +
                (this.gameArenaData.getMinPlayers() - this.gamePlayerData.getPlayers().size() <= 0 ? "!" : ": " + this.lang.players_to_start
                    .replace("<amount>", String.valueOf((this.gameArenaData.getMinPlayers() - this.gamePlayerData.getPlayers().size())))));
        }
        this.gameBlockData.updateLobbyBlock();
        this.gameScoreboard.updateBoards();
    }

    boolean isGameOver() {
        if (this.gamePlayerData.getPlayers().size() <= 1) return true;
        for (Player player : this.gamePlayerData.getPlayers()) {
            PlayerData playerData = this.playerManager.getPlayerData(player);
            assert playerData != null;
            GameTeam gameTeam = playerData.getTeam();

            if (gameTeam != null && (gameTeam.getPlayers().size() >= gamePlayerData.players.size())) {
                for (Player player1 : this.gamePlayerData.getPlayers()) {
                    if (!gameTeam.getPlayers().contains(player1)) {
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
                Util.sendMessage(player, lang.prefix + lang.command_join_no_money.replace("<cost>", String.valueOf(cost)));
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Game{name='" + this.gameArenaData.getName() + '\'' + ", bound=" + this.gameArenaData.getGameRegion() + '}';
    }

}
