package tk.shanebee.hg.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tk.shanebee.hg.Bound;
import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;
import tk.shanebee.hg.tasks.CompassTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

	/*
	public void thisIsAUselessMethod() { //THIS STAY BROKE FIX UM
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "arenas.yml");
		}
		arenadat = YamlConfiguration.loadConfiguration(customConfigFile);

		// Look for defaults in the jar
		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("arenas.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			arenadat.setDefaults(defConfig);
		}
	}
	*/

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

					try {
						timer = arenadat.getInt("arenas." + s + ".info." + "timer");
						minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
						maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
					} catch (Exception e) { 
						Util.warning("Unable to load infomation for arena " + s + "!"); 
						isReady = false;
					}

					try {
						lobbysign = (Sign) getSLoc(arenadat.getString("arenas." + s + "." + "lobbysign")).getBlock().getState();
					} catch (Exception e) { 
						Util.warning("Unable to load lobbysign for arena " + s + "!"); 
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
					plugin.games.add(new Game(s, b, spawns, lobbysign, timer, minplayers, maxplayers, freeroam, isReady));
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
