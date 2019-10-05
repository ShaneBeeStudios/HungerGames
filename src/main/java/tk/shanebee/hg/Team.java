package tk.shanebee.hg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import tk.shanebee.hg.util.Util;

public class Team {

	private UUID leader;
	private List<UUID> players = new ArrayList<>();
	private List<UUID> pending = new ArrayList<>();
	
	public Team(UUID leader) {
		this.leader = leader;
		players.add(leader);
	}
	
	
	public void invite(Player p) {
		Util.scm(p, HG.plugin.getLang().team_invite_1);
		Util.scm(p, HG.plugin.getLang().team_invite_2.replace("<inviter>", leader.toString()));
		Util.scm(p, HG.plugin.getLang().team_invite_3);
		Util.scm(p, HG.plugin.getLang().team_invite_4);
		pending.add(p.getUniqueId());
	}
	
	public void acceptInvite(Player p) {
		pending.remove(p.getUniqueId());
		players.add(p.getUniqueId());
		Util.scm(p, HG.plugin.getLang().joined_team);
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
