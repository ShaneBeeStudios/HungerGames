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
					int chestRefill = 0;
					Bound b = null;
					Location borderCenter = null;
					int borderSize = 0;
					int borderCountdownStart = 0;
					int borderCountdownEnd = 0;
					List<String> commands;
					KitManager kit;

					HashMap<Integer, ItemStack> items = null;
					HashMap<Integer, ItemStack> bonusItems = null;

					try {
						timer = arenadat.getInt("arenas." + s + ".info." + "timer");
						minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
						maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
						if (arenadat.isSet("arenas." + s + ".chest-refill")) {
							chestRefill = arenadat.getInt("arenas." + s + ".chest-refill");
						}
						if (arenadat.isSet("arenas." + s + ".border.center")) {
							borderCenter = getSLoc(arenadat.getString("arenas." + s + ".border.center"));
						}
						if (arenadat.isSet("arenas." + s + ".border.size")) {
							borderSize = arenadat.getInt("arenas." + s + ".border.size");
						} else {
							borderSize = Config.borderFinalSize;
						}
						if (arenadat.isSet("arenas." + s + ".border.countdown-start") &&
								arenadat.isSet("arenas." + s + ".border.countdown-end")) {
							borderCountdownStart = arenadat.getInt("arenas." + s + ".border.countdown-start");
							borderCountdownEnd = arenadat.getInt("arenas." + s + ".border.countdown-end");
						} else {
							borderCountdownStart = Config.borderCountdownStart;
							borderCountdownEnd = Config.borderCountdownEnd;
						}
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

					if (arenadat.isList("arenas." + s + ".commands")) {
						commands = arenadat.getStringList("arenas." + s + ".commands");
					} else {
						arenadat.set("arenas." + s + ".commands", Collections.singletonList("none"));
						saveCustomConfig();
						commands = Collections.singletonList("none");
					}
					try {
						kit = plugin.getItemStackManager().setGameKits(s, arenadat);
					} catch (Exception e) {
						kit = null;
						e.printStackTrace();
					}
					if (!arenadat.getStringList("arenas." + s + ".items").isEmpty()) {
						items = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + s + ".items")) {
							HG.plugin.getRandomItems().loadItems(itemString, items);
						}
						Util.log(items.size() + " Random items have been loaded for arena: " + s);
					}
					if (!arenadat.getStringList("arenas." + s + ".bonus").isEmpty()) {
						bonusItems = new HashMap<>();
						for (String itemString : arenadat.getStringList("arenas." + s + ".bonus")) {
							HG.plugin.getRandomItems().loadItems(itemString, bonusItems);
						}
						Util.log(bonusItems.size() + " Random bonus items have been loaded for arena: " + s);
					}

					plugin.games.add(new Game(s, b, spawns, lobbysign, timer, minplayers, maxplayers, freeroam, chestRefill,
							isReady, borderCenter, borderSize, borderCountdownStart, borderCountdownEnd, commands, kit, items, bonusItems));
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
