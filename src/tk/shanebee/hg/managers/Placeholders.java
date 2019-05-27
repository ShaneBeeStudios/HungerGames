package tk.shanebee.hg.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;

import java.util.Map;

public class Placeholders extends PlaceholderExpansion {

    private HG plugin;
    private Map<String, Integer> wins;

    public Placeholders(HG plugin) {
        this.plugin = plugin;
        this.wins = plugin.leaderboard.wins;
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
        if (identifier.contains("leaderboard_player")) {
            int leader = Integer.valueOf(identifier.replace("leaderboard_player_", ""));
            return plugin.leaderboard.getTop("player").get(leader);
        }
        if (identifier.contains("leaderboard_score")) {
            int leader = Integer.valueOf(identifier.replace("leaderboard_score_", ""));
            return plugin.leaderboard.getTop("score").get(leader);
        }
        return null;
    }
}
