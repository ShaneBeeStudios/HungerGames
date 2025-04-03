package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.MobData;
import com.shanebeestudios.hg.data.MobEntry;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Data holder for a {@link Game Game's} entities
 */
@SuppressWarnings("unused")
public class GameEntityData extends Data {

    private static final FixedMetadataValue SPAWN_KEY = new FixedMetadataValue(HungerGames.getPlugin(), true);

    private MobData mobData;
    private final List<Entity> entities = new ArrayList<>();

    GameEntityData(Game game) {
        super(game);
    }

    /**
     * Get the MobData of this game
     *
     * @return MobData of this game
     */
    public MobData getMobData() {
        return this.mobData;
    }

    /**
     * Set the MobData for this game
     *
     * @param mobData MobData for this game
     */
    public void setMobData(MobData mobData) {
        this.mobData = mobData;
    }

    /**
     * Spawn a mob from MobData
     *
     * @param location Location to spawn
     * @param dayTime  Whether it's daytime/nighttime
     * @return Whether an entity spawned
     */
    public boolean spawnMob(Location location, boolean dayTime) {
        MobEntry mobEntry;
        if (dayTime) {
            mobEntry = this.mobData.getRandomDayMob();
        } else {
            mobEntry = this.mobData.getRandomNightMob();
        }
        if (mobEntry != null) {
            Entity spawn = mobEntry.spawn(location);
            if (spawn != null) {
                logEntity(spawn);
                return true;
            }
        }
        return false;
    }

    /**
     * Kill/Remove all entities in the arena
     */
    public void removeEntities() {
        List<Entity> entitiesToRemove = new ArrayList<>(this.entities);
        entitiesToRemove.forEach(Entity::remove);
        this.entities.clear();
    }

    /**
     * Remove a logged entity
     *
     * @param entity Entity to remove
     */
    public void removeEntityFromLog(Entity entity) {
        this.entities.remove(entity);
    }

    /**
     * Log an entity to be removed later
     *
     * @param entity Entity to log
     */
    public void logEntity(@NotNull Entity entity) {
        if (this.entities.contains(entity)) return;
        entity.setPersistent(false);
        entity.setMetadata("hunger-games-spawned", SPAWN_KEY);
        this.entities.add(entity);
    }

    /**
     * Check if this entity is already logged
     *
     * @param entity Entity to check
     * @return True if entity is already logged
     */
    public boolean hasLoggedEntity(Entity entity) {
        return this.entities.contains(entity);
    }

    /**
     * Get a list of all entities logged
     *
     * @return Entities logged
     */
    public List<Entity> getLoggedEntities() {
        return this.entities;
    }

    /**
     * Get amount of entities logged
     *
     * @return Count of entities logged
     */
    public int getLoggedEntityCount() {
        return this.entities.size();
    }

}
