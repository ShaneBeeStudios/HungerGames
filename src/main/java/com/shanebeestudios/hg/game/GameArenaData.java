package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.api.status.Status;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class for holding a {@link Game Game's} general arena data
 */
public class GameArenaData extends Data {

    final String name;
    final GameRegion gameRegion;
    final int timer;
    final int minPlayers;
    final int maxPlayers;
    private final int roamTime;
    int cost;
    final List<Location> spawns;
    Location exit;
    Location persistentExit;
    private Status status = Status.NOT_READY;
    int chestRefillTime = 0;
    int chestRefillRepeat = 0;

    public GameArenaData(Game game, String name, GameRegion gameRegion, int timer, int minPlayers, int maxPlayers, int roamTime, int cost) {
        super(game);
        this.name = name;
        this.gameRegion = gameRegion;
        this.timer = timer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.roamTime = roamTime;
        this.cost = cost;
        this.spawns = new ArrayList<>();
    }

    /**
     * Get the bounding box of this game
     *
     * @return Bound of this game
     */
    public GameRegion getGameRegion() {
        return this.gameRegion;
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
        return gameRegion.isInRegion(location);
    }

    /**
     * Get the roam time of the game
     *
     * @return The roam time
     */
    public int getRoamTime() {
        return this.roamTime;
    }

    public int getTimer() {
        return this.timer;
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

    /**
     * Get the cost for this game
     * <p>This is the price the player must pay to play this game</p>
     *
     * @return Cost of game
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Set the cost for this game
     * <p>This is the price the player must pay to play this game</p>
     *
     * @param cost Cost of game
     */
    public void setCost(int cost) {
        this.cost = cost;
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
     * Add a spawn location to the game
     *
     * @param location The location to add
     */
    public void addSpawn(Location location) {
        this.spawns.add(location);
    }

    /**
     * Set the status of the game
     *
     * @param status Status to set
     */
    public void setStatus(Status status) {
        this.status = status;
        this.game.getGameBlockData().updateLobbyBlock();
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
     * Get the exit location associated with this game
     *
     * @return Exit location
     */
    public Location getExit() {
        return this.exit;
    }

    public @Nullable Location getPersistentExit() {
        return this.persistentExit;
    }

    /**
     * Set exit location for this game
     *
     * @param location Location where players will exit
     */
    public void setExit(Location location, boolean persistent) {
        this.exit = location;
        if (persistent) this.persistentExit = location;
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
     * Set the chest refill repeat time
     * <p>NOTE: in 30 second increments</p>
     *
     * @param chestRefillRepeat The increment for chest refills
     */
    public void setChestRefillRepeat(int chestRefillRepeat) {
        this.chestRefillRepeat = chestRefillRepeat;
    }

    /**
     * Get the chest refill repeat time
     *
     * @return The increment for chest refills
     */
    public int getChestRefillRepeat() {
        return chestRefillRepeat;
    }

}
