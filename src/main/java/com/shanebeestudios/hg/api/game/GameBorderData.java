package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.tasks.WorldBorderTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Data class for holding a {@link Game Game's} world border
 */
public class GameBorderData extends Data {

    private final Random random = new Random();
    private boolean isDefault;
    private Location centerLocation;
    private int finalBorderSize;
    private int borderCountdownStart;
    private int borderCountdownEnd;

    private final GamePlayerData gamePlayerData;
    private final WorldBorder worldBorder;
    private WorldBorderTask worldBorderTask;

    GameBorderData(Game game) {
        this(game, null, Config.WORLD_BORDER_FINAL_SIZE, Config.WORLD_BORDER_COUNTDOWN_START, Config.WORLD_BORDER_COUNTDOWN_END);
        this.isDefault = true;
    }

    GameBorderData(Game game, Location centerLocation, int finalSize, int start, int end) {
        super(game);
        this.gamePlayerData = game.getGamePlayerData();
        this.worldBorder = Bukkit.createWorldBorder();
        this.centerLocation = centerLocation;
        this.finalBorderSize = finalSize;
        this.borderCountdownStart = start;
        this.borderCountdownEnd = end;
        this.isDefault = false;
    }

    /**
     * Get the {@link WorldBorder} of this game
     *
     * @return WorldBorder of game
     */
    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    /**
     * Initialize the {@link WorldBorder} of this game
     */
    public void initialize() {
        resetBorder();
        this.gamePlayerData.getPlayers().forEach(player -> player.setWorldBorder(this.worldBorder));
        this.worldBorderTask = new WorldBorderTask(this.game);
    }

    /**
     * Reset the {@link WorldBorder} of this game
     */
    public void resetBorder() {
        Location center;
        GameArenaData gameArenaData = this.game.getGameArenaData();
        List<Location> spawns = gameArenaData.getSpawns();
        if (this.centerLocation != null) {
            center = this.centerLocation;
        } else {
            switch (Config.WORLD_BORDER_CENTER) { // 'first-spawn', 'random-spawn' and 'arena-center'
                case "first-spawn" -> center = spawns.getFirst();
                case "random-spawn" -> center = spawns.get(this.random.nextInt(spawns.size()));
                default -> center = gameArenaData.getGameRegion().getCenter();
            }
        }
        this.worldBorder.setCenter(center);

        GameRegion bound = gameArenaData.getGameRegion();
        BoundingBox boundingBox = bound.getBoundingBox();

        double x = Math.max(center.getX() - boundingBox.getMinX(), boundingBox.getMaxX() - center.getX());
        double z = Math.max(center.getZ() - boundingBox.getMinZ(), boundingBox.getMaxZ() - center.getZ());
        double size = Math.max(x, z);
        this.worldBorder.setSize((size + 5) * 2);

        if (this.worldBorderTask != null) {
            this.worldBorderTask.stop();
            this.worldBorderTask = null;
        }
    }

    /**
     * Start the shrinking process of the {@link WorldBorder}
     *
     * @param closingIn How long for the border to take to shrink (in seconds)
     */
    public void startShrinking(int closingIn) {
        this.worldBorder.setSize(this.finalBorderSize, closingIn);
    }

    /**
     * Set the center of the border of this game
     *
     * @param centerLocation Location of the center
     */
    public void setCenterLocation(Location centerLocation) {
        this.centerLocation = centerLocation;
        this.isDefault = false;
    }

    /**
     * Get the center location of the border
     *
     * @return Center location
     */
    public @Nullable Location getCenterLocation() {
        return this.centerLocation;
    }

    /**
     * Set the final size for the border of this game
     *
     * @param finalBorderSize The final size of the border
     */
    public void setFinalBorderSize(int finalBorderSize) {
        this.finalBorderSize = finalBorderSize;
        this.isDefault = false;
    }

    /**
     * Get the final size for the border
     *
     * @return Final size
     */
    public int getFinalBorderSize() {
        return this.finalBorderSize;
    }

    /**
     * Get when the border will start to countdown
     *
     * @return Start countdown
     */
    public int getBorderCountdownStart() {
        return this.borderCountdownStart;
    }

    /**
     * Set when the border will start to countdown
     *
     * @param borderCountdownStart When to countdown
     */
    public void setBorderCountdownStart(int borderCountdownStart) {
        this.borderCountdownStart = borderCountdownStart;
        this.isDefault = false;
    }

    /**
     * Get when the border countdown will stop
     *
     * @return When countdown stops
     */
    public int getBorderCountdownEnd() {
        return this.borderCountdownEnd;
    }

    /**
     * Set when the border countdown will stop
     *
     * @param borderCountdownEnd When to stop
     */
    public void setBorderCountdownEnd(int borderCountdownEnd) {
        this.borderCountdownEnd = borderCountdownEnd;
        this.isDefault = false;
    }

    /**
     * Whether this border uses default values from config
     *
     * @return Uses default values from config
     */
    public boolean isDefault() {
        return this.isDefault;
    }

}
