package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.hg.*;
import tk.shanebee.hg.game.Bound;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.managers.KitManager;
import tk.shanebee.hg.tasks.CompassTask;
import tk.shanebee.hg.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * General data handler for the plugin
 */
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
				// For each arena in config
				for (String arena : arenadat.getConfigurationSection("arenas").getKeys(false)) {
					boolean isReady = true;
					List<Location> spawns = new ArrayList<>();
					Sign lobbysign = null;
					int timer = 0;
					int cost = 0;
					int minplayers = 0;
					int maxplayers = 0;
					Bound bound = null;
					Bound carePackageBound = null;
					List<String> commands;

					try {
						timer = arenadat.getInt("arenas." + arena + ".info." + "timer");
						minplayers = arenadat.getInt("arenas." + arena + ".info." + "min-players");
						maxplayers = arenadat.getInt("arenas." + arena + ".info." + "max-players");
					} catch (Exception e) {
						Util.warning("Unable to load information for arena " + arena + "!");
						isReady = false;
					}
					try {
						cost = arenadat.getInt("arenas." + arena + ".info." + "cost");
					} catch (Exception ignore) {
					}

					try {
						lobbysign = (Sign) getSLoc(arenadat.getString("arenas." + arena + "." + "lobbysign")).getBlock().getState();
					} catch (Exception e) { 
						Util.warning("Unable to load lobby sign for arena " + arena + "!");
						isReady = false;
					}

					try {
						for (String l : arenadat.getStringList("arenas." + arena + "." + "spawns")) {
							spawns.add(getLocFromString(l));
						}
					} catch (Exception e) { 
						Util.warning("Unable to load random spawns for arena " + arena + "!"); 
						isReady = false;
					}

					try {
						bound = new Bound(arenadat.getString("arenas." + arena + ".bound." + "world"), BC(arena, "x"), BC(arena, "y"), BC(arena, "z"), BC(arena, "x2"), BC(arena, "y2"), BC(arena, "z2"));
					} catch (Exception e) { 
						Util.warning("Unable to load region bounds for arena " + arena + "!"); 
						isReady = false;
					}
					
					try {
						carePackageBound = new Bound(
								arenadat.getString("arenas." + arena + ".bound." + "world"),
								CBC(arena, "x"), 
								CBC(arena, "y"), 
								CBC(arena, "z"), 
								CBC(arena, "x2"), 
								CBC(arena, "y2"), 
								CBC(arena, "z2"));
						
					} catch (Exception e) { 
						Util.warning("Unable to load care package region bounds for arena " + arena + "!"); 
						isReady = false;
					}

					Game game = new Game(
							arena,
							bound, 
							spawns, 
							lobbysign, 
							timer, 
							minplayers, 
							maxplayers, 
							freeroam, 
							isReady, 
							cost, 
							carePackageBound);
					
					plugin.getGames().add(game);

					KitManager kit = plugin.getItemStackManager().setGameKits(arena, arenadat);
					if (kit != null)
						game.setKitManager(kit);

					if (!arenadat.getStringList("arenas." + arena + ".items").isEmpty()) {
						HashMap<Integer, ItemStack> items = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + arena + ".items")) {
							HG.getPlugin().getRandomItems().loadItems(itemString, items);
						}
						game.setItems(items);
						Util.log(items.size() + " Random items have been loaded for arena: " + arena);
					}
					if (!arenadat.getStringList("arenas." + arena + ".bonus").isEmpty()) {
						HashMap<Integer, ItemStack> bonusItems = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + arena + ".bonus")) {
							HG.getPlugin().getRandomItems().loadItems(itemString, bonusItems);
						}
						game.setBonusItems(bonusItems);
						Util.log(bonusItems.size() + " Random bonus items have been loaded for arena: " + arena);
					}

					if (arenadat.isSet("arenas." + arena + ".border.center")) {
						Location borderCenter = getSLoc(arenadat.getString("arenas." + arena + ".border.center"));
						game.setBorderCenter(borderCenter);
					}
					if (arenadat.isSet("arenas." + arena + ".border.size")) {
						int borderSize = arenadat.getInt("arenas." + arena + ".border.size");
						game.setBorderSize(borderSize);
					}
					if (arenadat.isSet("arenas." + arena + ".border.countdown-start") &&
							arenadat.isSet("arenas." + arena + ".border.countdown-end")) {
						int borderCountdownStart = arenadat.getInt("arenas." + arena + ".border.countdown-start");
						int borderCountdownEnd = arenadat.getInt("arenas." + arena + ".border.countdown-end");
						game.setBorderTimer(borderCountdownStart, borderCountdownEnd);
					}
					if (arenadat.isList("arenas." + arena + ".commands")) {
						commands = arenadat.getStringList("arenas." + arena + ".commands");
					} else {
						arenadat.set("arenas." + arena + ".commands", Collections.singletonList("none"));
						saveCustomConfig();
						commands = Collections.singletonList("none");
					}
					game.setCommands(commands);
					if (arenadat.isSet("arenas." + arena + ".chest-refill")) {
						int chestRefill = arenadat.getInt("arenas." + arena + ".chest-refill");
						game.setChestRefillTime(chestRefill);
					}

				}
			} else {
				Util.log("No Arenas to load.");
			}
		}
	}
	
	/**
	 * Bound config
	 * @param s
	 * @param st
	 * @return
	 */
	public int BC(String s, String st) {
		return arenadat.getInt("arenas." + s + ".bound." + st);
	}
	
	/**
	 * Chest bound config
	 * @param arena
	 * @param coord
	 * @return
	 */
	public int CBC(String arena, String coord) {
		return arenadat.getInt("arenas." + arena + ".care-package-bound." + coord);
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
