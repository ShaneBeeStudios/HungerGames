package tk.shanebee.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.shanebee.hg.HG;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Leaderboard {

    private HG plugin;
    private FileConfiguration leaderboardConfig;
    private File config_file;
    private Map<String, Integer> wins;
    public List<String> sorted_players;
    public List<String> sorted_scores;

    public Leaderboard(HG pluging) {
        this.plugin = pluging;
        wins = new TreeMap<>();
        sorted_players = new ArrayList<>();
        sorted_scores = new ArrayList<>();
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

    private void saveLeaderboard() {
        leaderboardConfig.set("Total-Wins", wins);
        try {
            leaderboardConfig.save(config_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortScores();
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
        sortScores();
    }

    private void sortScores() {
        sorted_scores.clear();
        sorted_players.clear();
        for (Map.Entry<String, Integer> map : entriesSortedByValues(wins)) {
            String player = Bukkit.getOfflinePlayer(UUID.fromString(map.getKey())).getName();
            int score = map.getValue();
            sorted_scores.add(String.valueOf(score));
            sorted_players.add(player);
        }

    }

    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
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