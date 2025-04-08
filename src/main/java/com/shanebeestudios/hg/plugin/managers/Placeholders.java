package com.shanebeestudios.hg.plugin.managers;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Language;
import com.shanebeestudios.hg.api.data.Leaderboard;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal placeholder class
 */
@SuppressWarnings("UnstableApiUsage")
public class Placeholders extends PlaceholderExpansion {

    private final HungerGames plugin;
    private final Leaderboard leaderboard;
    private final Language lang;

    public Placeholders(HungerGames plugin) {
        this.plugin = plugin;
        this.leaderboard = plugin.getLeaderboard();
        this.lang = plugin.getLang();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "hungergames";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return this.plugin.getPluginMeta().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }


    @Nullable
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String identifier) {
        GameManager gameManager = this.plugin.getGameManager();
        String[] id = identifier.split("_");
        switch (id[0]) {
            case "lb":
                switch (id[1]) {
                    case "wins":
                    case "kills":
                    case "deaths":
                    case "games":
                        if (id[2].equalsIgnoreCase("p"))
                            return getStatPlayers(identifier);
                        else if (id[2].equalsIgnoreCase("s"))
                            return getStatScores(identifier);
                        else if (id[2].equalsIgnoreCase("c"))
                            return getStatPlayers(identifier) + " : " + getStatScores(identifier);
                        else if (id[2].equalsIgnoreCase("player"))
                            return getStatsPlayer(identifier, offlinePlayer);
                }
            case "status":
                return gameManager.getGame(id[1]).getGameArenaData().getStatus().getStringName();
            case "player_status":
                if (offlinePlayer instanceof Player player)
                    return this.plugin.getPlayerManager().getPlayerStatus(player).getStringName();
            case "cost":
                return String.valueOf(gameManager.getGame(id[1]).getGameArenaData().getCost());
            case "playerscurrent":
                return String.valueOf(gameManager.getGame(id[1]).getGamePlayerData().getPlayers().size());
            case "playersmax":
                return String.valueOf(gameManager.getGame(id[1]).getGameArenaData().getMaxPlayers());
            case "playersmin":
                return String.valueOf(gameManager.getGame(id[1]).getGameArenaData().getMinPlayers());
        }
        return null;
    }

    private String getStatsPlayer(String identifier, OfflinePlayer player) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        return String.valueOf(leaderboard.getStat(player.getUniqueId(), stat));
    }

    private String getStatPlayers(String identifier) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        int leader = (Integer.parseInt(ind[3]));
        if (leaderboard.getStatsPlayers(stat).size() >= leader) {
            return leaderboard.getStatsPlayers(stat).get(leader - 1);
        } else {
            return lang.leaderboard_blank_space;
        }
    }

    private String getStatScores(String identifier) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        int leader = (Integer.parseInt(ind[3]));
        if (leaderboard.getStatsScores(stat).size() >= leader) {
            return leaderboard.getStatsScores(stat).get(leader - 1);
        } else {
            return lang.leaderboard_blank_space;
        }
    }

}
