package tk.shanebee.hg.game;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.events.PlayerJoinGameEvent;
import tk.shanebee.hg.events.PlayerLeaveGameEvent;
import tk.shanebee.hg.gui.SpectatorGUI;
import tk.shanebee.hg.managers.PlayerManager;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        return this.spectators;
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
        String kit = game.kit.getKitListString();
        Util.scm(player, " ");
        Util.scm(player, lang.kit_join_header);
        Util.scm(player, " ");
        if (player.hasPermission("hg.kit") && game.kit.hasKits()) {
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
        double spawn = getRandomIntegerBetweenRange(game.maxPlayers - 1);
        if (containsPlayer(game.spawns.get(((int) spawn)))) {
            Collections.shuffle(game.spawns);
            for (Location l : game.spawns) {
                if (!containsPlayer(l)) {
                    return l;
                }
            }
        }
        return game.spawns.get((int) spawn);
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
            int cost = game.cost;
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
        UUID uuid = player.getUniqueId();
        AtomicReference<Status> status = new AtomicReference<>(game.getStatus());
        if (status.get() != Status.WAITING && status.get() != Status.STOPPED && status.get() != Status.COUNTDOWN && status.get() != Status.READY) {
            Util.scm(player, lang.arena_not_ready);
            if ((status.get() == Status.RUNNING || status.get() == Status.BEGINNING) && Config.spectateEnabled) {
                Util.scm(player, lang.arena_spectate.replace("<arena>", game.getName()));
            }
        } else if (game.maxPlayers <= players.size()) {
            player.sendMessage(ChatColor.RED + game.getName() + " is currently full!");
            Util.scm(player, "&c" + game.getName() + " " + lang.game_full);
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location loc = pickSpawn();
                player.teleport(loc);

                if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                        loc.setY(loc.getY() - 1);
                    }
                }
                playerManager.addPlayerData(new PlayerData(player, game));

                heal(player);
                freeze(player);
                kills.put(player, 0);

                if (players.size() == 1 && status.get() == Status.READY)
                    status.set(Status.WAITING);
                if (players.size() >= game.minPlayers && (status.get() == Status.WAITING || status.get() == Status.READY)) {
                    game.startPreGame();
                } else if (status.get() == Status.WAITING) {
                    Util.broadcast(lang.player_joined_game.replace("<player>",
                            player.getName()) + (game.minPlayers - players.size() <= 0 ? "!" : ":" +
                            lang.players_to_start.replace("<amount>", String.valueOf((game.minPlayers - players.size())))));
                }
                kitHelp(player);

                game.getGameBlockData().updateLobbyBlock();
                game.sb.setSB(player);
                game.sb.setAlive();
                game.runCommands(Game.CommandType.JOIN, player);
            }, 5);
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
            if (game.getStatus() == Status.RUNNING)
                game.getGameBar().removePlayer(player);
            heal(player);
            playerManager.getPlayerData(uuid).restore(player);
            playerManager.removePlayerData(player);
            exit(player);
            game.sb.restoreSB(player);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
            if (game.spectate && game.spectateOnDeath && !game.isGameOver()) {
                spectate(player);
                player.sendTitle(game.getName(), "You are now spectating!", 10, 100, 10); //TODO this a temp test
            }
        } else {
            heal(player);
            playerManager.getPlayerData(uuid).restore(player);
            playerManager.removePlayerData(player);
            exit(player);
            game.sb.restoreSB(player);
        }
        game.updateAfterDeath(player, death);
    }

    void exit(Player player) {
        player.setInvulnerable(false);
        if (game.getStatus() == Status.RUNNING)
            game.getGameBar().removePlayer(player);
        if (game.exit != null && game.exit.getWorld() != null) {
            player.teleport(game.exit);
        } else {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }

    /**
     * Put a player into spectator for this game
     *
     * @param spectator The player to spectate
     */
    public void spectate(Player spectator) {
        UUID uuid = spectator.getUniqueId();
        if (playerManager.hasPlayerData(uuid)) {
            playerManager.transferPlayerDataToSpectator(uuid);
        } else {
            playerManager.addSpectatorData(new PlayerData(spectator, game));
        }
        this.spectators.add(uuid);
        spectator.teleport(game.getSpawns().get(0));
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
        game.getGameBar().addPlayer(spectator);
        spectator.getInventory().setItem(0, plugin.getItemStackManager().getSpectatorCompass());
    }

    /**
     * Remove a player from spectator of this game
     *
     * @param spectator The player to remove
     */
    public void leaveSpectate(Player spectator) {
        exit(spectator);
        spectator.setCollidable(true);
        UUID uuid = spectator.getUniqueId();
        if (Config.spectateFly) {
            GameMode mode = playerManager.getSpectatorData(uuid).getGameMode();
            if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE)
                spectator.setAllowFlight(false);
        }
        if (Config.spectateHide)
            revealPlayer(spectator);
        playerManager.getSpectatorData(uuid).restore(spectator);
        playerManager.removeSpectatorData(uuid);
        spectators.remove(spectator.getUniqueId());
    }

    void revealPlayer(Player hidden) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, hidden);
        }
    }

    // UTIL
    private static double getRandomIntegerBetweenRange(double max) {
        return (int) (Math.random() * ((max - (double) 0) + 1)) + (double) 0;
    }

}
