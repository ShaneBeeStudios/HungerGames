package com.shanebeestudios.hg.api.data;

import com.google.common.collect.ImmutableList;
import com.shanebeestudios.hg.api.util.WeightedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class MobData {

    private final Random random = new Random();
    private final WeightedList<MobEntry> dayMobs = new WeightedList<>();
    private final WeightedList<MobEntry> nightMobs = new WeightedList<>();
    private int mobCount;

    /**
     * Get list of MobEntries for daytime
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getDayMobs() {
        return ImmutableList.copyOf(this.dayMobs.getEntries());
    }

    /**
     * Get a random day mob
     *
     * @return Random day mob
     */
    public @Nullable MobEntry getRandomDayMob() {
        if (this.dayMobs.isEmpty()) return null;
        return this.dayMobs.nextEntry();
    }

    /**
     * Add a new mob entry to the day mobs
     *
     * @param mobEntry Mob entry to add
     * @param weight   Weight of mob entry
     */
    public void addDayMob(MobEntry mobEntry, int weight) {
        this.dayMobs.add(mobEntry, weight);
    }

    /**
     * Get list of MobEntries for nighttime
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getNightMobs() {
        return ImmutableList.copyOf(this.nightMobs.getEntries());
    }

    /**
     * Get a random night mob
     *
     * @return Random night mob
     */
    public @Nullable MobEntry getRandomNightMob() {
        if (this.nightMobs.isEmpty()) return null;
        return this.nightMobs.nextEntry();
    }

    /**
     * Add a new mob entry to the night mobs
     *
     * @param mobEntry Mob entry to add
     * @param weight   Weight of mob entry
     */
    public void addNightMob(MobEntry mobEntry, int weight) {
        this.nightMobs.add(mobEntry, weight);
    }

    /**
     * Get a count of all mobs in this MobData
     *
     * @return Count of all mobs
     */
    public int getMobCount() {
        return this.dayMobs.size() + this.nightMobs.size();
    }

    /**
     * Get a list of all MobEntries
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getAllMobs() {
        List<MobEntry> mobs = new ArrayList<>();
        mobs.addAll(this.dayMobs.getEntries());
        mobs.addAll(this.nightMobs.getEntries());
        return mobs;
    }

}
