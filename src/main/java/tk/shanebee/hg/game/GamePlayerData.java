package tk.shanebee.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.events.PlayerJoinGameEvent;
import tk.shanebee.hg.events.PlayerLeaveGameEvent;
import tk.shanebee.hg.game.GameCommandData.CommandType;
import tk.shanebee.hg.gui.SpectatorGUI;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data class for holding a {@link Game Game's} players
 */
public class GamePlayerData extends Data {

    private final PlayerManager playerManager;
    private final SpectatorGUI spectatorGUI;

    // Player Lists
    final List<UUID> players = new ArrayList<>();
    final List<UUID> spectators = new ArrayList<>();

    // Data lists
    final Map<Player, Integer> kills = new HashMap<>();
    final Map<String, Team> teams = new HashMap<>();

    protected GamePlayerData(Game game) {
        super(game);
        this.playerManager = plugin.getPlayerManager();
        this.spectatorGUI = new SpectatorGUI(game);
    }

    // TODO Data methods

    /**
     * Get a list of all players in the game
     *
     * @return UUID list of all players in game
     */
    public List<UUID> getPlayers() {
        return players;
    }

    void clearPlayers() {
        players.clear();
    }

    /**
     * Get a list of all players currently spectating the game
     *
     * @return List of spectators
     */
    public List<UUID> getSpectators() {
        return new ArrayList<>(this.spectators);
    }

    void clearSpectators() {
        spectators.clear();
    }

    public SpectatorGUI getSpectatorGUI() {
        return spectatorGUI;
    }

    // Utility methods

    private void kitHelp(Player player) {
        // Clear the chat a little bit, making this message easier to see
        for (int i = 0; i < 20; ++i)
            Util.scm(player, " ");
        String kit = game.kitManager.getKitListString();
        Util.scm(player, " ");
        Util.scm(player, lang.kit_join_header);
        Util.scm(player, " ");
        if (player.hasPermission("hg.kit") && game.kitManager.hasKits()) {
            Util.scm(player, lang.kit_join_msg);
            Util.scm(player, " ");
            Util.scm(player, lang.kit_join_avail + kit);
            Util.scm(player, " ");
        }
        Util.scm(player, lang.kit_join_footer);
        Util.scm(player, " ");
    }

    /**
     * Respawn all players in the game back to spawn points
     */
    public void respawnAll() {
        for (UUID u : players) {
            Player p = Bukkit.getPlayer(u);
            if (p != null)
                p.teleport(pickSpawn());
        }
    }

    void heal(Player player) {
        for (PotionEffect ef : player.getActivePotionEffects()) {
            player.removePotionEffect(ef.getType());
        }
        player.closeInventory();
        player.setHealth(20);
        player.setFoodLevel(20);
        try {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setFireTicks(0), 1);
        } catch (IllegalPluginAccessException ignore) {
        }
    }

    /**
     * Freeze a player
     *
     * @param player Player to freeze
     */
    public void freeze(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
        player.setWalkSpeed(0.0001F);
        player.setFoodLevel(1);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setInvulnerable(true);
    }

    /**
     * Unfreeze a player
     *
     * @param player Player to unfreeze
     */
    public void unFreeze(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setWalkSpeed(0.2F);
    }

    /**
     * Send a message to all players/spectators in the game
     *
     * @param message Message to send
     */
    public void msgAll(String message) {
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(players);
        allPlayers.addAll(spectators);
        for (UUID u : allPlayers) {
            Player p = Bukkit.getPlayer(u);
            if (p != null)
                Util.scm(p, message);
        }
    }

    Location pickSpawn() {
        GameArenaData gameArenaData = game.getGameArenaData();
        double spawn = getRandomIntegerBetweenRange(gameArenaData.maxPlayers - 1);
        if (containsPlayer(gameArenaData.spawns.get(((int) spawn)))) {
            Collections.shuffle(gameArenaData.spawns);
            for (Location l : gameArenaData.spawns) {
                if (!containsPlayer(l)) {
                    return l;
                }
            }
        }
        return gameArenaData.spawns.get((int) spawn);
    }

    boolean containsPlayer(Location location) {
        if (location == null) return false;

        for (UUID u : players) {
            Player p = Bukkit.getPlayer(u);
            if (p != null && p.getLocation().getBlock().equals(location.getBlock()))
                return true;
        }
        return false;
    }

    boolean vaultCheck(Player player) {
        if (Config.economy) {
            int cost = game.gameArenaData.cost;
            if (Vault.economy.getBalance(player) >= cost) {
                Vault.economy.withdrawPlayer(player, cost);
                return true;
            } else {
                Util.scm(player, lang.prefix + lang.cmd_join_no_money.replace("<cost>", String.valueOf(cost)));
                return false;
            }
        }
        return true;
    }

    /**
     * Add a kill to a player
     *
     * @param player The player to add a kill to
     */
    public void addKill(Player player) {
        this.kills.put(player, this.kills.get(player) + 1);
    }

    // TODO Game methods

    /**
     * Join a player to the game
     *
     * @param player Player to join the game
     */
    public void join(Player player) {
        join(player, false);
    }

    /**
     * Join a player to the game
     *
     * @param player  Player to join the game
     * @param command Whether joined using by using a command
     */
    public void join(Player player, boolean command) {
        GameArenaData gameArenaData = game.getGameArenaData();
        Status status = gameArenaData.getStatus();
        if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN && status != Status.READY) {
            Util.scm(player, lang.arena_not_ready);
            if ((status == Status.RUNNING || status == Status.BEGINNING) && Config.spectateEnabled) {
                Util.scm(player, lang.arena_spectate.replace("<arena>", game.gameArenaData.getName()));
            }
        } else if (gameArenaData.maxPlayers <= players.size()) {
            Util.scm(player, "&c" + gameArenaData.getName() + " " + lang.game_full);
        } else if (!players.contains(player.getUniqueId())) {
            if (!vaultCheck(player)) {
                return;
            }
            // Call PlayerJoinGameEvent
            PlayerJoinGameEvent event = new PlayerJoinGameEvent(game, player);
            Bukkit.getPluginManager().callEvent(event);
            // If cancelled, stop the player from joining the game
            if (event.isCancelled()) return;

            if (player.isInsideVehicle()) {
                player.leaveVehicle();
            }

            players.add(player.getUniqueId());

            Location loc = pickSpawn();
            if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    loc.setY(loc.getY() - 1);
                }
            }
            Location previousLocation = player.getLocation();

            player.teleport(loc);

            PlayerData playerData = new PlayerData(player, game);
            if (command && Config.savePreviousLocation) {
                playerData.setPreviousLocation(previousLocation);
            }
            playerManager.addPlayerData(playerData);
            gameArenaData.board.setBoard(player);

            heal(player);
            freeze(player);
            kills.put(player, 0);

            if (players.size() == 1 && status == Status.READY)
                gameArenaData.setStatus(Status.WAITING);
            if (players.size() >= game.gameArenaData.minPlayers && (status == Status.WAITING || status == Status.READY)) {
                game.startPreGame();
            } else if (status == Status.WAITING) {
                String broadcast = lang.player_joined_game
                        .replace("<arena>", gameArenaData.getName())
                        .replace("<player>", player.getName()) + (gameArenaData.minPlayers - players.size() <= 0 ? "!" : ":" +
                        lang.players_to_start.replace("<amount>", String.valueOf((gameArenaData.minPlayers - players.size()))));
                if (Config.broadcastJoinMessages) {
                    Util.broadcast(broadcast);
                } else {
                    msgAll(broadcast);
                }
            }
            kitHelp(player);

            game.getGameBlockData().updateLobbyBlock();
            game.gameArenaData.updateBoards();
            game.getGameCommandData().runCommands(CommandType.JOIN, player);
        }
    }

    /**
     * Make a player leave the game
     *
     * @param player Player to leave the game
     * @param death  Whether the player has died or not (Generally should be false)
     */
    public void leave(Player player, Boolean death) {
        Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(game, player, death));
        players.remove(player.getUniqueId());
        UUID uuid = player.getUniqueId();
        unFreeze(player);
        if (death) {
            if (Config.spectateEnabled && Config.spectateOnDeath && !game.isGameOver()) {
                spectate(player);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
                player.sendTitle(game.gameArenaData.getName(), Util.getColString(lang.spectator_start_title), 10, 100, 10);
                game.updateAfterDeath(player, true);
                return;
            } else if (game.gameArenaData.getStatus() == Status.RUNNING)
                game.getGameBarData().removePlayer(player);
        }
        heal(player);
        PlayerData playerData = playerManager.getPlayerData(uuid);
        Location previousLocation = playerData.getPreviousLocation();

        playerData.restore(player);
        playerManager.removePlayerData(player);
        exit(player, previousLocation);
        if (death) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
        }
        game.updateAfterDeath(player, death);
    }

    void exit(Player player, @Nullable Location exitLocation) {
        GameArenaData gameArenaData = game.getGameArenaData();
        player.setInvulnerable(false);
        if (gameArenaData.getStatus() == Status.RUNNING)
            game.getGameBarData().removePlayer(player);
        if (exitLocation != null) {
            player.teleport(exitLocation);
        } else if (gameArenaData.exit != null && gameArenaData.exit.getWorld() != null) {
            player.teleport(gameArenaData.exit);
        } else {
            Location worldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
            Location bedLocation = player.getBedSpawnLocation();
            player.teleport(bedLocation != null ? bedLocation : worldSpawn);
        }
    }

    /**
     * Put a player into spectator for this game
     *
     * @param spectator The player to spectate
     */
    public void spectate(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        spectator.teleport(game.gameArenaData.getSpawns().get(0));
        if (playerManager.hasPlayerData(uuid)) {
            playerManager.transferPlayerDataToSpectator(uuid);
        } else {
            playerManager.addSpectatorData(new PlayerData(spectator, game));
        }
        this.spectators.add(uuid);
        spectator.setGameMode(GameMode.SURVIVAL);
        spectator.setCollidable(false);
        if (Config.spectateFly)
            spectator.setAllowFlight(true);

        if (Config.spectateHide) {
            for (UUID u : players) {
                Player player = Bukkit.getPlayer(u);
                if (player == null) continue;
                player.hidePlayer(plugin, spectator);
            }
            for (UUID u : spectators) {
                Player player = Bukkit.getPlayer(u);
                if (player == null) continue;
                player.hidePlayer(plugin, spectator);
            }
        }
        game.getGameBarData().addPlayer(spectator);
        game.gameArenaData.board.setBoard(spectator);
        spectator.getInventory().setItem(0, plugin.getItemStackManager().getSpectatorCompass());
    }

    /**
     * Remove a player from spectator of this game
     *
     * @param spectator The player to remove
     */
    public void leaveSpectate(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        PlayerData playerData = playerManager.getSpectatorData(uuid);
        Location previousLocation = playerData.getPreviousLocation();

        playerData.restore(spectator);
        playerManager.removeSpectatorData(uuid);
        spectators.remove(spectator.getUniqueId());
        spectator.setCollidable(true);
        if (Config.spectateFly) {
            GameMode mode = spectator.getGameMode();
            if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE)
                spectator.setAllowFlight(false);
        }
        if (Config.spectateHide)
            revealPlayer(spectator);
        exit(spectator, previousLocation);
    }

    void revealPlayer(Player hidden) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, hidden);
        }
    }

    public void addTeam(Team team) {
        teams.put(team.getName(), team);
    }

    public void clearTeams() {
        teams.clear();
    }

    public boolean hasTeam(String name) {
        return teams.containsKey(name);
    }

    // UTIL
    private static double getRandomIntegerBetweenRange(double max) {
        return (int) (Math.random() * ((max - (double) 0) + 1)) + (double) 0;
    }

}
