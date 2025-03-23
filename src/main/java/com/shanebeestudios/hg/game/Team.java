package com.shanebeestudios.hg.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.api.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a HungerGames team
 */
public class Team {

    private final String name;
    private final UUID leader;
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> pending = new ArrayList<>();
    private final org.bukkit.scoreboard.Team bukkitTeam;

    public Team(Player leader, String name, Game game) {
        HungerGames plugin = HungerGames.getPlugin();
        this.name = name;
        this.leader = leader.getUniqueId();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(leader);
        players.add(leader.getUniqueId());
        playerData.setTeam(this);
        playerData.setPendingTeam(null);

        // Board/McTeam stuff
        bukkitTeam = game.gameArenaData.getBoard().registerTeam(name);
        bukkitTeam.addEntry(leader.getName());
    }

    /**
     * Invite a player to this team
     * <p>This will send the player a message inviting them to the team</p>
     *
     * @param player Player to invite
     */
    public void invite(Player player) {
        Player leader = Bukkit.getPlayer(this.leader);
        assert leader != null;
        Util.sendMessage(player, HungerGames.getPlugin().getLang().team_invite_1);
        Util.sendMessage(player, HungerGames.getPlugin().getLang().team_invite_2.replace("<inviter>", leader.getName()));
        Util.sendMessage(player, HungerGames.getPlugin().getLang().team_invite_3);
        Util.sendMessage(player, HungerGames.getPlugin().getLang().team_invite_4);
        pending.add(player.getUniqueId());
        HungerGames.getPlugin().getPlayerManager().getData(player).setPendingTeam(this);
    }

    /**
     * Accept the invite to this team
     *
     * @param player Player to force to accept the invite
     */
    public void acceptInvite(Player player) {
        PlayerData playerData = HungerGames.getPlugin().getPlayerManager().getPlayerData(player);
        playerData.setPendingTeam(null);
        playerData.setTeam(this);
        pending.remove(player.getUniqueId());
        players.add(player.getUniqueId());
        Util.sendMessage(player, HungerGames.getPlugin().getLang().joined_team);
        bukkitTeam.addEntry(player.getName());
    }

    /**
     * Check if a player is on this team
     *
     * @param uuid UUID of player to check
     * @return True if player is on this team
     */
    public boolean isOnTeam(UUID uuid) {
        return (players.contains(uuid));
    }

    /**
     * Check if a player is pending an invite for this team
     *
     * @param uuid UUID of player to check
     * @return True if this player is currently pending an invite for this team.
     */
    public boolean isPending(UUID uuid) {
        return (pending.contains(uuid));
    }

    /**
     * Get the players on this team
     *
     * @return List of UUIDs of players on this team
     */
    public List<UUID> getPlayers() {
        return players;
    }

    /**
     * Get the pending players on this team
     *
     * @return List of UUIDs of players pending to be on this team
     */
    public List<UUID> getPenders() {
        return pending;
    }

    /**
     * Get the leader of this team
     *
     * @return UUID of player who is leading this team
     */
    public UUID getLeader() {
        return leader;
    }

    /**
     * Get the name of this team
     *
     * @return Name of team
     */
    public String getName() {
        return name;
    }

    /**
     * Send a message to all members of this team
     *
     * @param message Message to send
     */
    public void messageMembers(String message) {
        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Util.sendMessage(player, message);
            }
        }
    }

    @Override
    public String toString() {
        return "Team{leader=" + leader + ", players=" + players + ", pending=" + pending + '}';
    }

}
