package com.shanebeestudios.hg.plugin.configs;

import com.shanebeestudios.hg.api.parsers.LocationParser;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Vault;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Main config class <b>Internal Use Only</b>
 */
public class Config {

    public static boolean SETTINGS_DEBUG;

    //Basic settings
    public static boolean SETTINGS_BROADCAST_JOIN_MESSAGES;
    public static boolean SETTINGS_BROADCAST_WIN_MESSAGES;
    public static boolean HAS_ECONOMY = true;
    public static boolean SETTINGS_BOSSBAR_COUNTDOWN;
    public static int SETTINGS_TRACKING_STICK_USES;
    public static int SETTINGS_PLAYERS_FOR_TRACKING_STICK;
    public static int SETTINGS_TELEPORT_AT_END_TIME;
    public static boolean SETTINGS_SAVE_PREVIOUS_LOCATION;
    public static int SETTINGS_FREE_ROAM_TIME;
    public static Location SETTINGS_GLOBAL_EXIT_LOCATION;

    // Scoreboard
    public static boolean SCOREBOARD_HIDE_NAMETAGS;
    public static boolean SCOREBOARD_SHOW_HEALTH_ENABLED;
    public static String SCOREBOARD_SHOW_HEALTH_DISPLAY_SLOT;
    public static String SCOREBOARD_SHOW_HEALTH_RENDER_TYPE;
    public static boolean TEAM_SHOW_TEAM_NAMES;
    public static int TEAM_MAX_TEAM_SIZE;
    public static boolean TEAM_ALLOW_FRIENDLY_FIRE;
    public static boolean TEAM_CAN_SEE_INVISIBLES;

    // Mobs
    public static boolean MOBS_SPAWN_ENABLED;
    public static int MOBS_SPAWN_INTERVAL;
    public static int MOBS_SPAWN_CAP_PER_PLAYER;

    // Reward info
    public static boolean REWARD_ENABLED;
    public static int REWARD_CASH;
    public static List<String> REWARD_COMMANDS;
    public static List<String> REWARD_MESSAGES;

    //Rollback
    public static boolean ROLLBACK_ALLOW_BREAK_BLOCKS;
    public static int ROLLBACK_BLOCKS_PER_SECOND;
    public static boolean ROLLBACK_PROTECT_DURING_FREE_ROAM;
    public static boolean ROLLBACK_PREVENT_TRAMPLING;
    public static List<String> ROLLBACK_EDITABLE_BLOCKS;
    public static boolean ROLLBACK_ALLOW_ITEMFRAME_TAKE;

    // Chests
    // Chests - Regular
    public static int CHESTS_REGULAR_MIN_CONTENT;
    public static int CHESTS_REGULAR_MAX_CONTENT;

    // Chests - Bonus
    public static int CHESTS_BONUS_MIN_CONTENT;
    public static int CHESTS_BONUS_MAX_CONTENT;
    public static List<String> CHESTS_BONUS_BLOCK_TYPES;
    // Chests - Bonus - Randomize
    public static boolean CHESTS_BONUS_RANDOMIZE_ENABLED;
    public static int CHESTS_BONUS_RANDOMIZE_CHANCE;
    public static String CHESTS_BONUS_RANDOMIZE_BLOCK;

    // Chests - Drops
    public static boolean CHESTS_CHEST_DROP_ENABLED;
    public static int CHESTS_CHEST_DROP_INTERVAL;
    public static int CHESTS_CHEST_DROP_MIN_CONTENT;
    public static int CHESTS_CHEST_DROP_MAX_CONTENT;

    //World border
    public static boolean WORLD_BORDER_ENABLED;
    public static boolean WORLD_BORDER_INITIATE_ON_START;
    public static String WORLD_BORDER_CENTER;
    public static int WORLD_BORDER_COUNTDOWN_START;
    public static int WORLD_BORDER_COUNTDOWN_END;
    public static int WORLD_BORDER_FINAL_SIZE;

