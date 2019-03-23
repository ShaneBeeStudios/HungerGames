package me.minebuilders.hg.data;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Language {

    private FileConfiguration lang = null;
    private File customLangFile = null;
    private final HG plugin;

    public String prefix;

    public Language(HG plugin) {
        this.plugin = plugin;
        loadLangFile();
    }

    private void loadLangFile() {
        if (customLangFile == null) {
            customLangFile = new File(plugin.getDataFolder(), "language.yml");
        }
        if (!customLangFile.exists()) {
            plugin.saveResource("language.yml", false);
            lang = YamlConfiguration.loadConfiguration(customLangFile);
            loadLang();
            Util.log("&7New language.yml created");
        } else {
            lang = YamlConfiguration.loadConfiguration(customLangFile);
            loadLang();
        }
        Util.log("&7language.yml loaded");
    }

    private void loadLang() {
        prefix = lang.getString("prefix");
    }

}
