package tk.shanebee.hg.data;

import org.bukkit.configuration.Configuration;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

import java.io.File;
import java.util.List;

/**
 * Main config class <b>Internal Use Only</b>
 */
public class Config {

	//Basic settings
	public static boolean economy = true;
	public static boolean spawnmobs;
	public static int spawnmobsinterval;
	public static boolean bossbar;
	public static int trackingstickuses;
	public static int playersfortrackingstick;
	public static int maxchestcontent;
	public static int minchestcontent;
	public static int maxbonuscontent;
	public static int minbonuscontent;
	public static int maxTeam;
	public static boolean teleportEnd;
	public static int teleportEndTime;

	//Timers
    public static int countdownTimer;
    public static int startingTimer;

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
	public static List<String> blocks;

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

	//mcMMO
	public static boolean mcmmoUseSkills;
	public static boolean mcmmoGainExp;

	private final HG plugin;

	public Config(HG plugin) {
		this.plugin = plugin;
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			Util.log("Config not found. Generating default config!");
			plugin.saveDefaultConfig();
		}
		Configuration config = plugin.getConfig().getRoot();
		assert config != null;
		config.options().copyDefaults(true);
		plugin.reloadConfig();
		config = plugin.getConfig();
		updateConfig(config);
		Util.log("Config loaded!");

		spawnmobs = config.getBoolean("settings.spawn-mobs");
		spawnmobsinterval = config.getInt("settings.spawn-mobs-interval") * 20;
		bossbar = config.getBoolean("settings.bossbar-countdown");
		trackingstickuses = config.getInt("settings.trackingstick-uses");
		playersfortrackingstick = config.getInt("settings.players-for-trackingstick");
		maxchestcontent = config.getInt("settings.max-chestcontent");
		minchestcontent = config.getInt("settings.min-chestcontent");
		maxbonuscontent = config.getInt("settings.max-bonus-chestcontent");
		minbonuscontent = config.getInt("settings.min-bonus-chestcontent");
		maxTeam = config.getInt("settings.max-team-size");
		giveReward = config.getBoolean("reward.enabled");
		cash = config.getInt("reward.cash");
		rewardCommands = config.getStringList("reward.commands");
		rewardMessages = config.getStringList("reward.messages");
		maxTeam = config.getInt("settings.max-team-size");
		giveReward = config.getBoolean("reward.enabled");
		cash = config.getInt("reward.cash");
		breakblocks = config.getBoolean("rollback.allow-block-break");
		blocks_per_second = config.getInt("rollback.blocks-per-second");
		protectCooldown = config.getBoolean("rollback.protect-during-cooldown");
		fixleaves = config.getBoolean("rollback.fix-leaves");
		preventtrample = config.getBoolean("rollback.prevent-trampling");
		blocks = config.getStringList("rollback.editable-blocks");
		randomChest = config.getBoolean("random-chest.enabled");
		randomChestInterval = config.getInt("random-chest.interval") * 20;
		randomChestMaxContent = config.getInt("random-chest.max-chestcontent");
		teleportEnd = config.getBoolean("settings.teleport-at-end");
		teleportEndTime = config.getInt("settings.teleport-at-end-time");

		countdownTimer = config.getInt("settings.countdown-timer");
		startingTimer = config.getInt("settings.starting-timer");

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

		mcmmoUseSkills = config.getBoolean("mcmmo.use-skills");
		mcmmoGainExp = config.getBoolean("mcmmo.gain-experience");

        try {
            Vault.setupEconomy();
            if (Vault.economy == null) {
                Util.log("&cUnable to setup vault!");
                Util.log(" - &cEconomy provider is missing.");
                Util.log(" - Cash rewards will not be given out..");
                giveReward = false;
                economy = false;
            }
        } catch (NoClassDefFoundError e) {
            Util.log("&cUnable to setup vault!");
            Util.log("  - Cash rewards will not be given out..");
            giveReward = false;
            economy = false;
        }
	}

	private void updateConfig(Configuration config) {
		if (!config.isSet("spectate.enabled")) {
			config.set("spectate.enabled", false);
			config.set("spectate.death-to-spectate", true);
			config.set("spectate.hide-spectators", true);
			config.set("spectate.fly", true);
		}
		if (!config.isSet("rollback.blocks-per-second")) {
			config.set("rollback.blocks-per-second", 500);
		}
		if (!config.isSet("spectate.chat")) {
			config.set("spectate.chat", false);
			config.set("mcmmo.use-skills", false);
			config.set("mcmmo.gain-experience", false);
		}
		plugin.saveConfig();
	}

}
