package tk.shanebee.hg.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

import java.util.List;
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
        if (identifier.startsWith("lb_player")) {
            int leader = Integer.valueOf(identifier.replace("lb_player_", ""));
            List<String> list =  plugin.leaderboard.getTop("player");
            if (list.size() >= leader)
                return list.get(leader - 1);
            else
                return "";
        }
        if (identifier.startsWith("lb_score")) {
            int leader = (Integer.valueOf(identifier.replace("lb_score_", "")));
            List<String> list = plugin.leaderboard.getTop("score");
            if (list.size() >= leader)
                return list.get(leader - 1);
            else
                return "";

        }
        return null;
    }
}
