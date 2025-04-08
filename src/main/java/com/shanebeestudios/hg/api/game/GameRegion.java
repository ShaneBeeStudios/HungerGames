package com.shanebeestudios.hg.api.game;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data holder for a {@link Game Game's} bounding box
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GameRegion {

    private final BoundingBox boundingBox;
    private final String world;

    public static GameRegion createNew(@NotNull Block corner1, @NotNull Block corner2) {
        BoundingBox boundingBox = BoundingBox.of(corner1, corner2);
        return new GameRegion(corner1.getWorld().getName(), boundingBox);
    }

    public static GameRegion loadFromConfig(String world, BoundingBox boundingBox) {
        return new GameRegion(world, boundingBox);
    }

    /**
     * Create a new bounding box between 2 sets of coordinates
     *
     * @param world       World this bound is in
     * @param boundingBox BoundingBox for this bound
     */
    private GameRegion(String world, BoundingBox boundingBox) {
        this.world = world;
        this.boundingBox = boundingBox;
    }

    /**
     * Get a random location within this region
     *
     * @return Random location
     */
    public Location getRandomLocation() {
        Random random = new Random();
        Location location = new Location(this.getWorld(),
            random.nextInt((int) this.boundingBox.getWidthX()) + this.boundingBox.getMinX(),
            this.boundingBox.getMaxY(),
            random.nextInt((int) this.boundingBox.getWidthZ()) + this.boundingBox.getMinZ());

        return location.getWorld().getHighestBlockAt(location, HeightMap.MOTION_BLOCKING_NO_LEAVES)
            .getLocation().add(0, 1, 0);
    }

    /**
     * Check if a location is within the region of this bound
     *
     * @param loc Location to check
     * @return True if location is within this bound
     */
    public boolean isInRegion(Location loc) {
        return this.boundingBox.contains(loc.toVector());
    }


    /**
     * Get location of all blocks of a type within a bound
     *
     * @param type Material type to check
     * @return ArrayList of locations of all blocks of this type in this bound
     */
    public List<Location> getBlocks(@Nullable Material type) {
        World world = Bukkit.getWorld(this.world);
        assert world != null;
        List<Location> blockList = new ArrayList<>();

        for (int x = (int) this.boundingBox.getMinX(); x < this.boundingBox.getMaxX(); x++) {
            for (int y = (int) this.boundingBox.getMinY(); y < this.boundingBox.getMaxY(); y++) {
                for (int z = (int) this.boundingBox.getMinZ(); z < this.boundingBox.getMaxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);
                    if (type == null || block.getType() == type) {
                        blockList.add(block.getLocation());
                    }
                }
            }
        }
        return blockList;
    }

    /**
     * Get the world of this bound
     *
     * @return World of this bound
     */
    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * Get the greater corner of this bound
     *
     * @return Location of greater corner
     */
    public Location getGreaterCorner() {
        return new Location(getWorld(), this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ());
    }

    /**
     * Get the lesser corner of this bound
     *
     * @return Location of lesser corner
     */
    public Location getLesserCorner() {
        return new Location(getWorld(), this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ());
    }

    /**
     * Get the center location of this bound
     *
     * @return The center location
     */
    public Location getCenter() {
        return new Location(getWorld(), this.boundingBox.getCenterX(), this.boundingBox.getCenterY(), this.boundingBox.getCenterZ());
    }

    @Override
    public String toString() {
        return "Bound{" +
            "boundingBox=" + boundingBox +
            ", world='" + world + '\'' +
            '}';
    }

}
