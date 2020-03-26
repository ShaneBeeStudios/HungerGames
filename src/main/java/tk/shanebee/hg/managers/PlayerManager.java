package tk.shanebee.hg.managers;

import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.game.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * General player manager
 * <p>You can get an instance of this from <b>{@link HG#getPlayerManager()}</b></p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PlayerManager {

    private final Map<UUID, PlayerData> playerMap;
    private final Map<UUID, PlayerData> spectatorMap;

    public PlayerManager() {
        this.playerMap = new HashMap<>();
        this.spectatorMap = new HashMap<>();
    }

    /** Check if a player is playing a game and has PlayerData
     * @param player Player to check
     * @return True if player is playing in a game and has data
     */
    public boolean hasPlayerData(Player player) {
        return hasPlayerData(player.getUniqueId());
    }

    /** Check if a player is playing a game and has PlayerData
     * @param uuid UUID of player to check
     * @return True if player is playing in a game and has data
     */
    public boolean hasPlayerData(UUID uuid) {
        return playerMap.containsKey(uuid);
    }

    /** Check if a player is spectating a game and has PlayerData
     * @param player Player to check
     * @return True if player is spectating a game and has data
     */
    public boolean hasSpectatorData(Player player) {
        return hasSpectatorData(player.getUniqueId());
    }

    /** Check if a player is spectating a game and has PlayerData
     * @param uuid UUID of player to check
     * @return True if player is spectating a game and has data
     */
    public boolean hasSpectatorData(UUID uuid) {
        return spectatorMap.containsKey(uuid);
    }

    /** Get an instance of a player's data if player is playing in a game
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /** Get an instance of a player's data if player is playing in a game
     * @param uuid UUID of player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    public PlayerData getPlayerData(UUID uuid) {
        if (hasPlayerData(uuid)) {
            return playerMap.get(uuid);
        }
        return null;
    }

    /** Get an instance of a player's data if player is spectating a game
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not spectating a game
     */
    public PlayerData getSpectatorData(Player player) {
        return getSpectatorData(player.getUniqueId());
    }

    /** Get an instance of a player's data if player is spectating a game
     * @param uuid UUID of player to get data for
     * @return PlayerData from player, null if player is not spectating a game
     */
    public PlayerData getSpectatorData(UUID uuid) {
        if (hasSpectatorData(uuid))
            return spectatorMap.get(uuid);
        return null;
    }

    /** Get an instance of a player's data if player is in a game
     * <p>This will first check if a player is playing in a game, then check if they are spectating a game.
     * <br>If you would like specific data use {@link #getPlayerData(Player)} or {@link #getSpectatorData(Player)}</p>
     * @param player Player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    public PlayerData getData(Player player) {
        return getData(player.getUniqueId());
    }

    /** Get an instance of a player's data if player is in a game
     * <p>This will first check if a player is playing in a game, then check if they are spectating a game.
     * <br>If you would like specific data use {@link #getPlayerData(UUID)} or {@link #getSpectatorData(UUID)}</p>
     * @param uuid UUID of player to get data for
     * @return PlayerData from player, null if player is not in a game
     */
    public PlayerData getData(UUID uuid) {
        if (hasPlayerData(uuid))
            return getPlayerData(uuid);
        else if (hasSpectatorData(uuid))
            return getSpectatorData(uuid);
        return null;
    }

    public boolean hasData(Player player) {
        return hasData(player.getUniqueId());
    }

    public boolean hasData(UUID uuid) {
        return hasPlayerData(uuid) || hasSpectatorData(uuid);
    }

    /** Add a PlayerData to the stored PlayerData map
     * <p>This should mainly be used <b>internally only</b></p>
     * @param playerData PlayerData to add
     */
    public void addPlayerData(PlayerData playerData) {
        playerMap.put(playerData.getUuid(), playerData);
    }

    /** Add a PlayerData to the stored SpectatorData map
     *  <p>This should mainly be used <b>internally only</b></p>
     * @param playerData PlayerData to add
     */
    public void addSpectatorData(PlayerData playerData) {
        this.spectatorMap.put(playerData.getUuid(), playerData);
    }

    /** Remove a PlayerData from the PlayerData map
     * @param player Holder of PlayerData to remove
     */
    public void removePlayerData(Player player) {
        this.playerMap.remove(player.getUniqueId());
    }

    /** Remove a PlayerData from the PlayerData map
     * @param uuid UUID of holder of PlayerData to remove
     */
    public void removePlayerData(UUID uuid) {
        this.playerMap.remove(uuid);
    }

    /** Remove a PlayerData from the SpectatorData map
     * @param player Holder of PlayerData to remove
     */
    public void removeSpectatorData(Player player) {
        this.spectatorMap.remove(player.getUniqueId());
    }

    /** Remove a PlayerData from the SpectatorData map
     * @param uuid UUID of holder of PlayerData to remove
     */
    public void removeSpectatorData(UUID uuid) {
        this.spectatorMap.remove(uuid);
    }

    /** Get the current game of a player
     * @param player Player to get game
     * @return Game of player, null if player is not in a game
     */
    public Game getGame(Player player) {
        return getGame(player.getUniqueId());
    }

    /** Get the current game of a player
     * @param uuid UUID of player to get game
     * @return Game of player, null if player is not in a game
     */
    public Game getGame(UUID uuid) {
        if (hasPlayerData(uuid))
            return getPlayerData(uuid).getGame();
        else if (hasSpectatorData(uuid))
            return getSpectatorData(uuid).getGame();
        else
            for (Game game : HG.getPlugin().getGames()) {
                if (game.getPlayers().contains(uuid)) {
                    return game;
                }
            }
            return null;
    }

}
