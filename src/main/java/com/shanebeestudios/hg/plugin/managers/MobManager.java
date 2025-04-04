package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.registry.Registries;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.MobData;
import com.shanebeestudios.hg.data.MobEntry;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
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

    private void loadDefaultMobs() {
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

    /**
     * Get the default MobData
     * <p>This is from the mobs.yml file</p>
     *
     * @return Default MobData
     */
    public MobData getDefaultMobData() {
        return this.defaultMobData;
    }

    /**
     * Load MobData from an arena config
     *
     * @param game        Game to add data to
     * @param arenaConfig Section of config to grab data from
     */
    public void loadGameMobs(Game game, ConfigurationSection arenaConfig) {
        ConfigurationSection mobsSection = arenaConfig.getConfigurationSection("mobs");
        if (mobsSection == null) return;

        MobData mobData = createMobData(mobsSection, game);
        Util.log("- Loaded <aqua>%s <grey>custom mobs for arena <white>'<aqua>%s<white>'",
            mobData.getMobCount(), game.getGameArenaData().getName());
        game.getGameEntityData().setMobData(mobData);
    }

    @SuppressWarnings("unchecked")
    private MobData createMobData(ConfigurationSection mobsSection, @Nullable Game game) {
        MobData mobData = new MobData();
        int count = 0;
        String gameName = game != null ? game.getGameArenaData().getName() + ":" : "";
        for (String time : Arrays.asList("day", "night")) {
            if (!mobsSection.contains(time)) continue;
            ConfigurationSection timeSection = mobsSection.getConfigurationSection(time);
            assert timeSection != null;

            for (String sectionKey : timeSection.getKeys(false)) {
                String mobEntryKey = gameName + time + ":" + sectionKey;
                ConfigurationSection mobSection = timeSection.getConfigurationSection(sectionKey);
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
                        mobEntry = new MobEntry(mobEntryKey, mob.get(), level);
                    } else {
                        Util.warning("Invalid MythicMob: %s", mythicMob);
                        continue;
                    }

                }
                // REGULAR MOB
                else {
                    // TYPE
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

                    mobEntry = new MobEntry(mobEntryKey, entityType);
                    if (mobSection.contains("name")) {
                        String name = mobSection.getString("name");
                        mobEntry.setName(Util.getMini(name));
                    }

                    // GEAR
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

                    // POTION EFFECTS
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
                // ATTRIBUTES
                if (mobSection.contains("attributes")) {
                    for (String attributeString : mobSection.getStringList("attributes")) {
                        Util.log("Creating attribute: %s", attributeString);
                        String[] split = attributeString.split("=");
                        NamespacedKey attributeKey = NamespacedKey.fromString(split[0]);
                        if (attributeKey == null) {
                            Util.warning("Attribute key isn't valid '%s' for mob entry '%s:%s'",
                                attributeString, time, sectionKey);
                            continue;
                        }
                        Attribute attribute = Registries.ATTRIBUTE_REGISTRY.get(attributeKey);
                        if (attribute == null) {
                            Util.warning("Invalid attribute '%s' for mob entry '%s:%s'",
                                attributeKey.toString(), time, sectionKey);
                            continue;
                        }

                        double value = Double.parseDouble(split[1]);
                        mobEntry.addAttribute(attribute, value);
                    }
                }

                // NBT
                if (mobSection.contains("nbt")) {
                    String nbtString = mobSection.getString("nbt");
                    if (nbtString != null) {
                        String validated = NBTApi.validateNBT(nbtString);
                        if (validated != null) {
                            Util.warning("Invalid NBT '%s' for mob entry '%s:%s'",
                                nbtString, time, sectionKey);
                        } else {
                            mobEntry.setNbt(nbtString);
                        }
                    }
                }

                // DEATH MESSAGE
                String deathMessage = mobSection.getString("death_message", null);
                if (deathMessage != null) {
                    mobEntry.setDeathMessage(deathMessage);
                }
                int weight = mobSection.getInt("weight", 1);
                count++;
                for (int i = 1; i <= weight; i++) {
                    if (time.equalsIgnoreCase("day")) {
                        mobData.addDayMob(mobEntry);
                    } else {
                        mobData.addNightMob(mobEntry);
                    }
                }
                if (Config.SETTINGS_DEBUG) {
                    Util.log("- Loaded mob entry <white>'<aqua>%s<white>'", mobEntryKey);
                }
            }
        }
        mobData.setMobCount(count);
        return mobData;
    }


}
