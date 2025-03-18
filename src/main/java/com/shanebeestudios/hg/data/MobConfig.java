package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Config for custom mobs
 */
public class MobConfig {

    private final FileConfiguration mobsConfig;

    public MobConfig(HungerGames plugin) {
        File mobFile = new File(plugin.getDataFolder(), "mobs.yml");
        if (!mobFile.exists()) {
            plugin.saveResource("mobs.yml", false);
            Util.logMini("New mobs.yml created");
        }
        this.mobsConfig = YamlConfiguration.loadConfiguration(mobFile);
    }

    /**
     * Get the mobs config
     *
     * @return Mobs config
     */
    public FileConfiguration getMobsConfig() {
        return this.mobsConfig;
    }

}
