package com.shanebeestudios.hg.game;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.plugin.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an in game team
 */
public class GameTeam {

    private final Language lang;
    private final PlayerManager playerManager;
    private final GameScoreboard gameScoreboard;
    private final String teamName;
    private final Player leader;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> pending = new ArrayList<>();
    private final org.bukkit.scoreboard.Team bukkitTeam;

    GameTeam(Game game, GameScoreboard gameScoreboard, Player leader, String teamName) {
        this.lang = game.plugin.getLang();
        this.playerManager = game.plugin.getPlayerManager();
        this.gameScoreboard = gameScoreboard;
        this.teamName = teamName;
        this.leader = leader;
        PlayerData playerData = this.playerManager.getPlayerData(leader);
        assert playerData != null;
        this.players.add(leader);
        playerData.setTeam(this);
        playerData.setPendingTeam(null);

        // Board/McTeam stuff
        Team team = gameScoreboard.getScoreboard().getTeam(teamName);
        if (team == null) team = gameScoreboard.getScoreboard().registerNewTeam(teamName);
        this.bukkitTeam = team;
        this.bukkitTeam.addPlayer(leader);

        this.bukkitTeam.setAllowFriendlyFire(Config.TEAM_ALLOW_FRIENDLY_FIRE);
        this.bukkitTeam.setCanSeeFriendlyInvisibles(Config.TEAM_CAN_SEE_INVISIBLES);
        if (Config.SCOREBOARD_HIDE_NAMETAGS && Config.TEAM_SHOW_TEAM_NAMES) {
            this.bukkitTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        }
        this.bukkitTeam.prefix(Util.getMini(this.lang.team_prefix.replace("<name>", teamName)));
        this.bukkitTeam.suffix(Util.getMini(this.lang.team_suffix.replace("<name>", teamName)));
    }

    /**
     * Invite a player to this team
     * <p>This will send the player a message inviting them to the team</p>
     *
     * @param player Player to invite
     */
    public void invite(Player player) {
        Util.sendMessage(player, this.lang.team_invite_1);
        Util.sendMessage(player, this.lang.team_invite_2.replace("<inviter>", this.leader.getName()));
        Util.sendMessage(player, this.lang.team_invite_3);
        Util.sendMessage(player, this.lang.team_invite_4);
        this.pending.add(player);
        PlayerData playerData = this.playerManager.getData(player);
        assert playerData != null;
        playerData.setPendingTeam(this);
    }

    /**
     * Accept the invite to this team
     *
     * @param player Player to force to accept the invite
     */
    public void acceptInvite(Player player) {
        PlayerData playerData = this.playerManager.getPlayerData(player);
        assert playerData != null;
        playerData.setPendingTeam(null);
        playerData.setTeam(this);
        this.pending.remove(player);
        this.players.add(player);
        Util.sendMessage(player, this.lang.team_joined);
        this.bukkitTeam.addPlayer(player);
        this.gameScoreboard.updateBoards();
    }

    public void declineInvite(Player player) {
        PlayerData playerData = this.playerManager.getPlayerData(player);
        assert playerData != null;
        playerData.setPendingTeam(null);
        this.pending.remove(player);
        Util.sendMessage(this.leader, this.lang.command_team_deny.replace("<player>", player.getName()));
    }

    /**
     * Check if a player is on this team
     *
     * @param player Player to check
     * @return True if player is on this team
     */
    public boolean isOnTeam(Player player) {
        return this.players.contains(player);
    }

    /**
     * Check if a player is pending an invitation for this team
     *
     * @param player Player to check
     * @return True if this player is currently pending an invitation for this team.
     */
    public boolean isPending(Player player) {
        return this.pending.contains(player);
    }

    /**
     * Get the players on this team
     *
     * @return List of players on this team
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Get the pending players on this team
     *
     * @return List of players pending to be on this team
     */
    public List<Player> getPendingPlayers() {
        return this.pending;
    }

    /**
     * Get the leader of this team
     *
     * @return Player who is leading this team
     */
    public @NotNull Player getLeader() {
        return this.leader;
    }

    /**
     * Get the name of this team
     *
     * @return Name of team
     */
    public String getTeamName() {
        return this.teamName;
    }

    /**
     * Send a message to all members of this team
     *
     * @param message Message to send
     */
    public void messageMembers(String message) {
        for (Player player : this.players) {
            if (player != null) {
                Util.sendMessage(player, message);
            }
        }
    }

    public void unregister() {
        this.bukkitTeam.unregister();
    }

    @Override
    public String toString() {
        return "GameTeam{" +
            ", teamName='" + teamName + '\'' +
            ", leader=" + leader +
            ", players=" + players +
            ", pending=" + pending +
            '}';
    }

}
