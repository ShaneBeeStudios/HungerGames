package com.shanebeestudios.hg.managers;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.KitEntry;
import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.util.NBTApi;
import com.shanebeestudios.hg.api.util.PotionEffectUtils;
import com.shanebeestudios.hg.api.util.PotionTypeUtils;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage item stacks for kits and chests
 */
public class ItemStackManager {

    private final HungerGames plugin;
    private final NBTApi nbtApi;

    public ItemStackManager(HungerGames plugin) {
        this.plugin = plugin;
        this.nbtApi = plugin.getNbtApi();
        loadGeneralKits();
    }

    public void loadGeneralKits() {
        File kitFile = new File(this.plugin.getDataFolder(), "kits.yml");

        if (!kitFile.exists()) {
            this.plugin.saveResource("kits.yml", false);
            Util.log("- New kits.yml file has been <green>successfully generated!");
        }
        YamlConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
        Util.log("Loading kits:");
        kitCreator(kitConfig, plugin.getKitManager(), null);
        Util.log("- Kits have been <green>successfully loaded!");
    }

    /**
     * Set the kits for a game from a config
     *
     * @param game The game to set the kits for
     * @param arenaSection   Config the kit is pulled from
     * @return New KitManager for a game
     */
    public KitManager loadGameKits(Game game, ConfigurationSection arenaSection) {
        KitManager kit = new KitManager();
        if (arenaSection.getConfigurationSection("kits") == null) return null;

        kitCreator(arenaSection, kit, null);
        Util.log("- Loaded custom kits for arena: <aqua>" + game.getGameArenaData().getName());
        return kit;
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private void kitCreator(ConfigurationSection config, KitManager kit, @Nullable String gameName) {
        if (gameName == null) gameName = "";
        if (config.getConfigurationSection(gameName + "kits") == null) return;

        for (String path : config.getConfigurationSection(gameName + "kits").getKeys(false)) {
            ConfigurationSection kitsSection = config.getConfigurationSection(gameName + "kits." + path);

            try {
                Map<Integer, ItemStack> items = new HashMap<>();
                loadItems(kitsSection.getMapList("items"), items);

                ItemStack helmet = ItemParser.parseItem(kitsSection.getConfigurationSection("helmet"));
                ItemStack chestplate = ItemParser.parseItem(kitsSection.getConfigurationSection("chestplate"));
                ItemStack leggings = ItemParser.parseItem(kitsSection.getConfigurationSection("leggings"));
                ItemStack boots = ItemParser.parseItem(kitsSection.getConfigurationSection("boots"));

                List<PotionEffect> potionEffects = new ArrayList<>();
                List<Map<?, ?>> mapList = kitsSection.getMapList("potion-effects");
                mapList.forEach(map -> potionEffects.add(ItemParser.parsePotionEffect((Map<String, Object>) map)));

                String permission = null;
                if (kitsSection.contains("permission") && !kitsSection.getString("permission").equalsIgnoreCase("none"))
                    permission = kitsSection.getString("permission");

                KitEntry kitEntry = new KitEntry(items.values().toArray(new ItemStack[0]), helmet, boots, chestplate, leggings, permission, potionEffects);
                kit.addKit(path, kitEntry);
                Util.log("- Loaded kit <white>'<aqua>%s<white>'", gameName + path);
            } catch (Exception e) {
                Util.log("-------------------------------------------");
                Util.log("<yellow>Unable to load kit " + gameName + path + "! (for a more detailed message enable 'debug' in config and reload)");
                Util.log("-------------------------------------------");
                Util.debug(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadItems(List<Map<?, ?>> configListMap, Map<Integer, ItemStack> map) {
        for (Map<?, ?> itemMapUnchecked : configListMap) {
            Map<String, Object> itemMap = (Map<String, Object>) itemMapUnchecked;

            ItemStack itemStack = ItemParser.parseItem(itemMap);
            int weight = (int) itemMap.getOrDefault("weight", 1);
            for (int i = 0; i < weight; i++) {
                map.put(map.size() + 1, itemStack);
            }
        }
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
