package tk.shanebee.hg.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

/**
 * General team handler
 */
public class Team {

	private UUID leader;
	private List<UUID> players = new ArrayList<>();
	private List<UUID> pending = new ArrayList<>();
	
	public Team(UUID leader) {
		this.leader = leader;
		players.add(leader);
	}

    /** Invite a player to this team
     * <p>This will send the player a message inviting them to the team</p>
     * @param player Player to invite
     */
	public void invite(Player player) {
		Util.scm(player, HG.getPlugin().getLang().team_invite_1);
		Util.scm(player, HG.getPlugin().getLang().team_invite_2.replace("<inviter>", leader.toString()));
		Util.scm(player, HG.getPlugin().getLang().team_invite_3);
		Util.scm(player, HG.getPlugin().getLang().team_invite_4);
		pending.add(player.getUniqueId());
	}

    /** Accept the invite to this team
     * @param player Player to force to accept the invite
     */
	public void acceptInvite(Player player) {
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
}
