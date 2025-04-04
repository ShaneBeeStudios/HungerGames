package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.ItemData;
import com.shanebeestudios.hg.data.ItemData.ChestType;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final HungerGames plugin;
    private ItemData defaultItemData;

    public ItemManager(HungerGames plugin) {
        this.plugin = plugin;
        loadDefaultItems();
    }

    public ItemData getDefaultItemData() {
        return this.defaultItemData;
    }

    public void loadDefaultItems() {
        Util.log("Loading items:");
        File kitFile = new File(this.plugin.getDataFolder(), "items.yml");

        if (!kitFile.exists()) {
            this.plugin.saveResource("items.yml", false);
            Util.log("- New items.yml file has been <green>successfully generated!");
        }
        YamlConfiguration itemsConfig = YamlConfiguration.loadConfiguration(kitFile);
        ConfigurationSection itemsSection = itemsConfig.getConfigurationSection("items");
        assert itemsSection != null;
        this.defaultItemData = createItemData(itemsSection, null);
        Util.log("- <aqua>%s <grey>items have been <green>successfully loaded!", this.defaultItemData.getTotalItemCount());
    }

    public void loadGameItems(Game game, ConfigurationSection arenaConfig) {
        ConfigurationSection itemsSection = arenaConfig.getConfigurationSection("items");
        if (itemsSection == null) return;

        ItemData itemData = createItemData(itemsSection, game);
        Util.log("- Loaded <aqua>%s <grey>items for arena: <aqua>%s",
            itemData.getTotalItemCount(), game.getGameArenaData().getName());
        game.getGameItemData().setItemData(itemData);
    }

    private ItemData createItemData(ConfigurationSection itemsSection, @Nullable Game game) {
        ItemData itemData = new ItemData();

        for (ChestType chestType : ChestType.values()) {
            int count = 0;
            ConfigurationSection chestTypeSection = itemsSection.getConfigurationSection(chestType.getName());
            if (chestTypeSection == null) {
                // If the section does not exist in a game, use defaults
                if (game != null && this.defaultItemData != null) {
                    itemData.setItems(chestType, this.defaultItemData.getItems(chestType));
                    count += this.defaultItemData.getItemCount(chestType);
                }
            } else {
                List<ItemStack> items = new ArrayList<>();
                for (String key : chestTypeSection.getKeys(false)) {
                    ConfigurationSection itemSection = chestTypeSection.getConfigurationSection(key);
                    if (itemSection == null) continue;

                    ItemStack itemStack = ItemParser.parseItem(itemSection);
                    int weight = itemSection.getInt("weight", 1);
                    for (int i = 0; i < weight; i++) {
                        items.add(itemStack);
                    }
                    count++;
                }
                itemData.setItems(chestType, items);
            }
            itemData.setItemCount(chestType, count);
        }
        return itemData;
    }

    public List<ItemStack> loadItems(ConfigurationSection config) {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection == null) continue;

                ItemStack itemStack = ItemParser.parseItem(itemSection);
                int weight = itemSection.getInt("weight", 1);
                for (int i = 0; i < weight; i++) {
                    items.add(itemStack);
                }
            }
        }
        return items;
    }

    public ItemStack getSpectatorCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        assert meta != null;
        meta.displayName(Util.getMini(plugin.getLang().spectator_compass));
        compass.setItemMeta(meta);
        return compass;
    }

}
