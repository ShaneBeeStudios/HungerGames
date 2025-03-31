package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the data for the game's scoreboard/teams
 */
public class GameScoreboard extends Data {

    private final GameSidebar gameSidebar;
    private final Scoreboard scoreboard;
    private final Team baseBukkitTeam;
    final Map<String, GameTeam> gameTeams = new HashMap<>();

    protected GameScoreboard(Game game) {
        super(game);
        this.gameSidebar = new GameSidebar(game);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.baseBukkitTeam = this.scoreboard.registerNewTeam("base_team_for_game");
        if (Config.SCOREBOARD_HIDE_NAMETAGS) {
            this.baseBukkitTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        if (Config.SCOREBOARD_SHOW_HEALTH_ENABLED) {
            Objective objective = this.scoreboard.registerNewObjective("health", Criteria.HEALTH,
                Util.getMini(this.lang.scoreboard_show_health_name));
            DisplaySlot displaySlot = Config.SCOREBOARD_SHOW_HEALTH_DISPLAY_SLOT.equalsIgnoreCase("below_name") ? DisplaySlot.BELOW_NAME : DisplaySlot.PLAYER_LIST;
            RenderType renderType = Config.SCOREBOARD_SHOW_HEALTH_RENDER_TYPE.equalsIgnoreCase("hearts") ? RenderType.HEARTS : RenderType.INTEGER;
            objective.setDisplaySlot(displaySlot);
            objective.setRenderType(renderType);
        }
    }

    public void setupBoard(Player player) {
        player.setScoreboard(this.scoreboard);
        this.baseBukkitTeam.addPlayer(player);
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

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void createGameTeam(Player player, String teamName) {
        GameTeam gameTeam = new GameTeam(this.game, this, player, teamName);
        this.gameTeams.put(gameTeam.getTeamName(), gameTeam);
        updateBoards();
    }

    public void clearGameTeams() {
        this.gameTeams.forEach((team, gameTeam) -> gameTeam.unregister());
        this.gameTeams.clear();
    }

    public boolean hasGameTeam(String teamName) {
        return this.gameTeams.containsKey(teamName);
    }

    public GameTeam getGameTeam(String teamName) {
        return this.gameTeams.get(teamName);
    }

    public @Nullable GameTeam getGameTeam(Player player) {
        for (GameTeam gameTeam : this.gameTeams.values()) {
            if (gameTeam.getPlayers().contains(player)) return gameTeam;
        }
        return null;
    }

}
