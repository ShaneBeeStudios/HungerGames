package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Represents the data for the game's scoreboard/teams
 */
public class GameScoreboard extends Data {

    private final GameSidebar gameSidebar;
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    protected GameScoreboard(Game game) {
        super(game);
        this.gameSidebar = new GameSidebar(game);
    }

    public void setupBoard(Player player) {
        player.setScoreboard(this.scoreboard);
        this.gameSidebar.setBoard(player);
    }

    public void removePlayer(Player player) {
        this.gameSidebar.removePlayer(player);
    }

    public void updateBoards() {
        this.gameSidebar.updateBoard();
    }

    public void resetBoards() {
        this.gameSidebar.reset();
    }

}
