package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class for holding a {@link Game Game's} general arena data
 */
public class GameArenaData extends Data {

    private final String name;
    private final GameRegion gameRegion;
    private int timer;
    private int minPlayers;
    private int maxPlayers;
    private int freeRoamTime;
    private int cost;
    private final List<Location> spawns;
    private Location exit;
    private Status status = Status.NOT_READY;
    private int chestRefillTime = 0;
    private int chestRefillRepeat = 0;

    GameArenaData(Game game, String name, GameRegion gameRegion, int timer, int minPlayers, int maxPlayers, int freeRoamTime, int cost) {
        super(game);
        this.name = name;
        this.gameRegion = gameRegion;
        this.timer = timer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.freeRoamTime = freeRoamTime;
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
     * Get the name of this game as a {@link Component}
     *
     * @return Name of this game
     */
    public Component getNameComponent() {
        return Util.getMini(this.name);
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

    public Game checkOverlap() {
        for (Game toCheck : HungerGames.getPlugin().getGameManager().getGames()) {
            if (this.game.equals(toCheck)) continue;

            if (toCheck.getGameArenaData().getGameRegion().getBoundingBox().overlaps(this.gameRegion.getBoundingBox())) return toCheck;
        }
        return null;
    }

    /**
     * Get the free roam time of the game
     *
     * @return Free roam time
     */
    public int getFreeRoamTime() {
        return this.freeRoamTime;
    }

    /**
     * Set the free roam time of the game
     *
     * @param freeRoamTime Free roam time
     */
    public void setFreeRoamTime(int freeRoamTime) {
        this.freeRoamTime = freeRoamTime;
    }

    /**
     * Get the timer of this game
     *
     * @return How many seconds this game will run for
     */
    public int getTimer() {
        return this.timer;
    }

    /**
     * Set the timer for this game
     *
     * @param timer How many seconds this game will run for
     */
    public void setTimer(int timer) {
        this.timer = timer;
    }

    /**
     * Get max players for this game
     *
     * @return Max amount of players for this game
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Set max players for this game
     *
     * @param maxPlayers Max players
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Get min players for this game
     *
     * @return Min amount of players for this game
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Set min players for this game
     *
     * @param minPlayers Min players
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
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
     * Clear all spawns
     */
    public void clearSpawns() {
        this.spawns.clear();
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
    public Location getExitLocation() {
        return this.exit;
    }

    /**
     * Set exit location for this game
     *
     * @param location Location where players will exit
     */
    public void setExitLocation(Location location) {
        this.exit = location;
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
