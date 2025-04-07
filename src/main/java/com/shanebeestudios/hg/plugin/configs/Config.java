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
    public static boolean broadcastJoinMessages;
    public static boolean broadcastWinMessages;
    public static boolean economy = true;
    public static boolean bossbar;
    public static int TRACKING_STICK_USES;
    public static int PLAYERS_FOR_TRACKING_STICK;
    public static int SETTINGS_TELEPORT_AT_END_TIME;
    public static boolean savePreviousLocation;
    public static int SETTINGS_FREE_ROAM_TIME;
    public static Location GLOBAL_EXIT_LOCATION;

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

    //Reward info
    public static boolean giveReward;
    public static int cash;
    public static List<String> rewardCommands;
    public static List<String> rewardMessages;

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
    public static boolean WORLD_BORDER_CENTER_ON_FIRST_SPAWN;
    public static int WORLD_BORDER_COUNTDOWN_START;
    public static int WORLD_BORDER_COUNTDOWN_END;
    public static int WORLD_BORDER_FINAL_SIZE;

    //Spectate
    public static boolean SPECTATE_ENABLED;
    public static boolean spectateOnDeath;
    public static boolean SPECTATE_HIDE;
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
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            Util.log("New config.yml <green>created");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        matchConfig(config, configFile);
        loadConfig();
        Util.log("config.yml <green>successfully loaded");
    }


    private void loadConfig() {
        // Settings
        SETTINGS_DEBUG = config.getBoolean("settings.debug");
        broadcastJoinMessages = config.getBoolean("settings.broadcast-join-messages");
        broadcastWinMessages = config.getBoolean("settings.broadcast-win-messages");
        bossbar = config.getBoolean("settings.bossbar-countdown");
        TRACKING_STICK_USES = config.getInt("settings.trackingstick-uses");
        PLAYERS_FOR_TRACKING_STICK = config.getInt("settings.players-for-trackingstick");
        savePreviousLocation = config.getBoolean("settings.save-previous-location");
        SETTINGS_TELEPORT_AT_END_TIME = config.getInt("settings.teleport-at-end-time");
        SETTINGS_FREE_ROAM_TIME = config.getInt("settings.free-room-time");
        String locString = config.getString("settings.global-exit-location");
        if (locString != null && locString.contains(":")) {
            GLOBAL_EXIT_LOCATION = LocationParser.getLocFromString(locString);
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

        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");
        rewardCommands = config.getStringList("reward.commands");
        rewardMessages = config.getStringList("reward.messages");
        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");

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
        WORLD_BORDER_CENTER_ON_FIRST_SPAWN = config.getBoolean("world-border.center-on-first-spawn");
        WORLD_BORDER_COUNTDOWN_START = config.getInt("world-border.countdown-start");
        WORLD_BORDER_COUNTDOWN_END = config.getInt("world-border.countdown-end");
        WORLD_BORDER_FINAL_SIZE = config.getInt("world-border.final-border-size");

        SPECTATE_ENABLED = config.getBoolean("spectate.enabled");
        spectateOnDeath = config.getBoolean("spectate.death-to-spectate");
        SPECTATE_HIDE = config.getBoolean("spectate.hide-spectators");
        SPECTATE_FLY = config.getBoolean("spectate.fly");
        SPECTATE_CHAT = config.getBoolean("spectate.chat");

        SOUNDS_DEATH = config.getString("sounds.death");
        SOUNDS_OPEN_CHEST_DROP = config.getString("sounds.open-chest-drop");

        try {
            Vault.setupEconomy();
            if (Vault.economy == null) {
                Util.log("<red>Unable to setup vault!");
                Util.log(" - <red>Economy provider is missing.");
                Util.log(" - <yellow>Cash rewards will not be given out..");
                giveReward = false;
                economy = false;
            }
        } catch (NoClassDefFoundError e) {
            Util.log("<red>Unable to setup vault!");
            Util.log("  - <yellow>Cash rewards will not be given out..");
            giveReward = false;
            economy = false;
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
