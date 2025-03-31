package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a sidebar for the players
 */
public class GameSidebar {

    private final Game game;
    private final Language lang;
    private final Map<Player, FastBoard> playersFastBoards = new HashMap<>();

    private Component title;

    public GameSidebar(Game game) {
        this.game = game;
        this.lang = game.lang;
        setTitle(this.lang.scoreboard_sidebar_title);

    }

    /**
     * Add a player to this scoreboard
     *
     * @param player Player to add
     */
    public void setBoard(Player player) {
        if (!this.playersFastBoards.containsKey(player)) {
            this.playersFastBoards.put(player, new FastBoard(player));
        }
    }

    public void removePlayer(Player player) {
        FastBoard fastBoard = this.playersFastBoards.get(player);
        if (fastBoard != null) {
            fastBoard.delete();
        }
        this.playersFastBoards.remove(player);
    }

    /**
     * Set the title of this scoreboard
     *
     * @param title Title to set
     */
    public void setTitle(String title) {
        this.title = Util.getMini(title);
    }

    /**
     * Update this scoreboard
     */
    public void updateBoard() {
        String alive = "  " + this.lang.scoreboard_sidebar_players_alive_num.replace("<num>", String.valueOf(this.game.getGamePlayerData().getPlayers().size()));
        String name = this.game.getGameArenaData().getName();

        this.playersFastBoards.forEach((player, board) -> {
            board.updateTitle(this.title);
            List<Component> lines = new ArrayList<>();
            lines.add(Util.getMini(" "));
            lines.add(Util.getMini(this.lang.scoreboard_sidebar_arena));
            lines.add(Util.getMini("  <yellow>" + name));
            lines.add(Util.getMini(" "));
            lines.add(Util.getMini(this.lang.scoreboard_sidebar_players_alive));
            lines.add(Util.getMini(alive));
            lines.add(Util.getMini(" "));

            // Team stuff
            GameTeam gameTeam = this.game.getGameScoreboard().getGameTeam(player);
            if (gameTeam != null) {
                lines.add(Util.getMini("Team:"));
                lines.add(Util.getMini(" <grey>Name: <aqua>" + gameTeam.getTeamName()));
                lines.add(Util.getMini(" <grey>Players: <green>" + gameTeam.getPlayers().size()));
            }

            board.updateLines(lines);
        });
    }

    public void reset() {
        this.playersFastBoards.forEach((player, fastBoard) -> fastBoard.delete());
        this.playersFastBoards.clear();
    }

    @Override
    public String toString() {
        return "Board{game=" + this.game + '}';
    }

}
