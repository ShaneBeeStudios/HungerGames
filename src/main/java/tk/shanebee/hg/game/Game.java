package tk.shanebee.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.Language;
import tk.shanebee.hg.data.Leaderboard;
import tk.shanebee.hg.events.GameEndEvent;
import tk.shanebee.hg.events.GameStartEvent;
import tk.shanebee.hg.game.GameCommandData.CommandType;
import tk.shanebee.hg.managers.KitManager;
import tk.shanebee.hg.managers.MobManager;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.managers.SBDisplay;
import tk.shanebee.hg.tasks.ChestDropTask;
import tk.shanebee.hg.tasks.FreeRoamTask;
import tk.shanebee.hg.tasks.Rollback;
import tk.shanebee.hg.tasks.SpawnerTask;
import tk.shanebee.hg.tasks.StartingTask;
import tk.shanebee.hg.tasks.TimerTask;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

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
    final String name;
    final List<Location> spawns;
    final Bound bound;

    KitManager kit;

    private final MobManager mobManager;
    private final PlayerManager playerManager;
    Location exit;
    Status status;
    final int minPlayers;
    final int maxPlayers;
    final int time;
    int cost;

    private final int roamTime;
    final SBDisplay sb;
    private int chestRefillTime = 0;

    // Task ID's here!
    private SpawnerTask spawner;
    private FreeRoamTask freeRoam;
    private StartingTask starting;
    private TimerTask timer;
    private ChestDropTask chestDrop;

    // Data Objects
    private final GameBar bar;
    private final GamePlayerData gamePlayerData;
    private final GameBlockData gameBlockData;
    private final GameItemData gameItemData;
    private final GameCommandData gameCommandData;
    private final GameBorderData gameBorderData;

    final boolean spectate = Config.spectateEnabled;
    final boolean spectateOnDeath = Config.spectateOnDeath;

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
        this.spawns.addAll(spawns);
        this.gameBlockData.sign1 = lobbySign;
        if (isReady) this.status = Status.READY;
        else this.status = Status.BROKEN;
        this.cost = cost;

        this.gameBlockData.setLobbyBlock(lobbySign);

        this.kit = plugin.getKitManager();
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
        this.playerManager = HG.getPlugin().getPlayerManager();
        this.lang = plugin.getLang();
        this.name = name;
        this.time = timer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.roamTime = roam;
        this.spawns = new ArrayList<>();
        this.bound = bound;
        this.status = Status.NOTREADY;
        this.sb = new SBDisplay(this);
        this.kit = plugin.getKitManager();
        this.mobManager = new MobManager(this);
        this.cost = cost;
        this.bar = new GameBar(this);
        this.gamePlayerData = new GamePlayerData(this);
        this.gameBlockData = new GameBlockData(this);
        this.gameItemData = new GameItemData(this);
        this.gameCommandData = new GameCommandData(this);
        this.gameBorderData = new GameBorderData(this);
        this.gameBorderData.setBorderSize(Config.borderFinalSize);
        this.gameBorderData.setBorderTimer(Config.borderCountdownStart, Config.borderCountdownEnd);
    }

    /**
     * Get an instance of the GameBar
     *
     * @return Instance of GameBar
     */
    public GameBar getGameBar() {
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
     * Get the bounding region of this game
     *
     * @return Region of this game
     */
    public Bound getRegion() {
        return bound;
    }

    public StartingTask getStartingTask() {
        return this.starting;
    }

    /**
     * Set the chest refill time for this game
     *
     * @param refill Remaining time in game (seconds : 30 second intervals)
     */
    public void setChestRefill(int refill) {
        this.chestRefillTime = refill;
    }

    /**
     * Set the status of the game
     *
     * @param status Status to set
     */
    public void setStatus(Status status) {
        this.status = status;
        gameBlockData.updateLobbyBlock();
    }

    /**
     * Set the chest refill time
     *
     * @param time The remaining time in the game for the chests to refill
     */
    public void setChestRefillTime(int time) {
        this.chestRefillTime = time;
    }

    /**
     * Get the chest refill time
     *
     * @return The remaining time in the game which the chests will refill
     */
    public int getChestRefillTime() {
        return this.chestRefillTime;
    }


    /**
     * Get the status of the game
     *
     * @return Status of the game
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Get the bounding box of this game
     *
     * @return Bound of this game
     */
    public Bound getBound() {
        return this.bound;
    }

    /**
     * Get the name of this game
     *
     * @return Name of this game
     */
    public String getName() {
        return this.name;
    }

    /**
     * Check if a location is within the games arena
     *
     * @param location Location to be checked
     * @return True if location is within the arena bounds
     */
    public boolean isInRegion(Location location) {
        return bound.isInRegion(location);
    }

    /**
     * Get a list of all spawn locations
     *
     * @return All spawn locations
     */
    public List<Location> getSpawns() {
        return spawns;
    }

    /**
     * Get the roam time of the game
     *
     * @return The roam time
     */
    public int getRoamTime() {
        return this.roamTime;
    }

    /**
     * Get the exit location associated with this game
     *
     * @return Exit location
     */
    public Location getExit() {
        return this.exit;
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
     * Get max players for this game
     *
     * @return Max amount of players for this game
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Get min players for this game
     *
     * @return Min amount of players for this game
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Get the kits for this game
     *
     * @return The KitManager kit for this game
     */
    public KitManager getKitManager() {
        return this.kit;
    }

    /**
     * Set the kits for this game
     *
     * @param kit The KitManager kit to set
     */
    @SuppressWarnings("unused")
    public void setKitManager(KitManager kit) {
        this.kit = kit;
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

        status = Status.COUNTDOWN;
        starting = new StartingTask(this);
        gameBlockData.updateLobbyBlock();
    }

    /**
     * Start the free roam state of the game
     */
    public void startFreeRoam() {
        status = Status.BEGINNING;
        gameBlockData.updateLobbyBlock();
        bound.removeEntities();
        freeRoam = new FreeRoamTask(this);
        gameCommandData.runCommands(CommandType.START, null);
    }

    /**
     * Start the game
     */
    public void startGame() {
        status = Status.RUNNING;
        if (Config.spawnmobs) spawner = new SpawnerTask(this, Config.spawnmobsinterval);
        if (Config.randomChest) chestDrop = new ChestDropTask(this);
        timer = new TimerTask(this, time);
        gameBlockData.updateLobbyBlock();
        if (Config.bossbar) {
            bar.createBossbar(time);
        }
        if (Config.borderEnabled && Config.borderOnStart) {
            gameBorderData.setBorder(time);
        }
    }

    /**
     * Add a spawn location to the game
     *
     * @param location The location to add
     */
    public void addSpawn(Location location) {
        this.spawns.add(location);
    }

    /**
     * Set exit location for this game
     *
     * @param location Location where players will exit
     */
    public void setExit(Location location) {
        this.exit = location;
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
        bound.removeEntities();
        List<UUID> win = new ArrayList<>();
        cancelTasks();
        for (UUID uuid : gamePlayerData.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                gamePlayerData.heal(player);
                playerManager.getPlayerData(uuid).restore(player);
                playerManager.removePlayerData(uuid);
                win.add(uuid);
                sb.restoreSB(player);
                gamePlayerData.exit(player);
            }
        }
        gamePlayerData.clearPlayers();

        for (UUID uuid : gamePlayerData.spectators) {
            Player spectator = Bukkit.getPlayer(uuid);
            if (spectator != null) {
                spectator.setCollidable(true);
                if (Config.spectateHide)
                    gamePlayerData.revealPlayer(spectator);
                if (Config.spectateFly) {
                    GameMode mode = playerManager.getSpectatorData(uuid).getGameMode();
                    if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE)
                        spectator.setAllowFlight(false);
                }
                gamePlayerData.exit(spectator);
                playerManager.getSpectatorData(uuid).restore(spectator);
                playerManager.removeSpectatorData(uuid);
                sb.restoreSB(spectator);
            }
        }
        gamePlayerData.clearSpectators();

        if (this.getStatus() == Status.RUNNING) {
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
                        Util.scm(p, HG.getPlugin().getLang().winning_amount.replace("<amount>", String.valueOf(db)));
                    }
                }
                plugin.getLeaderboard().addStat(u, Leaderboard.Stats.WINS);
                plugin.getLeaderboard().addStat(u, Leaderboard.Stats.GAMES);
            }
        }
        gameBlockData.clearChests();
        String winner = Util.translateStop(Util.convertUUIDListToStringList(win));
        // prevent not death winners from gaining a prize
        if (death)
            Util.broadcast(HG.getPlugin().getLang().player_won.replace("<arena>", name).replace("<winner>", winner));
        if (gameBlockData.requiresRollback()) {
            new Rollback(this);
        } else {
            status = Status.READY;
            gameBlockData.updateLobbyBlock();
        }
        sb.resetAlive();
        gameCommandData.runCommands(CommandType.STOP, null);

        // Call GameEndEvent
        Collection<Player> winners = new ArrayList<>();
        for (UUID uuid : win) {
            winners.add(Bukkit.getPlayer(uuid));
        }
        Bukkit.getPluginManager().callEvent(new GameEndEvent(this, winners, death));
    }

    void updateAfterDeath(Player player, boolean death) {
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
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    stop(finalDeath);
                    gameBlockData.updateLobbyBlock();
                    sb.setAlive();
                }, 20);

            }
        } else if (status == Status.WAITING) {
            gamePlayerData.msgAll(HG.getPlugin().getLang().player_left_game.replace("<player>", player.getName()) +
                    (minPlayers - gamePlayerData.players.size() <= 0 ? "!" : ":" + HG.getPlugin().getLang().players_to_start
                            .replace("<amount>", String.valueOf((minPlayers - gamePlayerData.players.size())))));
        }
        gameBlockData.updateLobbyBlock();
        sb.setAlive();
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
        return "Game{" +
                "name='" + name + '\'' +
                ", bound=" + bound +
                '}';
    }

}
