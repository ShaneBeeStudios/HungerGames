package com.shanebeestudios.hg.api.game;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Data holder for a {@link Game Game's} scoreboard
 */
@SuppressWarnings("unused")
public class GameScoreboard extends Data {

    private final GameSidebar gameSidebar;
    private final Scoreboard scoreboard;
    private final Team baseBukkitTeam;
    private final Map<String, GameTeam> gameTeams = new HashMap<>();

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

    /**
     * Setup scoreboard of a player
     *
     * @param player Player to set up
     */
    public void setupBoard(Player player) {
        player.setScoreboard(this.scoreboard);
        this.baseBukkitTeam.addPlayer(player);
        this.gameSidebar.setBoard(player);
    }

    /**
     * Remove a player from the sidebar
     *
     * @param player Player to remove
     */
    public void removePlayerFromSidebar(Player player) {
        this.gameSidebar.removePlayer(player);
    }

    /**
     * Update sidebar of all players
     */
    public void updateBoards() {
        this.gameSidebar.updateBoard();
    }

    /**
     * Reset all sidebars
     */
    public void resetSidebars() {
        this.gameSidebar.reset();
    }

    /**
     * Get the {@link Scoreboard Bukkit Scoreboard} from this data
     *
     * @return Bukkit Scoreboard
     */
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    /**
     * Create a new {@link GameTeam}
     *
     * @param player   Leader of the team
     * @param teamName Name of team
     */
    public void createGameTeam(Player player, String teamName) {
        GameTeam gameTeam = new GameTeam(this.game, this, player, teamName);
        this.gameTeams.put(gameTeam.getTeamName(), gameTeam);
        updateBoards();
    }

    /**
     * Clear all {@link GameTeam GameTeams}
     */
    public void clearGameTeams() {
        this.gameTeams.forEach((team, gameTeam) -> gameTeam.unregister());
        this.gameTeams.clear();
    }

    /**
     * Check if a {@link GameTeam} exists
     *
     * @param teamName Name to check
     * @return Whether the team with this name exists
     */
    public boolean hasGameTeam(String teamName) {
        return this.gameTeams.containsKey(teamName);
    }

    /**
     * Get a {@link GameTeam} from name
     *
     * @param teamName Name of team
     * @return GameTeam if it exists
     */
    public GameTeam getGameTeam(String teamName) {
        return this.gameTeams.get(teamName);
    }

    /**
     * Get the {@link GameTeam} a player is on
     *
     * @param player Player to check
     * @return GameTeam if the player is on one else null
     */
    public @Nullable GameTeam getGameTeam(Player player) {
        for (GameTeam gameTeam : this.gameTeams.values()) {
            if (gameTeam.getPlayers().contains(player)) return gameTeam;
        }
        return null;
    }

}
