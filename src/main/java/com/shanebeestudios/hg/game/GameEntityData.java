package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.data.MobData;
import com.shanebeestudios.hg.data.MobEntry;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Data holder for a {@link Game Game's} entities
 */
@SuppressWarnings("unused")
public class GameEntityData extends Data {

    private MobData mobData;

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
                this.game.getGameArenaData().getGameRegion().addEntity(spawn);
                return true;
            }
        }
        return false;
    }

}
