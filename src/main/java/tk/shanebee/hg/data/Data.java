package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.hg.*;
import tk.shanebee.hg.managers.KitManager;
import tk.shanebee.hg.tasks.CompassTask;
import tk.shanebee.hg.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Data {

	private FileConfiguration arenadat = null;
	private File customConfigFile = null;
	private final HG plugin;

	public Data(HG plugin) {
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
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create arena.yml!");
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
		int freeroam = plugin.getConfig().getInt("settings.free-roam");

		if (customConfigFile.exists()) {

			new CompassTask(plugin);

			boolean hasData = arenadat.getConfigurationSection("arenas") != null;
			
			if (hasData) {
				for (String s : arenadat.getConfigurationSection("arenas").getKeys(false)) {
					boolean isReady = true;
					List<Location> spawns = new ArrayList<>();
					Sign lobbysign = null;
					int timer = 0;
					int minplayers = 0;
					int maxplayers = 0;
					Bound b = null;
					List<String> commands;

					try {
						timer = arenadat.getInt("arenas." + s + ".info." + "timer");
						minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
						maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
					} catch (Exception e) {
						Util.warning("Unable to load information for arena " + s + "!");
						isReady = false;
					}

					try {
						lobbysign = (Sign) getSLoc(arenadat.getString("arenas." + s + "." + "lobbysign")).getBlock().getState();
					} catch (Exception e) { 
						Util.warning("Unable to load lobby sign for arena " + s + "!");
						isReady = false;
					}

					try {
						for (String l : arenadat.getStringList("arenas." + s + "." + "spawns")) {
							spawns.add(getLocFromString(l));
						}
					} catch (Exception e) { 
						Util.warning("Unable to load random spawns for arena " + s + "!"); 
						isReady = false;
					}

					try {
						b = new Bound(arenadat.getString("arenas." + s + ".bound." + "world"), BC(s, "x"), BC(s, "y"), BC(s, "z"), BC(s, "x2"), BC(s, "y2"), BC(s, "z2"));
					} catch (Exception e) { 
						Util.warning("Unable to load region bounds for arena " + s + "!"); 
						isReady = false;
					}

					Game game = new Game(s, b, spawns, lobbysign, timer, minplayers, maxplayers, freeroam, isReady);
					plugin.getGames().add(game);

					KitManager kit = plugin.getItemStackManager().setGameKits(s, arenadat);
					if (kit != null)
						game.setKitManager(kit);

					if (!arenadat.getStringList("arenas." + s + ".items").isEmpty()) {
						HashMap<Integer, ItemStack> items = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + s + ".items")) {
							HG.plugin.getRandomItems().loadItems(itemString, items);
						}
						game.setItems(items);
						Util.log(items.size() + " Random items have been loaded for arena: " + s);
					}
					if (!arenadat.getStringList("arenas." + s + ".bonus").isEmpty()) {
						HashMap<Integer, ItemStack> bonusItems = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + s + ".bonus")) {
							HG.plugin.getRandomItems().loadItems(itemString, bonusItems);
						}
						game.setBonusItems(bonusItems);
						Util.log(bonusItems.size() + " Random bonus items have been loaded for arena: " + s);
					}

					if (arenadat.isSet("arenas." + s + ".border.center")) {
						Location borderCenter = getSLoc(arenadat.getString("arenas." + s + ".border.center"));
						game.setBorderCenter(borderCenter);
					}
					if (arenadat.isSet("arenas." + s + ".border.size")) {
						int borderSize = arenadat.getInt("arenas." + s + ".border.size");
						game.setBorderSize(borderSize);
					}
					if (arenadat.isSet("arenas." + s + ".border.countdown-start") &&
							arenadat.isSet("arenas." + s + ".border.countdown-end")) {
						int borderCountdownStart = arenadat.getInt("arenas." + s + ".border.countdown-start");
						int borderCountdownEnd = arenadat.getInt("arenas." + s + ".border.countdown-end");
						game.setBorderTimer(borderCountdownStart, borderCountdownEnd);
					}
					if (arenadat.isList("arenas." + s + ".commands")) {
						commands = arenadat.getStringList("arenas." + s + ".commands");
					} else {
						arenadat.set("arenas." + s + ".commands", Collections.singletonList("none"));
						saveCustomConfig();
						commands = Collections.singletonList("none");
					}
					game.setCommands(commands);
					if (arenadat.isSet("arenas." + s + ".chest-refill")) {
						int chestRefill = arenadat.getInt("arenas." + s + ".chest-refill");
						game.setChestRefillTime(chestRefill);
					}

				}
			} else {
				Util.log("No Arenas to load.");
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
