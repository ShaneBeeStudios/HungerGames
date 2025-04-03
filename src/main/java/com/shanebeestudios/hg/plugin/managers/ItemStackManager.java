package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.parsers.ItemParser;
import com.shanebeestudios.hg.api.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * Manage item stacks for kits and chests
 */
public class ItemStackManager {

    private final HungerGames plugin;

    public ItemStackManager(HungerGames plugin) {
        this.plugin = plugin;
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
