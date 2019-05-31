package tk.shanebee.hg.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RandomItems {

	private FileConfiguration item = null;
	private File customConfigFile = null;
	public int size = 0;
	private final HG plugin;

	public RandomItems(HG plugin) {
		this.plugin = plugin;
		reloadCustomConfig();
		load();
	}

	private void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "items.yml");
		}
		if (!customConfigFile.exists()) {
			try {
				customConfigFile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create items.yml!");
			}
			item = YamlConfiguration.loadConfiguration(customConfigFile);
			saveCustomConfig();
			Util.log("New items.yml file has been successfully generated!");
		} else {
			item = YamlConfiguration.loadConfiguration(customConfigFile);
		}
	}

	private FileConfiguration getCustomConfig() {
		if (item == null) {
			this.reloadCustomConfig();
		}
		return item;
	}

	private void saveCustomConfig() {
		if (item == null || customConfigFile == null) {
			return;
		}
		try {
			getCustomConfig().save(customConfigFile);
		} catch (IOException ex) {
			Util.log("Could not save config to " + customConfigFile);
		}
	}

	public void load() {
		reloadCustomConfig();
		size = 0;
		if (item.getStringList("items").isEmpty()) {
			setDefaultss();
			saveCustomConfig();
			reloadCustomConfig();
			Util.log("generating defaults for random items!");
		}
		for (String s : item.getStringList("items")) {
			String[] amount = s.split(" ");
			if (s.contains("x:")) {
				for (String p : amount) {
					if (p.startsWith("x:")) {
						int c = Integer.parseInt(p.replace("x:", ""));
						while (c != 0) {
							c--;
							plugin.items.put(plugin.items.size() + 1, plugin.itemStackManager.getItem(s.replace("x:", ""), true));
							size++;
						}
					}
				}
			} else {
				plugin.items.put(plugin.items.size() + 1, plugin.itemStackManager.getItem(s, true));
			}
			size++;
		}
		Util.log(plugin.items.size() + " Random items have been loaded!");
	}

	private void setDefaultss() {
		ArrayList <String> items = new ArrayList <>();
		items.add("STONE_SWORD 1 x:5");
		items.add("GOLDEN_SWORD 1");
		items.add("MUSHROOM_STEW 1 x:2");
		items.add("STONE_HOE 1");
		items.add("LEATHER_HELMET 1 x:2");
		items.add("LEATHER_CHESTPLATE 1 x:2");
		items.add("LEATHER_LEGGINGS 1 x:2");
		items.add("IRON_HELMET 1 x:2");
		items.add("IRON_CHESTPLATE 1 x:2");
		items.add("IRON_LEGGINGS 1 x:2");
		items.add("IRON_BOOTS 1 x:2");//280
		items.add("BOW 1 x:3");
		items.add("ARROW 20 x:2");
		items.add("MILK_BUCKET 1 x:2");
		items.add("FISHING_ROD 1");
		items.add("COMPASS 1");
		items.add("STICK 1 name:&6TrackingStick_&aUses:_5 lore:&Left_click_in_the_air:&7To_find_nearby_players");
		items.add("GOLDEN_HELMET 1");
		items.add("GOLDEN_CHESTPLATE 1");
		items.add("BONE 1 x:2");
		items.add("GOLDEN_LEGGINGS 1");
		items.add("GOLDEN_BOOTS 1");
		items.add("DIAMOND_SWORD 1 name:&6Death_Dealer");
		items.add("GOLDEN_APPLE 1");
		items.add("CHAINMAIL_CHESTPLATE 1 x:1");
		items.add("CHAINMAIL_LEGGINGS 1 x:1");
		items.add("COOKIE 2 x:3");
		items.add("MELON_SLICE 1 x:4");
		items.add("COOKED_BEEF 1 x:2");
		items.add("ENDER_PEARL 1 x:2");
		items.add("POTION:SPEED:3600:1 1 x:2");
		items.add("POTION:HEAL:1:1 1 x:2");
		items.add("SPLASH_POTION:POISON:320:2 1");
		items.add("SPLASH_POTION:REGENERATION:660:1 1 x:2");
		items.add("APPLE 2 x:5");
		item.set("items", items);
	}
}