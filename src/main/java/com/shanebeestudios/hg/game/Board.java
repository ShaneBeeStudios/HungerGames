package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Validate;

/**
 * Represents a team based scoreboard for a game
 */
public class Board {

    private static final ChatColor[] COLORS;

    static {
        COLORS = new ChatColor[]{ChatColor.AQUA, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.GOLD};
    }

    private final Game game;
    private final HungerGames plugin;
    private final Scoreboard scoreboard;
    private final Objective board;
    private final Team[] lines = new Team[15];
    private final Team team;
    private final String[] entries = new String[]{"&1&r", "&2&r", "&3&r", "&4&r", "&5&r", "&6&r", "&7&r", "&8&r", "&9&r", "&0&r", "&a&r", "&b&r", "&c&r", "&d&r", "&e&r"};

    @SuppressWarnings("ConstantConditions")
    public Board(Game game) {
        this.game = game;
        this.plugin = game.plugin;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        board = scoreboard.registerNewObjective("Board", "dummy", "Board");
        board.setDisplaySlot(DisplaySlot.SIDEBAR);
        board.setDisplayName(" ");

        for (int i = 0; i < 15; i++) {
            lines[i] = scoreboard.registerNewTeam("line" + (i + 1));
        }

        for (int i = 0; i < 15; i++) {
            lines[i].addEntry(Util.getColString(entries[i]));
        }
        team = scoreboard.registerNewTeam("game-team");

        if (Config.hideNametags) {
            team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        }
    }

    Team registerTeam(String name) {
        Team team = scoreboard.registerNewTeam(name);
        String prefix = Util.getColString(plugin.getLang().team_prefix.replace("<name>", name) + " ");
        team.setPrefix(prefix);
        String suffix = Util.getColString(" " + plugin.getLang().team_suffix.replace("<name>", name));
        team.setSuffix(suffix);
        team.setColor(COLORS[game.gamePlayerData.teams.size() % COLORS.length]);
        if (Config.hideNametags && Config.team_showTeamNames) {
            team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
        }
        team.setAllowFriendlyFire(Config.team_friendly_fire);
        team.setCanSeeFriendlyInvisibles(Config.team_see_invis);
        return team;
    }

    /**
     * Add a player to this scoreboard
     *
     * @param player Player to add
     */
    public void setBoard(Player player) {
        player.setScoreboard(scoreboard);
        team.addEntry(player.getName());
    }

    /**
     * Set the title of this scoreboard
     *
     * @param title Title to set
     */
    public void setTitle(String title) {
        board.setDisplayName(Util.getColString(title));
    }

    /**
     * Set a specific line for this scoreboard
     * <p>Lines 1 - 15</p>
     *
     * @param line Line to set (1 - 15)
     * @param text Text to put in line
     */
    public void setLine(int line, String text) {
        Validate.isBetween(line, 1, 15);
        Team t = lines[line - 1];
        if (ChatColor.stripColor(text).length() > (128 / 2)) {
            String prefix = Util.getColString(text.substring(0, (128 / 2)));
            t.setPrefix(prefix);
            String lastColor = ChatColor.getLastColors(prefix);
            int splitMax = Math.min(text.length(), 128 - lastColor.length());
            t.setSuffix(Util.getColString(lastColor + text.substring((128 / 2), splitMax)));
        } else {
            t.setPrefix(Util.getColString(text));
            t.setSuffix("");
        }
        board.getScore(Util.getColString(entries[line - 1])).setScore(line);
    }

    /**
     * Update this scoreboard
     */
    public void updateBoard() {
        Language lang = plugin.getLang();
        String alive = "  " + lang.players_alive_num.replace("<num>", String.valueOf(game.getGamePlayerData().getPlayers().size()));

        setTitle(lang.scoreboard_title);
        setLine(15, " ");
        setLine(14, lang.scoreboard_arena);
        setLine(13, "  &e" + game.getGameArenaData().getName());
        setLine(12, " ");
        setLine(11, lang.players_alive);
        setLine(10, alive);
        setLine(9, " ");
    }

    @Override
    public String toString() {
        return "Board{game=" + game + '}';
    }

}
