package com.shanebeestudios.hg.managers;

import com.google.common.collect.ImmutableList;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.data.MobEntry;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Manager for mob spawning in games
 * <p>Get an instance of this class with {@link Game#getMobManager()}</p>
 */
@SuppressWarnings("unused")
public class MobManager {

    private final List<MobEntry> dayMobs = new ArrayList<>();
    private final List<MobEntry> nightMobs = new ArrayList<>();
    private final FileConfiguration config;

    public MobManager(Game game) {
        this.config = HungerGames.getPlugin().getMobConfig().getMobsConfig();
        loadMobs(game.getGameArenaData().getName());
    }

    @SuppressWarnings("unchecked")
    private void loadMobs(String gameName) {
        for (String time : Arrays.asList("day", "night")) {
            String mobTimeKey = "mobs." + time + ".";
            String sectionName = gameName;
            if (this.config.getConfigurationSection(mobTimeKey + gameName) == null) {
                sectionName = "default";
            }
            ConfigurationSection gameSection = config.getConfigurationSection(mobTimeKey + sectionName);
            if (gameSection == null) {
                continue;
            }
            for (String key : gameSection.getKeys(false)) {
                ConfigurationSection mobSection = gameSection.getConfigurationSection(key);
                if (mobSection == null) continue;

                MobEntry entry;
                String typeString = mobSection.getString("type");

                // MYTHIC MOB
                if (typeString == null) continue;

                if (typeString.startsWith("MM:")) {
                    if (HungerGames.getPlugin().getMmMobManager() == null) continue;

                    String mythicMob = typeString.replace("MM:", "");
                    entry = new MobEntry(mythicMob, mobSection.getInt("level"));

                }
                // REGULAR MOB
                else {
                    NamespacedKey namespacedKey = NamespacedKey.fromString(typeString);
                    if (namespacedKey == null) {
                        Util.logMini("<red>Invalid entity type <white>'<yellow>%s<white>'", typeString);
                        continue;
                    }
                    EntityType entityType = Registries.ENTITY_TYPE_REGISTRY.get(namespacedKey);
                    if (entityType == null) {
                        Util.logMini("<red>Invalid entity type <white>'<yellow>%s<white>'", namespacedKey);
                        continue;
                    }

                    entry = new MobEntry(entityType);
                    String name = mobSection.getString("name");
                    if (name != null) {
                        entry.setName(Util.getMini(name));
                    }

                    ConfigurationSection gearSection = mobSection.getConfigurationSection("gear");
                    if (gearSection != null) {
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            String slotName = slot.name().toLowerCase(Locale.ROOT);
                            if (gearSection.contains(slotName)) {
                                ConfigurationSection itemSection = gearSection.getConfigurationSection(slotName);
                                ItemStack itemStack = ItemParser.parseItem(itemSection);
                                if (itemStack != null) {
                                    entry.addGear(slot, itemStack);
                                }
                            }
                        }
                    }

                    if (mobSection.contains("potion_effects")) {
                        List<PotionEffect> potionEffects = new ArrayList<>();
                        List<Map<?, ?>> potionEffectsMapList = mobSection.getMapList("potion_effects");
                        potionEffectsMapList.forEach(map -> {
                            PotionEffect potionEffect = ItemParser.parsePotionEffect((Map<String, Object>) map);
                            potionEffects.add(potionEffect);
                        });
                        entry.addPotionEffects(potionEffects);
                    }
                }
                String deathMessageString = mobSection.getString("death_message", null);
                if (deathMessageString != null) {
                    entry.setDeathMessage(Util.getMini(deathMessageString));
                }
                int chance = mobSection.getInt("chance", 1);
                for (int i = 1; i <= chance; i++) {
                    if (time.equalsIgnoreCase("day")) {
                        this.dayMobs.add(entry);
                    } else {
                        this.nightMobs.add(entry);
                    }
                }
            }
        }
    }

    /**
     * Get list of MobEntries for daytime
     *
     * @return List of MobEntries
     */
    public List<MobEntry> getDayMobs() {
        return ImmutableList.copyOf(this.dayMobs);
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
     * Add a new mob entry to the night mobs
     *
     * @param mobEntry Mob entry to add
     */
    public void addNightMob(MobEntry mobEntry) {
        this.nightMobs.add(mobEntry);
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
