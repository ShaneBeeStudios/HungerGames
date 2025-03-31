package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.api.events.PlayerLeaveGameEvent;
import com.shanebeestudios.hg.gui.SpectatorGUI;
import com.shanebeestudios.hg.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    final List<Player> players = new ArrayList<>();
    final List<Player> spectators = new ArrayList<>();
    // This list contains all players who have joined the arena
    // Will be used to broadcast messages even if a player is no longer in the game
    final List<Player> allPlayers = new ArrayList<>();

    // Data lists
    final Map<Player, Integer> kills = new HashMap<>();
    private final List<Location> randomizedSpawns = new ArrayList<>();

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
    public List<Player> getPlayers() {
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
    public List<Player> getSpectators() {
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
            Util.sendMessage(player, " ");
        String kit = game.kitManager.getKitListString();
        Util.sendMessage(player, " ");
        Util.sendMessage(player, lang.kit_join_header);
        Util.sendMessage(player, " ");
        if (player.hasPermission("hg.kit") && game.kitManager.hasKits()) {
            Util.sendMessage(player, lang.kit_join_msg);
            Util.sendMessage(player, " ");
            Util.sendMessage(player, lang.kit_join_avail + kit);
            Util.sendMessage(player, " ");
        }
        Util.sendMessage(player, lang.kit_join_footer);
        Util.sendMessage(player, " ");
    }

    /**
     * Respawn all players in the game back to spawn points
     */
    public void respawnAll() {
        this.randomizedSpawns.clear();
        for (Player player : this.players) {
            player.teleport(pickRandomSpawn());
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
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(this.players);
        allPlayers.addAll(this.spectators);
        for (Player player : allPlayers) {
            Util.sendMessage(player, message);
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
        List<Player> allPlayers = new ArrayList<>(this.allPlayers);
        allPlayers.addAll(this.spectators);
        for (Player player : allPlayers) {
            Util.sendPrefixedMessage(player, message);
        }
    }

    private Location pickRandomSpawn() {
        if (this.randomizedSpawns.isEmpty()) {
            this.randomizedSpawns.addAll(this.game.getGameArenaData().getSpawns());
        }
        Collections.shuffle(this.randomizedSpawns);
        Location spawn = this.randomizedSpawns.getFirst();
        this.randomizedSpawns.remove(spawn);
        return spawn;
    }

    void addPlayerData(Player player) {
        this.players.add(player);
        this.allPlayers.add(player);
        this.game.getGameBlockData().updateLobbyBlock();
    }

    void putPlayerIntoArena(Player player, boolean savePreviousLocation) {
        Location loc = pickRandomSpawn();
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
            this.game.getGameScoreboard().setupBoard(player);

            heal(player);
            freeze(player);
            this.kills.put(player, 0);
            kitHelp(player);

            this.game.getGameScoreboard().updateBoards();
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
        this.players.remove(player);
        if (!death) this.allPlayers.remove(player); // Only remove the player if they voluntarily left the game
        unFreeze(player);
        if (death) {
            if (Config.SPECTATE_ENABLED && Config.spectateOnDeath && !game.isGameOver()) {
                spectate(player);
                player.playSound(player.getLocation(), Config.SOUNDS_DEATH, 5, 1);
                player.sendTitle(this.game.getGameArenaData().getName(), Util.getColString(lang.spectator_start_title), 10, 100, 10);
                this.game.updateAfterDeath(player, true);
                return;
            } else if (this.game.getGameArenaData().getStatus() == Status.RUNNING) {
                this.game.getGameBarData().removePlayer(player);
            }
        }
        heal(player);
        PlayerData playerData = this.playerManager.getPlayerData(uuid);
        assert playerData != null;
        Location previousLocation = playerData.getPreviousLocation();

        this.game.getGameScoreboard().removePlayer(player);
        playerData.restore(player);
        exit(player, previousLocation);
        this.playerManager.removePlayerData(player);
        if (death) {
            player.playSound(player.getLocation(), Config.SOUNDS_DEATH, 5, 1);
        }
        this.game.updateAfterDeath(player, death);
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
        } else if (gameArenaData.getExit() != null && gameArenaData.getExit().getWorld() != null) {
            loc = gameArenaData.getExit();
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
        spectator.teleport(game.gameArenaData.getSpawns().getFirst());
        if (playerManager.hasPlayerData(uuid)) {
            playerManager.transferPlayerDataToSpectator(uuid);
        } else {
            playerManager.addSpectatorData(new PlayerData(spectator, game));
        }
        this.spectators.add(spectator);
        spectator.setGameMode(GameMode.SURVIVAL);
        spectator.setCollidable(false);
        if (Config.spectateFly)
            spectator.setAllowFlight(true);

        if (Config.spectateHide) {
            for (Player player : this.players) {
                player.hidePlayer(plugin, spectator);
            }
            for (Player player : this.spectators) {
                player.hidePlayer(plugin, spectator);
            }
        }
        this.game.getGameBarData().addPlayer(spectator);
        this.game.getGameScoreboard().setupBoard(spectator);
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
        if (playerData == null) return;

        Location previousLocation = playerData.getPreviousLocation();

        playerData.restore(spectator);
        spectators.remove(spectator);
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

}
