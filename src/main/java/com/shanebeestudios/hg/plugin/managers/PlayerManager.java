package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.data.PlayerData;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.status.PlayerStatus;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * General player manager
 * <p>You can get an instance of this from <b>{@link HungerGames#getPlayerManager()}</b></p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PlayerManager {

    private final Map<UUID, PlayerData> playerMap;
    private final Map<UUID, PlayerData> spectatorMap;

    public PlayerManager() {
        this.playerMap = new HashMap<>();
        this.spectatorMap = new HashMap<>();
    }

    /**
     * Check if a player is playing a game and has PlayerData
     *
     * @param player Player to check
     * @return True if player is playing in a game and has data
     */
    public boolean hasPlayerData(Player player) {
        return this.playerMap.containsKey(player.getUniqueId());
    }

    /**
     * Check if a player is spectating a game and has PlayerData
     *
     * @param player Player to check
     * @return True if player is spectating a game and has data
     */
    public boolean hasSpectatorData(Player player) {
        return this.spectatorMap.containsKey(player.getUniqueId());
    }

    /**
     * Get an instance of a player's data if player is playing in a game
     *
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    @Nullable
    public PlayerData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerMap.containsKey(uuid)) {
            return this.playerMap.get(uuid);
        }
        return null;
    }

    /**
     * Get an instance of a player's data if player is spectating a game
     *
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not spectating a game
     */
    @Nullable
    public PlayerData getSpectatorData(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.spectatorMap.containsKey(uuid)) {
            return spectatorMap.get(uuid);
        }
        return null;
    }

    /**
     * Get an instance of a player's data if player is in a game
     * <p>This will first check if a player is playing in a game, then check if they are spectating a game.
     * <br>If you would like specific data use {@link #getPlayerData(Player)} or {@link #getSpectatorData(Player)}</p>
     *
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    @Nullable
    public PlayerData getData(Player player) {
        if (hasPlayerData(player))
            return getPlayerData(player);
        else if (hasSpectatorData(player))
            return getSpectatorData(player);
        return null;
    }

    /**
     * Create {@link PlayerData} for a player
     *
     * @param player Player to create data for
     * @param game   Game player is entering
     */
    public void createPlayerData(Player player, Game game) {
        PlayerData playerData = new PlayerData(player, game);
        this.playerMap.put(player.getUniqueId(), playerData);
    }

    /**
     * Create {@link PlayerData} for a spectator
     *
     * @param spectator Player to create data for
     * @param game      Game player is entering
     */
    public void createSpectatorData(Player spectator, Game game) {
        PlayerData playerData = new PlayerData(spectator, game);
        this.spectatorMap.put(spectator.getUniqueId(), playerData);
    }

    /**
     * Remove a PlayerData from the PlayerData map
     *
     * @param player Holder of PlayerData to remove
     */
    public void removePlayerData(Player player) {
        this.playerMap.remove(player.getUniqueId());
    }

    /**
     * Remove a PlayerData from the PlayerData map
     *
     * @param uuid UUID of holder of PlayerData to remove
     */
    public void removePlayerData(UUID uuid) {
        this.playerMap.remove(uuid);
    }

    /**
     * Remove a PlayerData from the SpectatorData map
     *
     * @param player Holder of PlayerData to remove
     */
    public void removeSpectatorData(Player player) {
        this.spectatorMap.remove(player.getUniqueId());
    }

    /**
     * Remove a PlayerData from the SpectatorData map
     *
     * @param uuid UUID of holder of PlayerData to remove
     */
    public void removeSpectatorData(UUID uuid) {
        this.spectatorMap.remove(uuid);
    }

    /**
     * Transfer {@link PlayerData} from Player to Spectator
     *
     * @param player Player to transfer
     */
    public void transferPlayerDataToSpectator(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.playerMap.containsKey(uuid)) {
            PlayerData clone = this.playerMap.get(uuid).clone();
            if (clone != null) {
                this.spectatorMap.put(uuid, clone);
                this.playerMap.remove(uuid);
            }
        }
    }

    /**
     * Get the current game of a player
     *
     * @param player Player to get game
     * @return Game of player, null if player is not in a game
     */
    @SuppressWarnings("DataFlowIssue")
    public @Nullable Game getGame(Player player) {
        if (hasPlayerData(player))
            return getPlayerData(player).getGame();
        else if (hasSpectatorData(player))
            return getSpectatorData(player).getGame();
        else
            return null;
    }

    /**
     * Check if a player is already in a game
     *
     * @param player Player to check
     * @return True if in game, otherwise false
     */
    public boolean isInGame(Player player) {
        return getPlayerStatus(player) != PlayerStatus.NOT_IN_GAME;
    }

    /**
     * Get the status of a player
     *
     * @param player Player to get status for
     * @return Status of player
     */
    public PlayerStatus getPlayerStatus(Player player) {
        if (hasPlayerData(player)) return PlayerStatus.IN_GAME;
        else if (hasSpectatorData(player)) return PlayerStatus.SPECTATOR;
        else return PlayerStatus.NOT_IN_GAME;
    }

}
