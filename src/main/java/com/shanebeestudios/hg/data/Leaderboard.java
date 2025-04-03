package com.shanebeestudios.hg.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.plugin.HungerGames;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * HungerGames leader boards
 * <p>Stores different stats for players in games including wins, deaths, kills and games played.</p>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Leaderboard {

    private final HungerGames plugin;
    private final Language lang;
    private FileConfiguration leaderboardConfig;
    private File config_file;
    private final Map<String, Integer> wins;
    private final Map<String, Integer> kills;
    private final Map<String, Integer> deaths;
    private final Map<String, Integer> gamesPlayed;

    private final List<String> sorted_players_wins;
    private final List<String> sorted_scores_wins;
    private final List<String> sorted_players_kills;
    private final List<String> sorted_scores_kills;
    private final List<String> sorted_players_deaths;
    private final List<String> sorted_scores_deaths;
    private final List<String> sorted_players_gamesPlayed;
    private final List<String> sorted_scores_gamesPlayed;

    public Leaderboard(HungerGames plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        wins = new TreeMap<>();
        kills = new TreeMap<>();
        deaths = new TreeMap<>();
        gamesPlayed = new TreeMap<>();
        sorted_players_wins = new ArrayList<>();
        sorted_scores_wins = new ArrayList<>();
        sorted_players_kills = new ArrayList<>();
        sorted_scores_kills = new ArrayList<>();
        sorted_players_deaths = new ArrayList<>();
        sorted_scores_deaths = new ArrayList<>();
        sorted_players_gamesPlayed = new ArrayList<>();
        sorted_scores_gamesPlayed = new ArrayList<>();
        loadLeaderboard();
    }

    /** Add a win to the leaderboard
     * @param uuid UUID of player to add
     * @deprecated Use {@link #addStat(UUID, Stats)} instead
     */
    @Deprecated
    public void addWin(UUID uuid) {
        if (wins.containsKey(uuid.toString())) {
            wins.replace(uuid.toString(), wins.get(uuid.toString()) + 1);
        } else {
            wins.put(uuid.toString(), 1);
        }
        saveLeaderboard();
    }

    /** Add a win to the leaderboard
     * @param player Player to add
     * @deprecated Use {@link #addStat(Player, Stats)} instead
     */
    @Deprecated
    public void addWin(Player player) {
        addWin(player.getUniqueId());
    }

    /** Get the wins for a player from the leaderboard
     * @param uuid UUID of player to get wins for
     * @return Number of wins for the player
     * @deprecated Use {@link #getStat(UUID, Stats)} instead
     */
    @Deprecated
    public int getWins(UUID uuid) {
        return wins.get(uuid.toString());
    }

    /** Get the wins for a player from the leaderboard
     * @param player Player to get wins for
     * @return Number of wins for the player
     * @deprecated Use {@link #getStat(Player, Stats)} instead
     */
    @Deprecated
    public int getWins(Player player) {
        return getWins(player.getUniqueId());
    }

    /** Add a stat to the leaderboard (Will default to 1)
     * @param uuid Uuid of player to add
     * @param stat Stat to add
     */
    public void addStat(UUID uuid, Stats stat) {
        addStat(uuid, stat, 1);
    }

    /** Add a stat to the leaderboard
     * @param uuid Uuid of player to add
     * @param stat Stat to add
     * @param amount Amount to add
     */
    public void addStat(UUID uuid, Stats stat, int amount) {
        Map<String, Integer> map;
        switch (stat) {
            case KILLS:
                map = this.kills;
                break;
            case DEATHS:
                map = this.deaths;
                break;
            case GAMES:
                map = this.gamesPlayed;
                break;
            default:
                map = this.wins;
        }
        if (map.containsKey(uuid.toString())) {
            map.replace(uuid.toString(), map.get(uuid.toString()) + amount);
        } else {
            map.put(uuid.toString(), amount);
        }
        saveLeaderboard();
    }

    /** Add a stat to the leaderboard (Will default to 1)
     * @param player Player to add
     * @param stat Stat to add
     */
    public void addStat(Player player, Stats stat) {
        addStat(player, stat, 1);
    }

    /** Add a stat to the leaderboard
     * @param player Player to add
     * @param stat Stat to add
     * @param amount Amount to add
     */
    public void addStat(Player player, Stats stat, int amount) {
        addStat(player.getUniqueId(), stat, amount);
    }

    /** Get a stat from the leaderboard
     * @param player Player to get
     * @param stat Stat to get
     * @return Amount of the relative stat
     */
    public int getStat(Player player, Stats stat) {
        return getStat(player.getUniqueId(), stat);
    }

    /** Get a stat from the leaderboard
     * @param uuid Uuid of player to get
     * @param stat Stat to get
     * @return Amount of the relative stat
     */
    public int getStat(UUID uuid, Stats stat) {
        Map<String, Integer> map;
        switch (stat) {
            case KILLS:
                map = this.kills;
                break;
            case DEATHS:
                map = this.deaths;
                break;
            case GAMES:
                map = this.gamesPlayed;
                break;
            default:
                map = this.wins;
        }
        return map.getOrDefault(uuid.toString(), 0);
    }

    /** Gets a list of players from a stat
     * <p>Will match up with scores from {@link #getStatsScores(Stats)}</p>
     * @param stat Stat to get players from
     * @return Sorted list of players from a stat
     */
    public List<String> getStatsPlayers(Stats stat) {
        switch (stat) {
            case KILLS:
                return sorted_players_kills;
            case DEATHS:
                return sorted_players_deaths;
            case GAMES:
                return sorted_players_gamesPlayed;
            default:
                return sorted_players_wins;
        }
    }

    /** Gets a list of scores from a stat
     * <p>Will match up with players from {@link #getStatsPlayers(Stats)}</p>
     * @param stat Stat to get scores from
     * @return Sorted list of scores from a stat
     */
    public List<String> getStatsScores(Stats stat) {
        switch (stat) {
            case KILLS:
                return sorted_scores_kills;
            case DEATHS:
                return sorted_scores_deaths;
            case GAMES:
                return sorted_scores_gamesPlayed;
            default:
                return sorted_scores_wins;
        }
    }

    private void saveLeaderboard() {
        leaderboardConfig.set("Total-Wins", wins);
        leaderboardConfig.set("Total-Deaths", deaths);
        leaderboardConfig.set("Total-Kills", kills);
        leaderboardConfig.set("Games-Played", gamesPlayed);
        try {
            leaderboardConfig.save(config_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortScores(wins, sorted_scores_wins, sorted_players_wins);
        sortScores(kills, sorted_scores_kills, sorted_players_kills);
        sortScores(deaths, sorted_scores_deaths, sorted_players_deaths);
        sortScores(gamesPlayed, sorted_scores_gamesPlayed, sorted_players_gamesPlayed);
    }

    private void loadLeaderboard() {
        config_file = new File(plugin.getDataFolder(), "leaderboard.yml");
        if (!config_file.exists()) {
            plugin.saveResource("leaderboard.yml", true);
        }
        leaderboardConfig = YamlConfiguration.loadConfiguration(config_file);
        getLeaderboard("Total-Wins", wins, sorted_scores_wins, sorted_players_wins);
        getLeaderboard("Total-Kills", kills, sorted_scores_kills, sorted_players_kills);
        getLeaderboard("Total-Deaths", deaths, sorted_scores_deaths, sorted_players_deaths);
        getLeaderboard("Games-Played", gamesPlayed, sorted_scores_gamesPlayed, sorted_players_gamesPlayed);
    }

    private void getLeaderboard(String path, Map<String, Integer> map, List<String> scores, List<String> players) {
        if (leaderboardConfig.getConfigurationSection(path) != null) {
            //noinspection ConstantConditions
            for (String key : leaderboardConfig.getConfigurationSection(path).getKeys(false)) {
                map.put(key, leaderboardConfig.getInt(path + "." + key));
            }
            sortScores(map, scores, players);
        }
    }

    private void sortScores(Map<String, Integer> map, List<String> scores, List<String> players) {
        scores.clear();
        players.clear();
        for (Map.Entry<String, Integer> sortingMap : entriesSortedByValues(map)) {
            String player = Bukkit.getOfflinePlayer(UUID.fromString(sortingMap.getKey())).getName();
            int score = sortingMap.getValue();
            scores.add(String.valueOf(score));
            players.add(player != null ? player : lang.lb_missing_player);
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

    /**
     * Stat types for leaderboards
     */
    public enum Stats {
        /**
         * Amount of times a player has won a game
         */
        WINS("wins"),
        /**
         * Amount of players a player has killed in a game
         */
        KILLS("kills"),
        /**
         * Amount of times a player has died in a game
         */
        DEATHS("deaths"),
        /**
         * Amount of games a player has played
         * <p>Only counted for a game a player has either won or died in. Leaving a game does not count</p>
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
