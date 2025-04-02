package com.shanebeestudios.hg.data;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.GameBlockData.ChestType;
import com.shanebeestudios.hg.managers.ItemStackManager;
import org.bukkit.configuration.ConfigurationSection;
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
    private final Map<ChestType, Map<Integer, ItemStack>> chestItems = new HashMap<>();

    public RandomItems(HungerGames plugin) {
        this.plugin = plugin;
        this.itemStackManager = plugin.getItemStackManager();
        loadItemsConfig();
        Util.log("Loading random items:");
        for (ChestType value : ChestType.values()) {
            this.chestItems.put(value, new HashMap<>());
        }
        loadItems();
    }

    private void loadItemsConfig() {
        if (this.itemsConfigFile == null) {
            this.itemsConfigFile = new File(this.plugin.getDataFolder(), "items.yml");
        }
        if (!this.itemsConfigFile.exists()) {
            this.plugin.saveResource("items.yml", false);
            Util.log("- New items.yml file has been <green>successfully generated!");
        }
        this.itemsConfig = YamlConfiguration.loadConfiguration(this.itemsConfigFile);
    }

    public void loadItems() {
        ConfigurationSection itemsSection = this.itemsConfig.getConfigurationSection("items");
        assert itemsSection != null;
        for (ChestType chestType : ChestType.values()) {
            if (chestType == ChestType.PLAYER_PLACED) continue;

            String chestTypeName = chestType.getName();
            if (itemsSection.isSet(chestTypeName)) {
                Map<Integer, ItemStack> itemMap = this.chestItems.get(chestType);
                this.itemStackManager.loadItems(itemsSection.getMapList(chestTypeName), itemMap);
                Util.log("- Loaded <green>%s <grey>%s items!", itemMap.size(), chestTypeName);
            }
        }
    }

    public Map<Integer, ItemStack> getItems(ChestType type) {
        return this.chestItems.get(type);
    }

}