    //Spectate
    public static boolean SPECTATE_ENABLED;
    public static boolean SPECTATE_DEATH_TO_SPECTATE;
    public static boolean SPECTATE_HIDE_HIDE_SPECTATORS;
    public static boolean SPECTATE_FLY;
    public static boolean SPECTATE_CHAT;

    // Sounds
    public static String SOUNDS_DEATH;
    public static String SOUNDS_OPEN_CHEST_DROP;

    private final HungerGames plugin;
    private File configFile;
    private FileConfiguration config;

    public Config(HungerGames plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("config.yml", false);
            Util.log("New config.yml <green>created");
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        matchConfig(this.config, this.configFile);
        loadConfig();
        Util.log("config.yml <green>successfully loaded");
    }


    private void loadConfig() {
        // Settings
        SETTINGS_DEBUG = config.getBoolean("settings.debug");
        SETTINGS_BROADCAST_JOIN_MESSAGES = config.getBoolean("settings.broadcast-join-messages");
        SETTINGS_BROADCAST_WIN_MESSAGES = config.getBoolean("settings.broadcast-win-messages");
        SETTINGS_BOSSBAR_COUNTDOWN = config.getBoolean("settings.bossbar-countdown");
        SETTINGS_TRACKING_STICK_USES = config.getInt("settings.tracking-stick-uses");
        SETTINGS_PLAYERS_FOR_TRACKING_STICK = config.getInt("settings.players-for-tracking-stick");
        SETTINGS_SAVE_PREVIOUS_LOCATION = config.getBoolean("settings.save-previous-location");
        SETTINGS_TELEPORT_AT_END_TIME = config.getInt("settings.teleport-at-end-time");
        SETTINGS_FREE_ROAM_TIME = config.getInt("settings.free-room-time");
        String locString = config.getString("settings.global-exit-location");
        if (locString != null && locString.contains(":")) {
            SETTINGS_GLOBAL_EXIT_LOCATION = LocationParser.getLocFromString(locString);
        }

        // Scoreboard
        SCOREBOARD_HIDE_NAMETAGS = config.getBoolean("scoreboard.hide-nametags");
        SCOREBOARD_SHOW_HEALTH_ENABLED = config.getBoolean("scoreboard.show-health.enabled");
        SCOREBOARD_SHOW_HEALTH_DISPLAY_SLOT = config.getString("scoreboard.show-health.display-slot");
        SCOREBOARD_SHOW_HEALTH_RENDER_TYPE = config.getString("scoreboard.show-health.render-type");
        TEAM_MAX_TEAM_SIZE = config.getInt("scoreboard.teams.max-team-size");
        TEAM_SHOW_TEAM_NAMES = config.getBoolean("scoreboard.teams.show-team-nametags");
        TEAM_ALLOW_FRIENDLY_FIRE = config.getBoolean("scoreboard.teams.allow-friendly-fire");
        TEAM_CAN_SEE_INVISIBLES = config.getBoolean("scoreboard.teams.can-see-friendly-invisibles");

        // Mobs
        MOBS_SPAWN_ENABLED = config.getBoolean("mob-spawning.enabled");
        MOBS_SPAWN_INTERVAL = config.getInt("mob-spawning.interval") * 20;
        MOBS_SPAWN_CAP_PER_PLAYER = config.getInt("mob-spawning.cap-per-player");

        REWARD_ENABLED = config.getBoolean("reward.enabled");
        REWARD_CASH = config.getInt("reward.cash");
        REWARD_COMMANDS = config.getStringList("reward.commands");
        REWARD_MESSAGES = config.getStringList("reward.messages");

        // Rollback
        ROLLBACK_ALLOW_BREAK_BLOCKS = config.getBoolean("rollback.allow-block-break");
        ROLLBACK_BLOCKS_PER_SECOND = config.getInt("rollback.blocks-per-second");
        ROLLBACK_PROTECT_DURING_FREE_ROAM = config.getBoolean("rollback.protect-during-free-roam");
        ROLLBACK_PREVENT_TRAMPLING = config.getBoolean("rollback.prevent-trampling");
        ROLLBACK_EDITABLE_BLOCKS = config.getStringList("rollback.editable-blocks");
        ROLLBACK_ALLOW_ITEMFRAME_TAKE = config.getBoolean("rollback.allow-itemframe-take");

        // Chests
        CHESTS_REGULAR_MIN_CONTENT = config.getInt("chests.regular.min-content");
        CHESTS_REGULAR_MAX_CONTENT = config.getInt("chests.regular.max-content");
        CHESTS_BONUS_MIN_CONTENT = config.getInt("chests.bonus.min-content");
        CHESTS_BONUS_MAX_CONTENT = config.getInt("chests.bonus.max-content");
        CHESTS_BONUS_BLOCK_TYPES = config.getStringList("chests.bonus.block-types");
        CHESTS_BONUS_RANDOMIZE_ENABLED = config.getBoolean("chests.bonus.randomize.enabled");
        CHESTS_BONUS_RANDOMIZE_CHANCE = config.getInt("chests.bonus.randomize.chance");
        CHESTS_BONUS_RANDOMIZE_BLOCK = config.getString("chests.bonus.randomize.block");
        CHESTS_CHEST_DROP_ENABLED = config.getBoolean("chests.chest-drop.enabled");
        CHESTS_CHEST_DROP_INTERVAL = config.getInt("chests.chest-drop.interval");
        CHESTS_CHEST_DROP_MIN_CONTENT = config.getInt("chests.chest-drop.min-content");
        CHESTS_CHEST_DROP_MAX_CONTENT = config.getInt("chests.chest-drop.max-content");

        WORLD_BORDER_ENABLED = config.getBoolean("world-border.enabled");
        WORLD_BORDER_INITIATE_ON_START = config.getBoolean("world-border.initiate-on-start");
        WORLD_BORDER_CENTER = config.getString("world-border.center");
        WORLD_BORDER_COUNTDOWN_START = config.getInt("world-border.countdown-start");
        WORLD_BORDER_COUNTDOWN_END = config.getInt("world-border.countdown-end");
        WORLD_BORDER_FINAL_SIZE = config.getInt("world-border.final-border-size");

        SPECTATE_ENABLED = config.getBoolean("spectate.enabled");
        SPECTATE_DEATH_TO_SPECTATE = config.getBoolean("spectate.death-to-spectate");
        SPECTATE_HIDE_HIDE_SPECTATORS = config.getBoolean("spectate.hide-spectators");
        SPECTATE_FLY = config.getBoolean("spectate.fly");
        SPECTATE_CHAT = config.getBoolean("spectate.chat");

        SOUNDS_DEATH = config.getString("sounds.death");
        SOUNDS_OPEN_CHEST_DROP = config.getString("sounds.open-chest-drop");

        try {
            Vault.setupEconomy();
            if (Vault.ECONOMY == null) {
                Util.log("<red>Unable to setup vault!");
                Util.log(" - <red>Economy provider is missing.");
                Util.log(" - <yellow>Cash rewards will not be given out..");
                REWARD_ENABLED = false;
                HAS_ECONOMY = false;
            }
        } catch (NoClassDefFoundError e) {
            Util.log("<red>Unable to setup vault!");
            Util.log("  - <yellow>Cash rewards will not be given out..");
            REWARD_ENABLED = false;
            HAS_ECONOMY = false;
        }
    }

    // Used to update config
    @SuppressWarnings({"ConstantConditions", "CallToPrintStackTrace"})
    private void matchConfig(FileConfiguration config, File file) {
        try {
            boolean hasUpdated = false;
            InputStream test = plugin.getResource(file.getName());
            assert test != null;
            InputStreamReader is = new InputStreamReader(test);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(is);
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key) && !key.contains("kits")) {
                    config.set(key, defConfig.get(key));
                    hasUpdated = true;
                }
            }
            for (String key : config.getConfigurationSection("").getKeys(true)) {
                if (!defConfig.contains(key) && !key.contains("kits.")) {
                    config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return this.config;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set and save the global exit location to config
     *
     * @param location Global exit location
     */
    public void setGlobalExitLocation(Location location) {
        String locString = LocationParser.locToString(location);
        this.config.set("settings.global-exit-location", locString);
        save();
    }

}
