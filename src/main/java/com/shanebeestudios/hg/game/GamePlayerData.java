package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.events.PlayerLeaveGameEvent;
import com.shanebeestudios.hg.gui.SpectatorGUI;
import com.shanebeestudios.hg.managers.PlayerManager;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @SuppressWarnings("DataFlowIssue")
    private static final @NotNull NamespacedKey JUMP_KEY = NamespacedKey.fromString("hg:freeze_jump");
    @SuppressWarnings("DataFlowIssue")
    private static final @NotNull NamespacedKey MOVE_KEY = NamespacedKey.fromString("hg:freeze_move");

    private final PlayerManager playerManager;
    private final SpectatorGUI spectatorGUI;

    // Player Lists
    final List<UUID> players = new ArrayList<>();
    final List<UUID> spectators = new ArrayList<>();
    // This list contains all players who have joined the arena
    // Will be used to broadcast messages even if a player is no longer in the game
    final List<UUID> allPlayers = new ArrayList<>();

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
        return this.players;
    }

    void clearPlayers() {
        this.players.clear();
        this.allPlayers.clear();
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
        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.teleport(pickSpawn());
        }
    }

    void heal(Player player) {
        player.clearActivePotionEffects();
        player.closeInventory();
        player.setHealth(20);
        player.setFoodLevel(20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.setFireTicks(0), 1);
    }

    /**
     * Freeze a player
     *
     * @param player Player to freeze
     */
    public void freeze(Player player) {
        player.setGameMode(GameMode.SURVIVAL);

        // Freeze movement
        AttributeInstance movementSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        assert movementSpeed != null;
        AttributeModifier moveMod = new AttributeModifier(MOVE_KEY, -movementSpeed.getValue(), Operation.ADD_NUMBER);
        movementSpeed.addTransientModifier(moveMod);

        // Freeze jumping
        AttributeInstance jumpStrength = player.getAttribute(Attribute.JUMP_STRENGTH);
        assert jumpStrength != null;
        AttributeModifier jumpMod = new AttributeModifier(JUMP_KEY, -jumpStrength.getValue(), Operation.ADD_NUMBER);
        jumpStrength.addTransientModifier(jumpMod);

        player.setAllowFlight(false);
        player.setFlying(false);
        player.setInvulnerable(true);
    }

    /**
     * Unfreeze a player
     *
     * @param player Player to unfreeze
     */
    @SuppressWarnings("DataFlowIssue")
    public void unFreeze(Player player) {
        player.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(MOVE_KEY);
        player.getAttribute(Attribute.JUMP_STRENGTH).removeModifier(JUMP_KEY);
    }

    /**
     * Send a message to all players/spectators in the game
     *
     * @param message Message to send
     */
    public void msgAll(String message) {
        List<UUID> allPlayers = new ArrayList<>();
        allPlayers.addAll(this.players);
        allPlayers.addAll(this.spectators);
        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                Util.scm(player, message);
        }
    }

    /**
     * Sends a message to all players/spectators
     * <b>Includes players who have died and left the game.
     * Used for broadcasting win messages</b>
     *
     * @param message Message to send
     */
    public void msgAllPlayers(String message) {
        List<UUID> allPlayers = new ArrayList<>(this.allPlayers);
        allPlayers.addAll(this.spectators);
        for (UUID u : allPlayers) {
            Player p = Bukkit.getPlayer(u);
            if (p != null)
                Util.scm(p, lang.prefix + message);
        }
    }

    // TODO redo?
    private Location pickSpawn() {
        GameArenaData gameArenaData = this.getGame().getGameArenaData();
        double spawn = getRandomIntegerBetweenRange(gameArenaData.getMaxPlayers() - 1);
        if (containsPlayer(gameArenaData.getSpawns().get(((int) spawn)))) {
            Collections.shuffle(gameArenaData.getSpawns());
            for (Location l : gameArenaData.getSpawns()) {
                if (!containsPlayer(l)) {
                    return l;
                }
            }
        }
        return gameArenaData.getSpawns().get((int) spawn);
    }

    // TODO redo?
    boolean containsPlayer(Location location) {
        if (location == null) return false;

        for (UUID u : this.getGame().getGamePlayerData().getPlayers()) {
            Player p = Bukkit.getPlayer(u);
            if (p != null && p.getLocation().getBlock().equals(location.getBlock()))
                return true;
        }
        return false;
    }

    // UTIL
    private static double getRandomIntegerBetweenRange(double max) {
        return (int) (Math.random() * ((max - (double) 0) + 1)) + (double) 0;
    }

    void addPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        this.players.add(uuid);
        this.allPlayers.add(uuid);
    }

    void putPlayerIntoArena(Player player, boolean savePreviousLocation) {
        GameArenaData gameArenaData = this.getGame().getGameArenaData();
        Location loc = pickSpawn(); // TODO rewrite spawn pick thingy
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                loc.setY(loc.getY() - 1);
            }
        }

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        Location previousLocation = player.getLocation();

        // Teleport async into the arena so it loads a little more smoothly
        player.teleportAsync(loc).thenAccept(a -> {
            PlayerData playerData = new PlayerData(player, this.game);
            if (savePreviousLocation && Config.savePreviousLocation) {
                playerData.setPreviousLocation(previousLocation);
            }
            this.playerManager.addPlayerData(playerData);
            gameArenaData.getBoard().setBoard(player);

            heal(player);
            freeze(player);
            this.kills.put(player, 0);
            kitHelp(player);

            gameArenaData.updateBoards();
            this.game.getGameCommandData().runCommands(GameCommandData.CommandType.JOIN, player);
        });
    }

    /**
     * Add a kill to a player
     *
     * @param player The player to add a kill to
     */
    public void addKill(Player player) {
        this.kills.put(player, this.kills.get(player) + 1);
    }

    /**
     * Make a player leave the game
     *
     * @param player Player to leave the game
     * @param death  Whether the player has died or not (Generally should be false)
     */
    public void leave(Player player, boolean death) {
        new PlayerLeaveGameEvent(this.game, player, death).callEvent();
        UUID uuid = player.getUniqueId();
        players.remove(uuid);
        if (!death) allPlayers.remove(uuid); // Only remove the player if they voluntarily left the game
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
        exit(player, previousLocation);
        playerManager.removePlayerData(player);
        if (death) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
        }
        game.updateAfterDeath(player, death);
    }

    void exit(Player player, @Nullable Location exitLocation) {
        unFreeze(player);
        GameArenaData gameArenaData = game.getGameArenaData();
        player.setInvulnerable(false);
        if (gameArenaData.getStatus() == Status.RUNNING)
            this.game.getGameBarData().removePlayer(player);
        Location loc;
        if (exitLocation != null) {
            loc = exitLocation;
        } else if (gameArenaData.exit != null && gameArenaData.exit.getWorld() != null) {
            loc = gameArenaData.exit;
        } else {
            Location worldSpawn = Bukkit.getWorlds().getFirst().getSpawnLocation();
            Location respawnLocation = player.getRespawnLocation();
            loc = respawnLocation != null ? respawnLocation : worldSpawn;
        }
        PlayerData playerData = this.playerManager.getData(player);
        if (playerData == null || playerData.isOnline()) {
            player.teleportAsync(loc);
        } else {
            player.teleport(loc);
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
        playerManager.removeSpectatorData(uuid);
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

}
