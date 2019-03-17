package me.minebuilders.hg.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

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

	public void reloadCustomConfig() {
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

	public FileConfiguration getCustomConfig() {
		if (item == null) {
			this.reloadCustomConfig();
		}
		return item;
	}

	public void saveCustomConfig() {
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
		size = 0;
		if (item.getStringList("items").isEmpty()) {
			setDefaultss();
			saveCustomConfig();
			reloadCustomConfig();
			Util.log("generating defaults for random items!");
		}
		for (String s : item.getStringList("items")) {
			String[] amount = s.split(" ");
			for (String p : amount)
				if (p.startsWith("x:")) {
					int c = Integer.parseInt(p.replace("x:", ""));
					while(c != 0) {
						c--;
						plugin.items.put(plugin.items.size() + 1, plugin.ism.getItem(s.replace("x:", ""), true));
						size++;
					}
				} else
					plugin.items.put(plugin.items.size() + 1, plugin.ism.getItem(s, true));
			size++;
		}
		Util.log(plugin.items.size() + " Random items have been loaded!");
	}

	public void setDefaultss() {
		ArrayList <String> s = new ArrayList <String>();
		s.add("STONE_SWORD 1 x:5");
		s.add("GOLDEN_SWORD 1");
		s.add("MUSHROOM_STEW 1 x:2");
		s.add("STONE_HOE 1");
		s.add("LEATHER_HELMET 1 x:2");
		s.add("LEATHER_CHESTPLATE 1 x:2");
		s.add("LEATHER_LEGGINGS 1 x:2");
		s.add("IRON_HELMET 1 x:2");
		s.add("IRON_CHESTPLATE 1 x:2");
		s.add("IRON_LEGGINGS 1 x:2");
		s.add("IRON_BOOTS 1 x:2");//280
		s.add("BOW 1 x:3");
		s.add("ARROW 20 x:2");
		s.add("MILK_BUCKET 1 x:2");
		s.add("FISHING_ROD 1");
		s.add("COMPASS 1");
		s.add("STICK 1 name:&6TrackingStick_&aUses:_5");
		s.add("GOLDEN_HELMET 1");
		s.add("GOLDEN_CHESTPLATE 1");
		s.add("BONE 1 x:2");
		s.add("GOLDEN_LEGGINGS 1");
		s.add("GOLDEN_BOOTS 1");
		s.add("DIAMOND_SWORD 1 name:&6Death_Dealer");
		s.add("GOLDEN_APPLE 1");
		s.add("CHAINMAIL_CHESTPLATE 1 x:1");
		s.add("CHAINMAIL_LEGGINGS 1 x:1");
		s.add("COOKIE 2 x:3");
		s.add("MELON_SLICE 1 x:4");
		s.add("COOKED_BEEF 1 x:2");
		s.add("ENDER_PEARL 1 x:2");
		/*
		s.add("373:8194 1 x:2");
		s.add("373:8197 1 x:2");
		s.add("373:16420 1");
		s.add("373:16385 1 x:2");
		*/
		s.add("POTION 1 x:2");
		s.add("POTION 1 x:2");
		s.add("POTION 1");
		s.add("POTION 1 x:2");
		s.add("APPLE 2 x:5");
		item.set("items", s);
	}
}