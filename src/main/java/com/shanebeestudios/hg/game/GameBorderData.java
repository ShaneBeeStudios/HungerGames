package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.tasks.WorldBorderTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;

/**
 * Data class for holding a {@link Game Game's} world border
 */
public class GameBorderData extends Data {

    private boolean isDefault;
    private Location centerLocation;
    private int finalBorderSize;
    private int borderCountdownStart;
    private int borderCountdownEnd;

    private final GamePlayerData gamePlayerData;
    private final WorldBorder worldBorder;
    private WorldBorderTask worldBorderTask;

    protected GameBorderData(Game game) {
        this(game, null, Config.WORLD_BORDER_FINAL_SIZE, Config.WORLD_BORDER_COUNTDOWN_START, Config.WORLD_BORDER_COUNTDOWN_END);
        this.isDefault = true;
    }

    protected GameBorderData(Game game, Location centerLocation, int finalSize, int start, int end) {
        super(game);
        this.gamePlayerData = game.getGamePlayerData();
        this.worldBorder = Bukkit.createWorldBorder();
        this.centerLocation = centerLocation;
        this.finalBorderSize = finalSize;
        this.borderCountdownStart = start;
        this.borderCountdownEnd = end;
        this.isDefault = false;
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public void initialize() {
        resetBorder();
        this.gamePlayerData.getPlayers().forEach(player -> player.setWorldBorder(this.worldBorder));
        this.worldBorderTask = new WorldBorderTask(this.game);
    }

    public void resetBorder() {
        Location center;
        if (this.centerLocation != null) {
            center = this.centerLocation;
        } else if (Config.WORLD_BORDER_CENTER_ON_FIRST_SPAWN) {
            center = this.game.getGameArenaData().getSpawns().getFirst();
        } else {
            center = this.game.getGameArenaData().getGameRegion().getCenter();
        }
        this.worldBorder.setCenter(center);

        GameRegion bound = this.game.getGameArenaData().getGameRegion();
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

    public int getFinalBorderSize() {
        return this.finalBorderSize;
    }

    public int getBorderCountdownStart() {
        return this.borderCountdownStart;
    }

    public void setBorderCountdownStart(int borderCountdownStart) {
        this.borderCountdownStart = borderCountdownStart;
        this.isDefault = false;
    }

    public int getBorderCountdownEnd() {
        return this.borderCountdownEnd;
    }

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
