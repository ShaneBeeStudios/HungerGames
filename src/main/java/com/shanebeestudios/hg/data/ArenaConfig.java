package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.game.Bound;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.managers.KitManager;
import com.shanebeestudios.hg.tasks.CompassTask;
import com.shanebeestudios.hg.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * General data handler for the plugin
 */
public class ArenaConfig {

	private FileConfiguration arenadat = null;
	private File customConfigFile = null;
	private final HG plugin;

	public ArenaConfig(HG plugin) {
		this.plugin = plugin;
		reloadCustomConfig();
		load();
	}

	/** Get arena data file
	 * @return Arena data file
	 */
	public FileConfiguration getConfig() {
		return arenadat;
	}

	public void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "arenas.yml");
		}
		if (!customConfigFile.exists()) {
			try {
				//noinspection ResultOfMethodCallIgnored
				customConfigFile.createNewFile();
			}
			catch (IOException e) {
				Util.warning("&cCould not create arena.yml!");
				Util.debug(e);
			}
			arenadat = YamlConfiguration.loadConfiguration(customConfigFile);
			saveCustomConfig();
			Util.log("New arenas.yml file has been successfully generated!");
		} else {
			arenadat = YamlConfiguration.loadConfiguration(customConfigFile);
		}
	}

	public FileConfiguration getCustomConfig() {
		if (arenadat == null) {
			this.reloadCustomConfig();
		}
		return arenadat;
	}

	public void saveCustomConfig() {
		if (arenadat == null || customConfigFile == null) {
			return;
		}
		try {
			getCustomConfig().save(customConfigFile);
		} catch (IOException ex) {
			Util.log("Could not save config to " + customConfigFile);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void load() {
		Util.log("Loading arenas...");
		Configuration pluginConfig = plugin.getHGConfig().getConfig();
		int freeroam = pluginConfig.getInt("settings.free-roam");

		if (customConfigFile.exists()) {
			// TODO remove after a while (aug 30/2020)
			// Move global exit from config.yml to arenas.yml
			if (pluginConfig.isSet("settings.globalexit")) {
				String globalExit = pluginConfig.getString("settings.globalexit");
				pluginConfig.set("settings.globalexit", null);
				plugin.getHGConfig().save();
				arenadat.set("global-exit-location", globalExit);
				saveCustomConfig();
			}

			new CompassTask(plugin);

			ConfigurationSection section = arenadat.getConfigurationSection("arenas");

			if (section != null) {
				for (String arenaName : section.getKeys(false)) {
					boolean isReady = true;
					List<Location> spawns = new ArrayList<>();
					Sign lobbysign = null;
					int timer = 0;
					int cost = 0;
					int minplayers = 0;
					int maxplayers = 0;
					Bound bound = null;
					List<String> commands;

					String path = "arenas." + arenaName;
					try {
						timer = arenadat.getInt(path + ".info.timer");
						minplayers = arenadat.getInt(path + ".info.min-players");
						maxplayers = arenadat.getInt(path + ".info.max-players");
					} catch (Exception e) {
						Util.warning("Unable to load information for arena '" + arenaName + "'!");
						isReady = false;
					}
					try {
						cost = arenadat.getInt(path + ".info.cost");
					} catch (Exception ignore) {
					}

					try {
						lobbysign = (Sign) getSLoc(arenadat.getString(path + ".lobbysign")).getBlock().getState();
					} catch (Exception e) {
						Util.warning("Unable to load lobby sign for arena '" + arenaName + "'!");
						Util.debug(e);
						isReady = false;
					}

					try {
						for (String l : arenadat.getStringList(path + ".spawns")) {
							spawns.add(getLocFromString(l));
						}
					} catch (Exception e) {
						Util.warning("Unable to load random spawns for arena '" + arenaName + "'!");
						isReady = false;
					}

					try {
						bound = new Bound(arenadat.getString(path + ".bound.world"), BC(arenaName, "x"), BC(arenaName, "y"), BC(arenaName, "z"), BC(arenaName, "x2"), BC(arenaName, "y2"), BC(arenaName, "z2"));
					} catch (Exception e) {
						Util.warning("Unable to load region bounds for arena " + arenaName + "!");
						isReady = false;
					}

					Game game = new Game(arenaName, bound, spawns, lobbysign, timer, minplayers, maxplayers, freeroam, isReady, cost);
					plugin.getGames().add(game);

					World world = bound.getWorld();
					if (world.getDifficulty() == Difficulty.PEACEFUL) {
						Util.warning("Difficulty in world '%s' for arena '%s' is set to PEACEFUL...", world.getName(), arenaName);
						Util.warning("This can have negative effects on the game, please consider raising the difficulty.");
					}

					KitManager kit = plugin.getItemStackManager().setGameKits(arenaName, arenadat);
					if (kit != null)
						game.setKitManager(kit);

					if (!arenadat.getStringList(path + ".items").isEmpty()) {
						HashMap<Integer, ItemStack> items = new HashMap<>();
						for (String itemString : arenadat.getStringList(path + ".items")) {
							plugin.getRandomItems().loadItems(itemString, items);
						}
						game.getGameItemData().setItems(items);
						Util.log(items.size() + " Random items have been loaded for arena: &b" + arenaName);
					}
					if (!arenadat.getStringList(path + ".bonus").isEmpty()) {
						HashMap<Integer, ItemStack> bonusItems = new HashMap<>();
						for (String itemString : arenadat.getStringList(path + ".bonus")) {
							plugin.getRandomItems().loadItems(itemString, bonusItems);
						}
						game.getGameItemData().setBonusItems(bonusItems);
						Util.log(bonusItems.size() + " Random bonus items have been loaded for arena: &b" + arenaName);
					}

					if (arenadat.isSet(path + ".border.center")) {
						Location borderCenter = getSLoc(arenadat.getString(path + ".border.center"));
						game.getGameBorderData().setBorderCenter(borderCenter);
					}
					if (arenadat.isSet(path + ".border.size")) {
						int borderSize = arenadat.getInt(path + ".border.size");
						game.getGameBorderData().setBorderSize(borderSize);
					}
					if (arenadat.isSet(path + ".border.countdown-start") && arenadat.isSet(path + ".border.countdown-end")) {
						int borderCountdownStart = arenadat.getInt(path + ".border.countdown-start");
						int borderCountdownEnd = arenadat.getInt(path + ".border.countdown-end");
						game.getGameBorderData().setBorderTimer(borderCountdownStart, borderCountdownEnd);
					}
					if (arenadat.isList(path + ".commands")) {
						commands = arenadat.getStringList(path + ".commands");
					} else {
						arenadat.set(path + ".commands", Collections.singletonList("none"));
						saveCustomConfig();
						commands = Collections.singletonList("none");
					}
					game.getGameCommandData().setCommands(commands);
					GameArenaData gameArenaData = game.getGameArenaData();
					if (arenadat.isSet(path + ".chest-refill")) {
						int chestRefill = arenadat.getInt(path + ".chest-refill");
						gameArenaData.setChestRefillTime(chestRefill);
					}
					if (arenadat.isSet(path + ".chest-refill-repeat")) {
						int chestRefillRepeat = arenadat.getInt(path + ".chest-refill-repeat");
						gameArenaData.setChestRefillRepeat(chestRefillRepeat);
					}
					try {
						Location location;
						if (arenadat.isSet(path + ".exit-location")) {
							location = getLocFromString(arenadat.getString(path + ".exit-location"));
						} else if (arenadat.isSet("global-exit-location")) {
							location = getLocFromString(arenadat.getString("global-exit-location"));
						} else {
							location = game.getLobbyLocation().getWorld().getSpawnLocation();
						}
						gameArenaData.setExit(location);
					} catch (Exception exception) {
					    World mainWorld = Bukkit.getWorlds().get(0);
						gameArenaData.setExit(mainWorld.getSpawnLocation());
						Util.warning("Failed to setup exit location for arena '%s', defaulting to spawn location of world '%s'",
                                arenaName, world.getName());
						Util.debug(exception);
					}
					Util.log("Arena &b" + arenaName + "&7 has been &aloaded!");

				}
			} else {
				Util.log("&cNo Arenas to load.");
			}
		}
	}

	public int BC(String s, String st) {
		return arenadat.getInt("arenas." + s + ".bound." + st);
	}

	public Location getLocFromString(String s) {
		String[] h = s.split(":");
		return new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5, Integer.parseInt(h[2]), Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
	}

	public Location getSLoc(String s) {
		String[] h = s.split(":");
		return new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]), Integer.parseInt(h[2]), Integer.parseInt(h[3]));
	}

}
