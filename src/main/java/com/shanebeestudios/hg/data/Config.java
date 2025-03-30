package com.shanebeestudios.hg.data;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.shanebeestudios.hg.HungerGames;
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

    public static boolean debug;

    //Basic settings
    public static boolean broadcastJoinMessages;
    public static boolean broadcastWinMessages;
    public static boolean economy = true;
    public static boolean bossbar;
    public static int trackingstickuses;
    public static int playersfortrackingstick;
    public static int maxchestcontent;
    public static int minchestcontent;
    public static int maxbonuscontent;
    public static int minbonuscontent;
    public static boolean teleportEnd;
    public static int teleportEndTime;
    public static List<String> SETTINGS_BONUS_BLOCK_TYPES;
    public static boolean HIDE_NAMETAGS;
    public static boolean savePreviousLocation;

    // Mobs
    public static boolean MOBS_SPAWN_ENABLED;
    public static int MOBS_SPAWN_INTERVAL;
    public static int MOBS_SPAWN_CAP_PER_PLAYER;

    //Team info
    public static boolean TEAM_SHOW_TEAM_NAMES;
    public static int team_maxTeamSize;
    public static boolean TEAM_ALLOW_FRIENDLY_FIRE;
    public static boolean TEAM_CAN_SEE_INVISIBLES;

    //Reward info
    public static boolean giveReward;
    public static int cash;
    public static List<String> rewardCommands;
    public static List<String> rewardMessages;

    //Rollback config info
    public static boolean breakblocks;
    public static int blocks_per_second;
    public static boolean protectCooldown;
    public static boolean fixleaves;
    public static boolean preventtrample;
    public static List<String> ROLLBACK_EDITABLE_BLOCKS;
    public static boolean itemframe_take;

    //Random chest
    public static boolean randomChest;
    public static int randomChestInterval;
    public static int randomChestMaxContent;

    //World border
    public static boolean borderEnabled;
    public static boolean borderOnStart;
    public static boolean centerSpawn;
    public static int borderCountdownStart;
    public static int borderCountdownEnd;
    public static int borderFinalSize;

    //Spectate
    public static boolean spectateEnabled;
    public static boolean spectateOnDeath;
    public static boolean spectateHide;
    public static boolean spectateFly;
    public static boolean spectateChat;

    // Sounds
    public static String SOUNDS_DEATH;
    public static String SOUNDS_OPEN_CHEST_DROP;

    //mcMMO
    public static boolean mcmmoUseSkills;
    public static boolean mcmmoGainExp;

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
        debug = config.getBoolean("settings.debug");
        broadcastJoinMessages = config.getBoolean("settings.broadcast-join-messages");
        broadcastWinMessages = config.getBoolean("settings.broadcast-win-messages");
        bossbar = config.getBoolean("settings.bossbar-countdown");
        trackingstickuses = config.getInt("settings.trackingstick-uses");
        playersfortrackingstick = config.getInt("settings.players-for-trackingstick");
        maxchestcontent = config.getInt("settings.max-chestcontent");
        minchestcontent = config.getInt("settings.min-chestcontent");
        maxbonuscontent = config.getInt("settings.max-bonus-chestcontent");
        minbonuscontent = config.getInt("settings.min-bonus-chestcontent");
        HIDE_NAMETAGS = config.getBoolean("settings.hide-nametags");
        savePreviousLocation = config.getBoolean("settings.save-previous-location");
        SETTINGS_BONUS_BLOCK_TYPES = config.getStringList("settings.bonus-block-types");

        // Mobs
        MOBS_SPAWN_ENABLED = config.getBoolean("mob-spawning.enabled");
        MOBS_SPAWN_INTERVAL = config.getInt("mob-spawning.interval") * 20;
        MOBS_SPAWN_CAP_PER_PLAYER = config.getInt("mob-spawning.cap-per-player");

        // Team
        team_maxTeamSize = config.getInt("team.max-team-size");
        TEAM_SHOW_TEAM_NAMES = config.getBoolean("team.show-team-nametags");
        TEAM_ALLOW_FRIENDLY_FIRE = config.getBoolean("team.allow-friendly-fire");
        TEAM_CAN_SEE_INVISIBLES = config.getBoolean("team.can-see-friendly-invisibles");

        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");
        rewardCommands = config.getStringList("reward.commands");
        rewardMessages = config.getStringList("reward.messages");
        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");
        breakblocks = config.getBoolean("rollback.allow-block-break");
        blocks_per_second = config.getInt("rollback.blocks-per-second");
        protectCooldown = config.getBoolean("rollback.protect-during-cooldown");
        fixleaves = config.getBoolean("rollback.fix-leaves");
        preventtrample = config.getBoolean("rollback.prevent-trampling");
        ROLLBACK_EDITABLE_BLOCKS = config.getStringList("rollback.editable-blocks");
        itemframe_take = config.getBoolean("rollback.allow-itemframe-take");

        randomChest = config.getBoolean("random-chest.enabled");
        randomChestInterval = config.getInt("random-chest.interval") * 20;
        randomChestMaxContent = config.getInt("random-chest.max-chestcontent");
        teleportEnd = config.getBoolean("settings.teleport-at-end");
        teleportEndTime = config.getInt("settings.teleport-at-end-time");

        borderEnabled = config.getBoolean("world-border.enabled");
        borderOnStart = config.getBoolean("world-border.initiate-on-start");
        centerSpawn = config.getBoolean("world-border.center-on-first-spawn");
        borderCountdownStart = config.getInt("world-border.countdown-start");
        borderCountdownEnd = config.getInt("world-border.countdown-end");
        borderFinalSize = config.getInt("world-border.final-border-size");

        spectateEnabled = config.getBoolean("spectate.enabled");
        spectateOnDeath = config.getBoolean("spectate.death-to-spectate");
        spectateHide = config.getBoolean("spectate.hide-spectators");
        spectateFly = config.getBoolean("spectate.fly");
        spectateChat = config.getBoolean("spectate.chat");

        SOUNDS_DEATH = config.getString("sounds.death");
        SOUNDS_OPEN_CHEST_DROP = config.getString("sounds.open-chest-drop");

        mcmmoUseSkills = config.getBoolean("mcmmo.use-skills");
        mcmmoGainExp = config.getBoolean("mcmmo.gain-experience");

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
    @SuppressWarnings("ConstantConditions")
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
                if (!defConfig.contains(key) && !key.contains("kits.") && !key.contains("globalexit")) {
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
        return config;
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
