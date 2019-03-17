package me.minebuilders.hg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class Config {

	//Basic settings
	public static boolean spawnmobs;
	public static int spawnmobsinterval;
	public static int freeroam;
	public static int trackingstickuses;
	public static int playersfortrackingstick;
	public static int maxchestcontent;
	public static boolean teleAtEnd;
	public static int maxTeam;

	//Reward info
	public static boolean giveReward;
	public static int cash;

	//Rollback config info
	public static boolean breakblocks;
	public static List<Integer> blocks;

	//Random chest
	public static boolean randomChest;
	public static int randomChestInterval;
	public static int randomChestMaxContent;

	//Misc items
	public static ItemStack firework;

	private static Configuration config;

	public Config(HG plugin) {
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			Util.log("Config not found. Generating default config!");
			plugin.saveDefaultConfig();
		}
		
		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		plugin.reloadConfig();
		config = plugin.getConfig();

		spawnmobs = config.getBoolean("settings.spawn-mobs");
		spawnmobsinterval = config.getInt("settings.spawn-mobs-interval") * 20;
		freeroam = config.getInt("settings.free-roam");
		trackingstickuses = config.getInt("settings.trackingstick-uses");
		playersfortrackingstick = config.getInt("settings.players-for-trackingstick");
		maxchestcontent = config.getInt("settings.max-chestcontent");
		teleAtEnd = config.getBoolean("settings.teleport-at-end");
		maxTeam = config.getInt("settings.max-team-size");
		giveReward = config.getBoolean("reward.enabled");
		cash = config.getInt("reward.cash");
		maxTeam = config.getInt("settings.max-team-size");
		giveReward = config.getBoolean("reward.enabled");
		cash = config.getInt("reward.cash");
		breakblocks = config.getBoolean("rollback.allow-block-break");
		blocks = config.getIntegerList("rollback.editable-blocks");
		randomChest = config.getBoolean("random-chest.enabled");
		randomChestInterval = config.getInt("random-chest.interval") * 20;
		randomChestMaxContent = config.getInt("random-chest.max-chestcontent");

		if (giveReward) {
			try {
				Vault.setupEconomy();
			} catch (NoClassDefFoundError e) {
				Util.log("Unable to setup vault! Rewards will not be given out..");
				giveReward = false;
			}
		}
		//Firework setup info
		ItemStack i = new ItemStack(Material.FIREWORK_ROCKET, 64);
		FireworkMeta fm = (FireworkMeta) i.getItemMeta();
		List<Color> c = new ArrayList<Color>();
		c.add(Color.ORANGE);
		c.add(Color.RED);
		FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
		fm.addEffect(e);
		fm.setPower(3);
		i.setItemMeta(fm);

		firework = i;
	}
}
