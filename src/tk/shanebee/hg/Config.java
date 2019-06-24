package tk.shanebee.hg;

import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.List;

public class Config {

	//Basic settings
	static boolean spawnmobs;
	static int spawnmobsinterval;
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

	//Reward info
	static boolean giveReward;
	static int cash;
	static List<String> rewardCommands;
	static List<String> rewardMessages;

	//Rollback config info
	public static boolean breakblocks;
	public static boolean protectCooldown;
	public static boolean fixleaves;
	public static boolean preventtrample;
	public static List<String> blocks;

	//Random chest
	static boolean randomChest;
	public static int randomChestInterval;
	static int randomChestMaxContent;

	//World border
	public static boolean borderEnabled;
	public static boolean borderOnStart;
	static boolean centerSpawn;
	public static int borderCountdownStart;
	public static int borderCountdownEnd;
	public static int borderFinalSize;

	//Spectate
	public static boolean spectateEnabled;
	public static boolean spectateOnDeath;

	private HG plugin;

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
		protectCooldown = config.getBoolean("rollback.protect-during-cooldown");
		fixleaves = config.getBoolean("rollback.fix-leaves");
		preventtrample = config.getBoolean("rollback.prevent-trampling");
		blocks = config.getStringList("rollback.editable-blocks");
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

		if (giveReward) {
			try {
				Vault.setupEconomy();
				if (Vault.economy == null) {
					Util.log("&cUnable to setup vault!");
					Util.log(" - &cEconomy provider is missing.");
					Util.log(" - Cash rewards will not be given out..");
					giveReward = false;
				}
			} catch (NoClassDefFoundError e) {
				Util.log("&cUnable to setup vault!");
				Util.log("  - Cash rewards will not be given out..");
				giveReward = false;
			}
		}
	}

	private void updateConfig(Configuration config) {
		if (!config.isSet("settings.bossbar-countdown")) {
			config.set("settings.bossbar-countdown", true);
		}
		if (!config.isSet("world-border.enabled")) {
			config.set("world-border.enabled", false);
			config.set("world-border.initiate-on-start", true);
			config.set("world-border.countdown-start", 60);
			config.set("world-border.countdown-end", 30);
			config.set("world-border.final-border-size", 30);
			config.set("world-border.center-on-first-spawn", true);
			config.set("settings.min-chestcontent", 1);
		}
		if (!config.isSet("settings.max-bonus-chestcontent")) {
			config.set("settings.max-bonus-chestcontent", 5);
			config.set("settings.min-bonus-chestcontent", 1);
		}
		if (!config.isSet("rollback.protect-during-cooldown")) {
			config.set("rollback.protect-during-cooldown", true);
		}
		if (!config.isSet("spectate.enabled")) {
			config.set("spectate.enabled", false);
			config.set("spectate.death-to-spectate", true);
		}
		plugin.saveConfig();
	}

}
