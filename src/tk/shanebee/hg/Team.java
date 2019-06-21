package tk.shanebee.hg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Team {

	private UUID leader;
	private List<UUID> players = new ArrayList<>();
	private List<UUID> pending = new ArrayList<>();
	
	public Team(UUID leader) {
		this.leader = leader;
		players.add(leader);
	}
	
	
	public void invite(Player p) {
		Util.scm(p, HG.plugin.lang.team_invite_1);
		Util.scm(p, HG.plugin.lang.team_invite_2.replace("<inviter>", leader.toString()));
		Util.scm(p, HG.plugin.lang.team_invite_3);
		Util.scm(p, HG.plugin.lang.team_invite_4);
		pending.add(p.getUniqueId());
	}
	
	public void acceptInvite(Player p) {
		pending.remove(p.getUniqueId());
		players.add(p.getUniqueId());
		Util.msg(p, HG.plugin.lang.joined_team);
	}
	
	public boolean isOnTeam(UUID p) {
		return (players.contains(p));
	}
	
	public boolean isPending(UUID p) {
		return (pending.contains(p));
	}
	
	public List<UUID> getPlayers() {
		return players;
	}
	
	public List<UUID> getPenders() {
		return pending;
	}
	
	public UUID getLeader() {
		return leader;
	}
}
