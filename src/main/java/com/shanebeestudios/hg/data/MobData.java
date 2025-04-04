package com.shanebeestudios.hg.data;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class MobData {

    private final Random random = new Random();
    private final List<MobEntry> dayMobs = new ArrayList<>();
    private final List<MobEntry> nightMobs = new ArrayList<>();
    private int mobCount;

    /**
     * Get list of MobEntries for daytime
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getDayMobs() {
        return ImmutableList.copyOf(this.dayMobs);
    }

    /**
     * Get a random day mob
     *
     * @return Random day mob
     */
    public @Nullable MobEntry getRandomDayMob() {
        if (this.dayMobs.isEmpty()) return null;
        return this.dayMobs.get(this.random.nextInt(this.dayMobs.size()));
    }

    /**
     * Add a new mob entry to the day mobs
     *
     * @param mobEntry Mob entry to add
     */
    public void addDayMob(MobEntry mobEntry) {
        this.dayMobs.add(mobEntry);
    }

    /**
     * Get list of MobEntries for nighttime
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getNightMobs() {
        return ImmutableList.copyOf(this.nightMobs);
    }

    /**
     * Get a random night mob
     *
     * @return Random night mob
     */
    public @Nullable MobEntry getRandomNightMob() {
        if (this.nightMobs.isEmpty()) return null;
        return this.nightMobs.get(this.random.nextInt(this.nightMobs.size()));
    }

    /**
     * Add a new mob entry to the night mobs
     *
     * @param mobEntry Mob entry to add
     */
    public void addNightMob(MobEntry mobEntry) {
        this.nightMobs.add(mobEntry);
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void setMobCount(int mobCount) {
        this.mobCount = mobCount;
    }

    /**
     * Get a count of all mobs in this MobData
     *
     * @return Count of all mobs
     */
    public int getMobCount() {
        return this.mobCount;
    }

    /**
     * Get list of all MobEntries
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getAllMobs() {
        List<MobEntry> mobs = new ArrayList<>();
        mobs.addAll(this.dayMobs);
        mobs.addAll(this.nightMobs);
        return mobs;
    }

}
