package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
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
public class GameRegion {

    private static final FixedMetadataValue SPAWN_KEY = new FixedMetadataValue(HungerGames.getPlugin(), true);

    private final BoundingBox boundingBox;
    private final String world;
    private final List<Entity> entities = new ArrayList<>();

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
     * Kill/Remove all entities in this bound
     */
    public void removeEntities() {
        List<Entity> entitiesToRemove = new ArrayList<>(this.entities);
        entitiesToRemove.forEach(Entity::remove);
        this.entities.clear();
    }

    /**
     * Remove an entity from this bound
     *
     * @param entity Entity to remove
     */
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    /**
     * Add an entity to the entity list
     *
     * @param entity The entity to add
     */
    public void addEntity(@NotNull Entity entity) {
        if (this.entities.contains(entity)) return;
        entity.setPersistent(false);
        entity.setMetadata("hunger-games-spawned", SPAWN_KEY);
        this.entities.add(entity);
    }

    /**
     * Check if this bound already contains an entity
     *
     * @param entity Entity to check
     * @return True if entity is already in this bound
     */
    public boolean hasEntity(Entity entity) {
        return this.entities.contains(entity);
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
     * Get amount of entities already in this bound
     *
     * @return Entities in this bound
     */
    public int getEntityCount() {
        return this.entities.size();
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
