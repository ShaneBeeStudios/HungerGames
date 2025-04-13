package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.data.Leaderboard;
import com.shanebeestudios.hg.api.data.PlayerData;
import com.shanebeestudios.hg.api.events.GameEndEvent;
import com.shanebeestudios.hg.api.events.GameStartEvent;
import com.shanebeestudios.hg.api.events.PlayerJoinGameEvent;
import com.shanebeestudios.hg.api.game.GameCommandData.CommandType;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Vault;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import com.shanebeestudios.hg.plugin.tasks.ChestDropTask;
import com.shanebeestudios.hg.plugin.tasks.ChestRefillRepeatTask;
import com.shanebeestudios.hg.plugin.tasks.FreeRoamTask;
import com.shanebeestudios.hg.plugin.tasks.GameTimerTask;
import com.shanebeestudios.hg.plugin.tasks.MobSpawnerTask;
import com.shanebeestudios.hg.plugin.tasks.NearestPlayerCompassTask;
import com.shanebeestudios.hg.plugin.tasks.RollbackTask;
import com.shanebeestudios.hg.plugin.tasks.StartingTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * General game object
 */
@SuppressWarnings("unused")
public class Game {

    final HungerGames plugin;
    final Language lang;

    // Managers
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
    private final GameEntityData gameEntityData;
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
     * @param lobbySign     Location of lobby sign block
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
        this.gameEntityData = new GameEntityData(this);
        this.gamePlayerData = new GamePlayerData(this);
        this.gameBlockData = new GameBlockData(this);
        this.gameScoreboard = new GameScoreboard(this);
        this.playerManager = HungerGames.getPlugin().getPlayerManager();
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
     * Get an instance of the GameEntityData
     *
     * @return Instance of GameEntityData
     */
    public GameEntityData getGameEntityData() {
        return this.gameEntityData;
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

    /**
     * Get an instance of the StartingGameTAsk
     *
     * @return Instance of StartingGameTask
     */
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
     * Get plugin instance from game
     *
     * @return Plugin instance
     */
    public HungerGames getPlugin() {
        return this.plugin;
    }

    /**
     * Get the time remaining in this game
     *
     * @return Amount of seconds left if active otherwise 0
     */
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
        long start = System.currentTimeMillis();
        this.gameBlockData.logBlocksForRollback();
        this.gameBlockData.setupRandomizedBonusChests();
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
        this.gamePlayerData.putAllPlayersIntoArena();
        this.startingTask = new StartingTask(this);
    }

