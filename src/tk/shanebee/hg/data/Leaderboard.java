package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class Leaderboard {

    private HG plugin;
    public Map<String, Integer> wins;
    private FileConfiguration leaderboardConfig;
    private File config_file;

    public Leaderboard(HG pluging) {
        this.plugin = pluging;
        wins = new TreeMap<>();
        loadLeaderboard();
    }

    public void addWin(UUID uuid) {
        if (wins.containsKey(uuid.toString())) {
            wins.replace(uuid.toString(), wins.get(uuid.toString()) + 1);
        } else {
            wins.put(uuid.toString(), 1);
        }
        saveLeaderboard();
    }

    public int getWins(Player player) {
        String uuid = player.getUniqueId().toString();
        return wins.getOrDefault(uuid, 0);
    }

    private void saveLeaderboard() {
        leaderboardConfig.set("Total-Wins", wins);
        try {
            leaderboardConfig.save(config_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLeaderboard() {
        config_file = new File(plugin.getDataFolder(), "leaderboard.yml");
        if (!config_file.exists()) {
            plugin.saveResource("leaderboard.yml", true);
        }
        leaderboardConfig = YamlConfiguration.loadConfiguration(config_file);
        if (leaderboardConfig.getConfigurationSection("Total-Wins") == null) return;
        for (String key : leaderboardConfig.getConfigurationSection("Total-Wins").getKeys(false)) {
            wins.put(key, leaderboardConfig.getInt("Total-Wins." + key));
        }
    }

    public List<String> getTop(String entry) {
        List<String> top = new ArrayList<>();
        for (Map.Entry<String, Integer> map : entriesSortedByValues(wins)) {
            String player = Bukkit.getOfflinePlayer(UUID.fromString(map.getKey())).getName();
            int score = map.getValue();
            top.add(entry.equalsIgnoreCase("player") ? player : String.valueOf(score));
        }
        return top;
    }

    private static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                (Map.Entry<K, V> e2, Map.Entry<K, V> e1) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    if (res == 0) return 1;
                    else return res;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
