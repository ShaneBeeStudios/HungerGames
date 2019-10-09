package tk.shanebee.hg.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

import java.io.File;

/**
 * Config for custom mobs
 */
public class MobConfig {

	private HG plugin;
	private FileConfiguration mobs = null;
	private File mobFile = null;

	public MobConfig(HG plugin) {
		this.plugin = plugin;
		loadMobFile();
	}

	private void loadMobFile() {
		if (mobFile == null) {
			mobFile = new File(plugin.getDataFolder(), "mobs.yml");
		}
		if (!mobFile.exists()) {
			plugin.saveResource("mobs.yml", false);
			mobs = YamlConfiguration.loadConfiguration(mobFile);
			Util.log("&7New mobs.yml created");
		} else {
			mobs = YamlConfiguration.loadConfiguration(mobFile);
		}
	}

    /** Get the mob config
     * @return Mob config
     */
	public FileConfiguration getMobs() {
		return this.mobs;
	}

}
