package me.minebuilders.hg.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Config;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerData;
import me.minebuilders.hg.Team;
import me.minebuilders.hg.Util;

public class TeamCmd extends BaseCmd {

	public TeamCmd() {
		forcePlayer = true;
		cmdName = "team";
		forceInGame = true;
		argLength = 2;
		usage = "<invite/accept>";
	}

	@Override
	public boolean run() {
		PlayerData pd = HG.plugin.players.get(player.getUniqueId());
		Game g = pd.getGame();

		if (args[1].equalsIgnoreCase("invite")) {

			if (args.length >= 3) {

				Player p = Bukkit.getPlayer(args[2]);

				if (p == null || !g.getPlayers().contains(p.getUniqueId())) {
					Util.msg(player, "&c" + args[2] + " Is not available!");
					return true;
				}

				if (pd.getTeam() != null) {

					Team t = pd.getTeam();

					if (!t.getLeader().equals(player.getUniqueId())) {
						Util.msg(player, "&cOnly the leader can invite other players!");
						return true;
					}
					if (t.isOnTeam(p.getUniqueId())) {
						Util.msg(player, "&c" + args[2] + " &6is already on a team!");
						return true;
					}

					if ((t.getPlayers().size() + t.getPenders().size()) >= Config.maxTeam) {
						Util.msg(player, "&cYou've hit the max team limit!");
						return true;
					}

					HG.plugin.players.get(p.getUniqueId()).setTeam(t);
					t.invite(p);
					Util.msg(player, "&c" + p.getName() + " &6Has been invited!");
					return true;
				}

				if (HG.plugin.players.get(p.getUniqueId()).isOnTeam(p.getUniqueId())) {
					Util.msg(player, "&c" + args[2] + " &6is already on a team!");
					return true;
				}

				Team t = new Team(player.getUniqueId());
				HG.plugin.players.get(p.getUniqueId()).setTeam(t);
				pd.setTeam(t);
				t.invite(p);
				Util.msg(player, "&c" + p.getName() + " &6Has been invited!");
				return true;
			} else {
				Util.msg(player, "&cWrong Usage: &6/hg team invite &c<name>");
			}
		} else if (args[1].equalsIgnoreCase("accept")) {

			Team t = HG.plugin.players.get(player.getUniqueId()).getTeam();

			if (t == null) {
				Util.msg(player, "&cYou have no pending invites...");
				return true;
			}
			if (t.getPenders().contains(player.getUniqueId())) {

				t.acceptInvite(player);
				for (UUID u : t.getPlayers()) {
					Player p = Bukkit.getPlayer(u);

					if (p != null) {
						Util.scm(p, "&6*&b&m                                                                             &6*");
						Util.scm(p, ChatColor.WHITE + player.getName() + " &6Just joined your team!");
						Util.scm(p, "&6*&b&m                                                                             &6*");
					}
					return true;
				}

				return true;
			}
		} else {
			Util.scm(player, "&c" + args[1] + " is not a valid command!");
			Util.scm(player, "&cValid arguments: &6invite&c, &6accept ");
		}
		return true;
	}
}