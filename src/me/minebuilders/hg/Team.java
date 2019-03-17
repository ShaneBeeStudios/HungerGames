package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Team {

	private UUID leader;
	private List<UUID> players = new ArrayList<UUID>();
	private List<UUID> pending = new ArrayList<UUID>();
	
	public Team(UUID leader) {
		this.leader = leader;
		players.add(leader);
	}
	
	
	public void invite(Player p) {
		Util.scm(p, "&6*&b&m                                                                             &6*");
		Util.scm(p, "| &f" + leader + " &3Just invited you to a &fTeam&3!");
		Util.scm(p, "| &3Type &f/hg team accept &3To join!");
		Util.scm(p, "&6*&b&m                                                                             &6*");
		pending.add(p.getUniqueId());
	}
	
	public void acceptInvite(Player p) {
		pending.remove(p.getUniqueId());
		players.add(p.getUniqueId());
		Util.msg(p, "&3You successfully joined this team!");
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
