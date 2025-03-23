package com.shanebeestudios.hg.managers;

import com.shanebeestudios.hg.api.util.Util;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.Leaderboard;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.startsWith("lb_player_")) {
            int leader = Integer.parseInt(identifier.replace("lb_player_", ""));
            if (leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return lang.lb_blank_space;
        }
        if (identifier.startsWith("lb_score_")) {
            int leader = (Integer.parseInt(identifier.replace("lb_score_", "")));
            if (leaderboard.getStatsScores(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsScores(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return lang.lb_blank_space;

        }
        if (identifier.startsWith("lb_combined_")) {
            int leader = (Integer.parseInt(identifier.replace("lb_combined_", "")));
            if (leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).get(leader - 1) + lang.lb_combined_separator +
                        leaderboard.getStatsScores(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return lang.lb_blank_space + lang.lb_combined_separator + lang.lb_blank_space;
        }
        if (identifier.equalsIgnoreCase("lb_player")) {
            return String.valueOf(leaderboard.getStat(player.getUniqueId(), Leaderboard.Stats.WINS));
        }
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
                            return getStatsPlayer(identifier, player);
                }
            case "status":
                Component name = this.plugin.getGameManager().getGame(id[1]).getGameArenaData().getStatus().getName();
                return Util.unMini(name);
            case "cost":
                return String.valueOf(this.plugin.getGameManager().getGame(id[1]).getGameArenaData().getCost());
            case "playerscurrent":
                return String.valueOf(this.plugin.getGameManager().getGame(id[1]).getGamePlayerData().getPlayers().size());
            case "playersmax":
                return String.valueOf(this.plugin.getGameManager().getGame(id[1]).getGameArenaData().getMaxPlayers());
            case "playersmin":
                return String.valueOf(this.plugin.getGameManager().getGame(id[1]).getGameArenaData().getMinPlayers());
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
            return lang.lb_blank_space;
        }
    }

    private String getStatScores(String identifier) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        int leader = (Integer.parseInt(ind[3]));
        if (leaderboard.getStatsScores(stat).size() >= leader) {
            return leaderboard.getStatsScores(stat).get(leader - 1);
        } else {
            return lang.lb_blank_space;
        }
    }

}
