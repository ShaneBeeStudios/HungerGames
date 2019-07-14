package tk.shanebee.hg.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Leaderboard;

/**
 * Internal placeholder class
 */
public class Placeholders extends PlaceholderExpansion {

    private HG plugin;
    private Leaderboard leaderboard;

    public Placeholders(HG plugin) {
        this.plugin = plugin;
        this.leaderboard = plugin.getLeaderboard();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "hungergames";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("lb_player_")) {
            int leader = Integer.valueOf(identifier.replace("lb_player_", ""));
            if (leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return "";
        }
        if (identifier.startsWith("lb_score_")) {
            int leader = (Integer.valueOf(identifier.replace("lb_score_", "")));
            if (leaderboard.getStatsScores(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsScores(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return "";

        }
        if (identifier.startsWith("lb_combined_")) {
            int leader = (Integer.valueOf(identifier.replace("lb_combined_", "")));
            if (leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).size() >= leader)
                return leaderboard.getStatsPlayers(Leaderboard.Stats.WINS).get(leader - 1) + " : " +
                        leaderboard.getStatsScores(Leaderboard.Stats.WINS).get(leader - 1);
            else
                return "";
        }
        if (identifier.equalsIgnoreCase("lb_player")) {
            return String.valueOf(leaderboard.getStat(player, Leaderboard.Stats.WINS));
        }
        String[] ind = identifier.split("_");
        switch (ind[1]) {
            case "wins":
            case "kills":
            case "deaths":
            case "games":
                if (ind[2].equalsIgnoreCase("p"))
                    return getStatPlayers(identifier);
                else if (ind[2].equalsIgnoreCase("s"))
                    return getStatScores(identifier);
                else if (ind[2].equalsIgnoreCase("c"))
                    return getStatPlayers(identifier) + " : " + getStatScores(identifier);
                else if (ind[2].equalsIgnoreCase("player"))
                    return getStatsPlayer(identifier, player);
        }
        return null;
    }

    private String getStatsPlayer(String identifier, Player player) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        return String.valueOf(leaderboard.getStat(player, stat));
    }

    private String getStatPlayers(String identifier) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        int leader = (Integer.valueOf(ind[3]));
        if (leaderboard.getStatsPlayers(stat).size() >= leader) {
            return leaderboard.getStatsPlayers(stat).get(leader - 1);
        } else {
            return "";
        }
    }

    private String getStatScores(String identifier) {
        String[] ind = identifier.split("_");
        Leaderboard.Stats stat = Leaderboard.Stats.valueOf(ind[1].toUpperCase());
        int leader = (Integer.valueOf(ind[3]));
        if (leaderboard.getStatsScores(stat).size() >= leader) {
            return leaderboard.getStatsScores(stat).get(leader - 1);
        } else {
            return "";
        }
    }

}
