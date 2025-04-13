package com.shanebeestudios.hg.api.data;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * HungerGames leader boards
 * <p>Stores different stats for players in games including wins, deaths, kills and games played.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Leaderboard {

    private static class Data implements Comparable<Data> {
        private final String playerName;
        private int score;

        public Data(UUID uuid) {
            this.playerName = Bukkit.getOfflinePlayer(uuid).getName();
        }

        @Override
        public int compareTo(@NotNull Data data) {
            return data.score - this.score;
        }
    }

    private final HungerGames plugin;
    private FileConfiguration leaderboardConfig;
    private File config_file;
    private final Map<Stats, Map<String, Data>> stats = new HashMap<>();

    public Leaderboard(HungerGames plugin) {
        this.plugin = plugin;
        for (Stats value : Stats.values()) {
            this.stats.put(value, new LinkedHashMap<>());
        }
        loadLeaderboard();
    }

    /**
     * Add a stat to the leaderboard
     *
     * @param uuid   Uuid of player to add
     * @param stat   Stat to add
     * @param amount Amount to add
     */
    public void addStat(UUID uuid, Stats stat, int amount) {
        Map<String, Data> map = this.stats.get(stat);
        Data data = map.getOrDefault(uuid.toString(), new Data(uuid));
        data.score += amount;
        map.put(uuid.toString(), data);
        sort(stat);
    }

    /**
     * Add a stat to the leaderboard (Will default to 1)
     *
     * @param player Player to add
     * @param stat   Stat to add
     */
    public void addStat(Player player, Stats stat) {
        addStat(player, stat, 1);
    }

    /**
     * Add a stat to the leaderboard
     *
     * @param player Player to add
     * @param stat   Stat to add
     * @param amount Amount to add
     */
    public void addStat(Player player, Stats stat, int amount) {
        addStat(player.getUniqueId(), stat, amount);
    }

    /**
     * Get a stat from the leaderboard
     *
     * @param player Player to get
     * @param stat   Stat to get
     * @return Amount of the relative stat
     */
    public int getStat(Player player, Stats stat) {
        return getStat(player.getUniqueId(), stat);
    }

    /**
     * Get a stat from the leaderboard
     *
     * @param uuid Uuid of player to get
     * @param stat Stat to get
     * @return Amount of the relative stat
     */
    public int getStat(UUID uuid, Stats stat) {
        Map<String, Data> map = this.stats.get(stat);
        if (map.containsKey(uuid.toString())) {
            return map.get(uuid.toString()).score;
        }
        return 0;
    }

    /**
     * Gets a list of players from a stat
     * <p>Will match up with scores from {@link #getStatsScores(Stats)}</p>
     *
     * @param stat Stat to get players from
     * @return Sorted list of players from a stat
     */
    public List<String> getStatsPlayers(Stats stat) {
        Map<String, Data> map = this.stats.get(stat);
        List<String> playerNames = new ArrayList<>();
        for (Map.Entry<String, Data> entry : map.entrySet()) {
            playerNames.add(entry.getValue().playerName);
        }
        return playerNames;
    }

    /**
     * Gets a list of scores from a stat
     * <p>Will match up with players from {@link #getStatsPlayers(Stats)}</p>
     *
     * @param stat Stat to get scores from
     * @return Sorted list of scores from a stat
     */
    public List<Integer> getStatsScores(Stats stat) {
        Map<String, Data> map = this.stats.get(stat);
        List<Integer> playerScores = new ArrayList<>();
        for (Map.Entry<String, Data> entry : map.entrySet()) {
            playerScores.add(entry.getValue().score);
        }
        return playerScores;
    }

    public void saveLeaderboard() {
        for (Stats stat : Stats.values()) {
            ConfigurationSection statSection = this.leaderboardConfig.createSection(stat.getName());
            this.stats.get(stat).forEach((key, value) ->
                statSection.set(key, value.score));
        }
        try {
            this.leaderboardConfig.save(this.config_file);
        } catch (IOException e) {
            Util.warning("Could not save leaderboard file: %s", e.getMessage());
        }
    }

    private void loadLeaderboard() {
        this.config_file = new File(this.plugin.getDataFolder(), "leaderboard.yml");
        if (!this.config_file.exists()) {
            this.plugin.saveResource("leaderboard.yml", true);
        }
        this.leaderboardConfig = YamlConfiguration.loadConfiguration(this.config_file);
        for (Stats stat : Stats.values()) {
            ConfigurationSection statSection = this.leaderboardConfig.getConfigurationSection(stat.getName());
            if (statSection == null) continue;

            Map<String, Data> map = new HashMap<>();
            for (String key : statSection.getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                Data data = new Data(uuid);
                data.score = statSection.getInt(key);
                map.put(key, data);
            }
            this.stats.put(stat, map);
            sort(stat);
        }
    }

    private void sort(Stats stat) {
        LinkedHashMap<String, Data> collect = this.stats.get(stat).entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
        this.stats.put(stat, collect);
    }

    /**
     * Stat types for leaderboards
     */
    public enum Stats {
        /**
         * Amount of times a player has won a game
         */
        WINS("wins"),
        /**
         * Amount of players a player has killed in all games
         */
        KILLS("kills"),
        /**
         * Amount of times a player has died in a game
         */
        DEATHS("deaths"),
        /**
         * Amount of games a player has played
         * <p>Only counted for a game a player has either won or died in.
         * Leaving a game does not count</p>
         */
        GAMES("games");

        private final String stat;

        Stats(String stat) {
            this.stat = stat;
        }

        public String getName() {
            return this.stat;
        }
    }

}
