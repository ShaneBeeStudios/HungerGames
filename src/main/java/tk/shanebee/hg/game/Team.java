package tk.shanebee.hg.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.util.Util;

/**
 * General team handler
 */
public class Team {

	private final UUID leader;
	private final List<UUID> players = new ArrayList<>();
	private final List<UUID> pending = new ArrayList<>();
	
	public Team(UUID leader) {
		this.leader = leader;
        PlayerData playerData = HG.getPlugin().getPlayerManager().getPlayerData(leader);
		players.add(leader);
		playerData.setTeam(this);
		playerData.setPendingTeam(null);
	}

    /** Invite a player to this team
     * <p>This will send the player a message inviting them to the team</p>
     * @param player Player to invite
     */
	public void invite(Player player) {
	    Player p = Bukkit.getPlayer(leader);
	    assert p != null;
		Util.scm(player, HG.getPlugin().getLang().team_invite_1);
		Util.scm(player, HG.getPlugin().getLang().team_invite_2.replace("<inviter>", p.getName()));
		Util.scm(player, HG.getPlugin().getLang().team_invite_3);
		Util.scm(player, HG.getPlugin().getLang().team_invite_4);
		pending.add(player.getUniqueId());
		HG.getPlugin().getPlayerManager().getData(player).setPendingTeam(this);
	}

    /** Accept the invite to this team
     * @param player Player to force to accept the invite
     */
	public void acceptInvite(Player player) {
        PlayerData playerData = HG.getPlugin().getPlayerManager().getPlayerData(player);
        playerData.setPendingTeam(null);
        playerData.setTeam(this);
		pending.remove(player.getUniqueId());
		players.add(player.getUniqueId());
		Util.scm(player, HG.getPlugin().getLang().joined_team);
	}

    /** Check if a player is on this team
     * @param uuid UUID of player to check
     * @return True if player is on this team
     */
	public boolean isOnTeam(UUID uuid) {
		return (players.contains(uuid));
	}

    /** Check if a player is pending an invite for this team
     * @param uuid UUID of player to check
     * @return True if this player is currently pending an invite for this team.
     */
	public boolean isPending(UUID uuid) {
		return (pending.contains(uuid));
	}

    /** Get the players on this team
     * @return List of UUIDs of players on this team
     */
	public List<UUID> getPlayers() {
		return players;
	}

    /** Get the pending players on this team
     * @return List of UUIDs of players pending to be on this team
     */
	public List<UUID> getPenders() {
		return pending;
	}

    /** Get the leader of this team
     * @return UUID of player who is leading this team
     */
	public UUID getLeader() {
		return leader;
	}

    /** Send a message to all members of this team
     * @param message Message to send
     */
	public void messageMembers(String message) {
	    for (UUID uuid : this.players) {
	        Player player = Bukkit.getPlayer(uuid);
	        if (player != null) {
	            Util.scm(player, message);
            }
        }
    }

    @Override
    public String toString() {
        return "Team{" +
                "leader=" + leader +
                ", players=" + players +
                ", pending=" + pending +
                '}';
    }

}
