package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.KitData;
import com.shanebeestudios.hg.data.KitEntry;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General manager for kits
 */
public class KitManager {

    private final HungerGames plugin;
    private final ItemStackManager itemStackManager;
    private KitData defaultKitData;

    public KitManager(HungerGames plugin) {
        this.plugin = plugin;
        this.itemStackManager = plugin.getItemStackManager();
        loadDefaultKits();
    }

    private void loadDefaultKits() {
        File kitFile = new File(this.plugin.getDataFolder(), "kits.yml");

        if (!kitFile.exists()) {
            this.plugin.saveResource("kits.yml", false);
            Util.log("- New kits.yml file has been <green>successfully generated!");
        }
        YamlConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
        ConfigurationSection kitsSection = kitConfig.getConfigurationSection("kits");
        assert kitsSection != null;
        Util.log("Loading kits:");
        this.defaultKitData = kitCreator(kitsSection, null);
        Util.log("- Kits have been <green>successfully loaded!");
    }

    /**
     * Get the default KitData
     *
     * @return Default KitData
     */
    public KitData getDefaultKitData() {
        return this.defaultKitData;
    }

    /**
     * Set the kits for a game from a config
     *
     * @param game         The game to set the kits for
     * @param arenaSection Config the kit is pulled from
     */
    public void loadGameKits(Game game, ConfigurationSection arenaSection) {
        ConfigurationSection kitsSection = arenaSection.getConfigurationSection("kits");
        if (kitsSection == null) return;

        KitData kitData = kitCreator(kitsSection, game);
        Util.log("- Loaded custom kits for arena: <aqua>" + game.getGameArenaData().getName());
        game.getGameItemData().setKitData(kitData);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private KitData kitCreator(ConfigurationSection kitsSection, @Nullable Game game) {
        KitData kit = new KitData();
        String gameName = game != null ? game.getGameArenaData().getName() + ":" : "";
        for (String kitName : kitsSection.getKeys(false)) {
            try {
                ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitName);
                Map<Integer, ItemStack> items = new HashMap<>();
                this.itemStackManager.loadItems(kitSection.getMapList("items"), items);

                ItemStack helmet = ItemParser.parseItem(kitSection.getConfigurationSection("helmet"));
                ItemStack chestplate = ItemParser.parseItem(kitSection.getConfigurationSection("chestplate"));
                ItemStack leggings = ItemParser.parseItem(kitSection.getConfigurationSection("leggings"));
                ItemStack boots = ItemParser.parseItem(kitSection.getConfigurationSection("boots"));

                List<PotionEffect> potionEffects = new ArrayList<>();
                List<Map<?, ?>> mapList = kitSection.getMapList("potion-effects");
                mapList.forEach(map -> potionEffects.add(ItemParser.parsePotionEffect((Map<String, Object>) map)));

                String permission = null;
                if (kitSection.contains("permission") && !kitSection.getString("permission").equalsIgnoreCase("none"))
                    permission = kitSection.getString("permission");

                KitEntry kitEntry = new KitEntry(kitName, new ArrayList<>(items.values()), helmet, chestplate, leggings, boots, permission, potionEffects);
                kit.addKitEntry(kitName, kitEntry);
                Util.log("- Loaded kit <white>'<aqua>%s<white>'", gameName + kitName);
            } catch (Exception e) {
                Util.log("-------------------------------------------");
                Util.log("<yellow>Unable to load kit " + gameName + kitName + "! (for a more detailed message enable 'debug' in config and reload)");
                Util.log("-------------------------------------------");
                Util.debug(e);
            }
        }
        return kit;
    }

}
