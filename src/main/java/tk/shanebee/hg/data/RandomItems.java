package tk.shanebee.hg.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

import java.io.File;
import java.util.Map;

/**
 * Handler for random items
 */
public class RandomItems {

    private FileConfiguration item = null;
    private File customConfigFile = null;
    public int size = 0;
    private final HG plugin;

    public RandomItems(HG plugin) {
        this.plugin = plugin;
        reloadCustomConfig();
        Util.log("Loading random items...");
        load();
    }

    private void reloadCustomConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), "items.yml");
        }
        if (!customConfigFile.exists()) {
            plugin.saveResource("items.yml", false);
            Util.log("New items.yml file has been &asuccessfully generated!");
        }
        item = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public void load() {
        // Regular items
        for (String s : item.getStringList("items")) {
            loadItems(s, plugin.getItems());
        }
        // Bonus items
        for (String s : item.getStringList("bonus")) {
            loadItems(s, plugin.getBonusItems());
        }
        Util.log(plugin.getItems().size() + " Random items have been &aloaded!");
        Util.log(plugin.getBonusItems().size() + " Random bonus items have been &aloaded!");
    }

    void loadItems(String itemString, Map<Integer, ItemStack> map) {
        String[] amount = itemString.split(" ");
        if (itemString.contains("x:")) {
            for (String p : amount) {
                if (p.startsWith("x:")) {
                    int c = Integer.parseInt(p.replace("x:", ""));
                    ItemStack stack = plugin.getItemStackManager().getItem(itemString.replace("x:", ""), true);
                    if (stack == null) {
                        continue;
                    }
                    while (c != 0) {
                        c--;
                        map.put(map.size() + 1, stack.clone());
                    }
                }
            }
        } else {
            map.put(map.size() + 1, plugin.getItemStackManager().getItem(itemString, true));
        }
    }

}
