package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bounding box object for creating regions
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Bound {

    private final BoundingBox boundingBox;
    private final String world;
    private final List<Entity> entities = new ArrayList<>();

    public static Bound createNew(@NotNull Block corner1, @NotNull Block corner2) {
        BoundingBox boundingBox = BoundingBox.of(corner1, corner2);
        return new Bound(corner1.getWorld().getName(), boundingBox);
    }

    public static Bound loadFromConfig(String world, BoundingBox boundingBox) {
        return new Bound(world, boundingBox);
    }

    /**
     * Create a new bounding box between 2 sets of coordinates
     *
     * @param world       World this bound is in
     * @param boundingBox BoundingBox for this bound
     */
    private Bound(String world, BoundingBox boundingBox) {
        this.world = world;
        this.boundingBox = boundingBox;
    }

    public Integer[] getRandomLocs() {
        Random r = new Random();
        double minX = this.boundingBox.getMinX();
        double minZ = this.boundingBox.getMinZ();
        return new Integer[]{
            (int) (r.nextInt((int) this.boundingBox.getWidthX()) + minX),
            (int) this.boundingBox.getMaxY(),
            (int) (r.nextInt((int) (this.boundingBox.getWidthZ())) + minZ)};
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
     * Kill/Remove all entities in this bound
     */
    public void removeEntities() {
        this.entities.forEach(Entity::remove);
        this.entities.clear();
    }

    /**
     * Add an entity to the entity list
     *
     * @param entity The entity to add
     */
    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    /**
     * Get a list of all entities in this bound
     *
     * @return Entities in this bound
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Get location of all blocks of a type within a bound
     *
     * @param type Material type to check
     * @return ArrayList of locations of all blocks of this type in this bound
     */
    @SuppressWarnings("unused")
    public List<Location> getBlocks(@Nullable Material type) {
        World world = Bukkit.getWorld(this.world);
        assert world != null;
        List<Location> blockList = new ArrayList<>();

        for (int x = (int) this.boundingBox.getMinX(); x < this.boundingBox.getMaxX() - 1; x++) {
            for (int y = (int) this.boundingBox.getMinY(); y < this.boundingBox.getMaxY() - 1; y++) {
                for (int z = (int) this.boundingBox.getMinZ(); z < this.boundingBox.getMaxZ() - 1; z++) {

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
            ", entities=" + entities +
            '}';
    }

}