    /**
     * Start the free roam state of the game
     */
    public void startFreeRoam() {
        // Close possible Kit GUIs
        this.gamePlayerData.getPlayers().forEach(HumanEntity::closeInventory);
        this.gameArenaData.setStatus(Status.FREE_ROAM);
        this.gameEntityData.removeEntities();
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

    /**
     * Cancel all active tasks
     */
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
     * Broadcast a message to all players on the server
     * that someone joined a game, how many more to start
     * and the command to join
     *
     * @param player Player who joined
     */
    private void broadcastJoin(Player player) {
        if (!Config.BROADCAST_JOIN_MESSAGES) return;
        String name = this.getGameArenaData().getName();
        Util.broadcast(this.lang.game_waiting_join
            .replace("<arena>", name)
            .replace("<player>", player.getName()));

        int toStart = this.gameArenaData.getMinPlayers() - this.gamePlayerData.getPlayers().size();
        Util.broadcast(this.lang.game_waiting_players_to_start
            .replace("<amount>", "" + toStart));
        Util.broadcast(this.lang.game_join.replace("<arena>", name));
    }

    /**
     * Join a player to the game
     *
     * @param player Player to join the game
     * @return Whether the player joined the game
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
     * @return Whether the player joined the game
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
                Util.sendPrefixedMessage(player, this.lang.game_arena_not_ready);
                return false;
            }
            case RUNNING, FREE_ROAM -> {
                Util.sendPrefixedMessage(player, this.lang.game_running.replace("<arena>", arenaName), arenaName);
                if (Config.SPECTATE_ENABLED) {
                    Util.sendPrefixedMessage(player, this.lang.game_arena_spectate.replace("<arena>", arenaName));
                }
                return false;
            }
            case READY -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player, savePreviousLocation);
                startWaitingPeriod();
                broadcastJoin(player);
                Util.sendPrefixedMessage(player, this.lang.game_joined_waiting_to_teleport.replace("<arena>", arenaName));
            }
            case WAITING -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player, savePreviousLocation);
                if (this.gamePlayerData.getPlayers().size() >= this.gameArenaData.getMinPlayers()) {
                    startPreGameCountdown();
                } else {
                    broadcastJoin(player);
                    Util.sendPrefixedMessage(player, this.lang.game_joined_waiting_to_teleport.replace("<arena>", arenaName));
                }
            }
            case COUNTDOWN -> {
                if (!canJoin(player)) return false;
                this.gamePlayerData.addPlayerData(player, savePreviousLocation);
                this.gamePlayerData.putPlayerIntoArena(player);
            }
        }

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
     * @param death Whether the game stopped after the result of a death (false = no winnings paid out)
     */
    public void stop(boolean death) {
        if (Config.WORLD_BORDER_ENABLED) {
            this.gameBorderData.resetBorder();
        }
        this.gameEntityData.removeEntities();
        this.gameScoreboard.resetSidebars();

        List<Player> winners = new ArrayList<>();
        cancelTasks();
        for (Player player : this.gamePlayerData.getPlayers()) {
            PlayerData playerData = this.playerManager.getPlayerData(player);

            // We might be in the waiting stage
            if (playerData == null) continue;

            Location previousLocation = playerData.getPreviousLocation();

            this.gamePlayerData.heal(player);
            playerData.restore(player);
            winners.add(player);
            this.gamePlayerData.exit(player, previousLocation);
            this.playerManager.removePlayerData(player);
        }

        for (Player spectator : this.gamePlayerData.getSpectators()) {
            this.gamePlayerData.leaveSpectate(spectator);
        }

        if (gameArenaData.getStatus() == Status.RUNNING) {
            this.bar.clearBar();
        }

        // Handle rewards and stats
        if (!winners.isEmpty() && death) {
            double winningReward = (double) Config.REWARD_CASH / winners.size();
            for (Player winner : winners) {
                if (Config.REWARD_GIVE_REWARD) {
                    // Run reward commands
                    if (!Config.REWARD_COMMANDS.isEmpty()) {
                        for (String cmd : Config.REWARD_COMMANDS) {
                            if (cmd.equalsIgnoreCase("none")) continue;
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("<player>", winner.getName()));
                        }
                    }
                    // Send reward messages
                    if (!Config.REWARD_MESSAGES.isEmpty()) {
                        for (String msg : Config.REWARD_MESSAGES) {
                            if (msg.equalsIgnoreCase("none")) continue;
                            Util.sendMessage(winner, msg.replace("<player>", winner.getName()));
                        }
                    }
                    // Deposit reward winnings
                    if (winningReward > 0) {
                        Vault.ECONOMY.depositPlayer(winner, winningReward);
                        Util.sendMessage(winner, this.lang.winning_amount.replace("<amount>", String.valueOf(winningReward)));
                    }
                }
                this.plugin.getLeaderboard().addStat(winner, Leaderboard.Stats.WINS);
                this.plugin.getLeaderboard().addStat(winner, Leaderboard.Stats.GAMES);
            }
        }
        this.gameBlockData.clearChests();

        // Broadcast wins
        if (death) {
            // String together winners names
            StringJoiner joiner = new StringJoiner(", ");
            winners.forEach(player -> joiner.add(player.getName()));
            String joinedWinners = joiner.toString();

            String broadcast = this.lang.game_player_won
                .replace("<arena>", this.gameArenaData.getName())
                .replace("<winner>", joinedWinners);
            if (Config.SETTINGS_BROADCAST_WIN_MESSAGES) {
                Util.broadcast(broadcast);
            } else {
                this.gamePlayerData.messageAllPlayers(broadcast);
            }
        }

        // Run rollback
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

        // Run stop commands
        this.gameCommandData.runCommands(CommandType.STOP, null);

        // Game has ended, we can clear all players now
        this.gamePlayerData.clearPlayers();
        this.gamePlayerData.clearSpectators();
        this.gameScoreboard.clearGameTeams();

        // Call GameEndEvent
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
            this.gamePlayerData.messageAllActivePlayers(this.lang.game_player_left_game
                .replace("<arena>", this.gameArenaData.getName())
                .replace("<player>", player.getName()) +
                (this.gameArenaData.getMinPlayers() - this.gamePlayerData.getPlayers().size() <= 0 ? "!" : ": " + this.lang.game_waiting_players_to_start
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
            String name = this.gameArenaData.getName();
            Util.sendPrefixedMessage(player, this.lang.game_full.replace("<name>", name));
            return false;
        }
        return vaultCheck(player);
    }

    boolean vaultCheck(Player player) {
        if (Config.economy) {
            int cost = this.getGameArenaData().getCost();
            if (Vault.ECONOMY.getBalance(player) >= cost) {
                Vault.ECONOMY.withdrawPlayer(player, cost);
                return true;
            } else {
                Util.sendPrefixedMessage(player, this.lang.command_join_no_money.replace("<cost>", String.valueOf(cost)));
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
