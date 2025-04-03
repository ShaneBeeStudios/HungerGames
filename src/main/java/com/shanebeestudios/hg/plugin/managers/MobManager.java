package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.MobData;
import com.shanebeestudios.hg.data.MobEntry;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.HungerGames;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Manager for mob spawning in games
 * <p>Get an instance of this class with {@link HungerGames#getMobManager()}</p>
 */
@SuppressWarnings("unused")
public class MobManager {

    private final HungerGames plugin;
    private final Random random = new Random();
    private MobData defaultMobData;

    public MobManager(HungerGames plugin) {
        this.plugin = plugin;
        loadDefaultMobs();
    }

    public void loadDefaultMobs() {
        Util.log("Loading mobs:");
        File kitFile = new File(this.plugin.getDataFolder(), "mobs.yml");

        if (!kitFile.exists()) {
            this.plugin.saveResource("mobs.yml", false);
            Util.log("- New mobs.yml file has been <green>successfully generated!");
        }
        YamlConfiguration mobConfig = YamlConfiguration.loadConfiguration(kitFile);
        ConfigurationSection mobsSection = mobConfig.getConfigurationSection("mobs");
        assert mobsSection != null;
        this.defaultMobData = createMobData(mobsSection, null);
        Util.log("- <aqua>%s <grey>mobs have been <green>successfully loaded!", this.defaultMobData.getMobCount());
    }

    public void loadGameMobs(Game game, ConfigurationSection arenaSection) {
        ConfigurationSection mobsSection = arenaSection.getConfigurationSection("mobs");
        if (mobsSection == null) {
            game.getGameEntityData().setMobData(this.defaultMobData);
            return;
        }

        MobData mobData = createMobData(mobsSection, game);
        Util.log("- Loaded <aqua>%s <grey>custom mobs for arena: <aqua>%s",
            mobData.getMobCount(), game.getGameArenaData().getName());
        game.getGameEntityData().setMobData(mobData);
    }

    @SuppressWarnings("unchecked")
    public MobData createMobData(ConfigurationSection mobsSection, @Nullable Game game) {
        MobData mobData = new MobData();
        String gameName = game != null ? game.getGameArenaData().getName() + ":" : "";
        for (String time : Arrays.asList("day", "night")) {
            if (!mobsSection.contains(time)) continue;
            ConfigurationSection timeSection = mobsSection.getConfigurationSection(time);
            assert timeSection != null;

            for (String key : timeSection.getKeys(false)) {
                ConfigurationSection mobSection = timeSection.getConfigurationSection(key);
                assert mobSection != null;

                String typeString = mobSection.getString("type");
                if (typeString == null) continue;

                MobEntry mobEntry;
                // MYTHIC MOB
                if (typeString.startsWith("MM:")) {
                    if (this.plugin.getMythicMobManager() == null) continue;

                    String mythicMob = typeString.replace("MM:", "");
                    double level = mobSection.getDouble("level", 0);

                    Optional<MythicMob> mob = this.plugin.getMythicMobManager().getMythicMob(mythicMob);
                    if (mob.isPresent()) {
                        mobEntry = new MobEntry(mob.get(), level);
                    } else {
                        Util.warning("Invalid MythicMob: %s", mythicMob);
                        continue;
                    }

                }
                // REGULAR MOB
                else {
                    NamespacedKey namespacedKey = NamespacedKey.fromString(typeString);
                    if (namespacedKey == null) {
                        Util.log("<red>Invalid entity type <white>'<yellow>%s<white>'", typeString);
                        continue;
                    }
                    EntityType entityType = Registries.ENTITY_TYPE_REGISTRY.get(namespacedKey);
                    if (entityType == null) {
                        Util.log("<red>Invalid entity type <white>'<yellow>%s<white>'", namespacedKey);
                        continue;
                    }

                    mobEntry = new MobEntry(entityType);
                    if (mobSection.contains("name")) {
                        String name = mobSection.getString("name");
                        mobEntry.setName(Util.getMini(name));
                    }

                    ConfigurationSection gearSection = mobSection.getConfigurationSection("gear");
                    if (gearSection != null) {
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            String slotName = slot.name().toLowerCase(Locale.ROOT);
                            if (gearSection.contains(slotName)) {
                                ConfigurationSection itemSection = gearSection.getConfigurationSection(slotName);
                                ItemStack itemStack = ItemParser.parseItem(itemSection);
                                if (itemStack != null) {
                                    mobEntry.addGear(slot, itemStack);
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
                        mobEntry.addPotionEffects(potionEffects);
                    }
                }
                String deathMessage = mobSection.getString("death_message", null);
                if (deathMessage != null) {
                    mobEntry.setDeathMessage(deathMessage);
                }
                int weight = mobSection.getInt("weight", 1);
                for (int i = 1; i <= weight; i++) {
                    if (time.equalsIgnoreCase("day")) {
                        mobData.addDayMob(mobEntry);
                    } else {
                        mobData.addNightMob(mobEntry);
                    }
                }
            }
        }
        return mobData;
    }


}
