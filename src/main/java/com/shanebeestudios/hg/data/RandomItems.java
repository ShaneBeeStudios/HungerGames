package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.managers.ItemStackManager;
import com.shanebeestudios.hg.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for random items
 */
public class RandomItems {

    private FileConfiguration itemsConfig = null;
    private File itemsConfigFile = null;
    private final HungerGames plugin;
    private final ItemStackManager itemStackManager;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, ItemStack> bonusItems = new HashMap<>();

    public RandomItems(HungerGames plugin) {
        this.plugin = plugin;
        this.itemStackManager = plugin.getItemStackManager();
        loadItemsConfig();
        Util.logMini("Loading random items:");
        loadItems();
    }

    private void loadItemsConfig() {
        if (this.itemsConfigFile == null) {
            this.itemsConfigFile = new File(this.plugin.getDataFolder(), "items.yml");
        }
        if (!itemsConfigFile.exists()) {
            this.plugin.saveResource("items.yml", false);
            Util.logMini("- New items.yml file has been <green>successfully generated!");
        }
        this.itemsConfig = YamlConfiguration.loadConfiguration(this.itemsConfigFile);
    }

    public void loadItems() {
        // Regular items
        this.itemStackManager.loadItems(this.itemsConfig.getMapList("items"), this.items);

        // Bonus items
        this.itemStackManager.loadItems(this.itemsConfig.getMapList("bonus"), this.bonusItems);

        Util.logMini("- Loaded <green>%s <grey>random items!", this.items.size());
        Util.logMini("- Loaded <green>%s <grey>bonus items!", this.bonusItems.size());
    }

    public Map<Integer, ItemStack> getItems() {
        return this.items;
    }

    public Map<Integer, ItemStack> getBonusItems() {
        return this.bonusItems;
    }

}
