package tk.shanebee.hg.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.Leaderboard;

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
        if (identifier.startsWith("lb_player")) {
            int leader = Integer.valueOf(identifier.replace("lb_player_", ""));
            if (leaderboard.sorted_players.size() >= leader)
                return leaderboard.sorted_players.get(leader - 1);
            else
                return "";
        }
        if (identifier.startsWith("lb_score")) {
            int leader = (Integer.valueOf(identifier.replace("lb_score_", "")));
            if (leaderboard.sorted_scores.size() >= leader)
                return leaderboard.sorted_scores.get(leader - 1);
            else
                return "";

        }
        return null;
    }

}
