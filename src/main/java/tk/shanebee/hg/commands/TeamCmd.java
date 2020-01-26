package tk.shanebee.hg.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerData;
import tk.shanebee.hg.game.Team;
import tk.shanebee.hg.util.Util;

public class TeamCmd extends BaseCmd {

	public TeamCmd() {
		forcePlayer = true;
		cmdName = "team";
		forceInGame = true;
		argLength = 2;
		usage = "<invite/accept/tp>";
	}

	@Override
	public boolean run() {
        PlayerData pd = playerManager.getPlayerData(player.getUniqueId());
		Game g = pd.getGame();

		if (args[1].equalsIgnoreCase("invite")) {

			if (args.length >= 3) {

				Player p = Bukkit.getPlayer(args[2]);

				if (p == null || !g.getPlayers().contains(p.getUniqueId())) {
					Util.scm(player, HG.getPlugin().getLang().cmd_team_not_avail.replace("<player>", args[2]));
					return true;
				}
				if (p == player) {
				    Util.scm(player, lang.cmd_team_self);
				    return true;
                }

				if (pd.getTeam() != null) {

					Team team = pd.getTeam();

					if (!team.getLeader().equals(player.getUniqueId())) {
						Util.scm(player, HG.getPlugin().getLang().cmd_team_only_leader);
						return true;
					}
					if (team.isOnTeam(p.getUniqueId())) {
						Util.scm(player, HG.getPlugin().getLang().cmd_team_on_team.replace("<player>", args[2]));
						return true;
					}

					if ((team.getPlayers().size() + team.getPenders().size()) >= Config.maxTeam) {
						Util.scm(player, HG.getPlugin().getLang().cmd_team_max);
						return true;
					}

					pd.setTeam(team);
					team.invite(p);
					Util.scm(player, HG.getPlugin().getLang().cmd_team_invited.replace("<player>", p.getName()));
					return true;
				}

				if (pd.isOnTeam(p.getUniqueId())) {
					Util.scm(player, HG.getPlugin().getLang().cmd_team_on_team.replace("<player>", args[2]));
					return true;
				}

				Team team = new Team(player.getUniqueId());
				pd.setTeam(team);
				pd.setTeam(team);
				team.invite(p);
				Util.scm(player, HG.getPlugin().getLang().cmd_team_invited.replace("<player>", p.getName()));
				return true;
			} else {
				Util.scm(player, HG.getPlugin().getLang().cmd_team_wrong);
			}
		} else if (args[1].equalsIgnoreCase("accept")) {

            Team team = pd.getTeam();

            if (team == null) {
                Util.scm(player, HG.getPlugin().getLang().cmd_team_no_pend);
                return true;
            }
            if (team.getPenders().contains(player.getUniqueId())) {

                team.acceptInvite(player);
                for (UUID u : team.getPlayers()) {
                    Player p = Bukkit.getPlayer(u);

                    if (p != null) {
                        Util.scm(p, "&6*&b&m                                                                             &6*");
                        Util.scm(p, HG.getPlugin().getLang().cmd_team_joined.replace("<player>", player.getName()));
                        Util.scm(p, "&6*&b&m                                                                             &6*");
                    }
                    return true;
                }

                return true;
            }
        } else if (args[1].equalsIgnoreCase("tp")) {
		    if (!player.hasPermission("hg.team.tp")) {
		        Util.scm(player, lang.cmd_base_noperm.replace("<command>", "team tp"));
		        return true;
            }
		    Team team = pd.getTeam();
		    if (team == null) {
		        Util.scm(player, lang.cmd_team_no_team);
                return true;
            }
		    if (args.length >= 3) {
		        Player tpTo = Bukkit.getPlayer(args[2]);
		        if (tpTo != null && team.isOnTeam(tpTo.getUniqueId())) {
		            player.teleport(tpTo);
		            Util.scm(player, lang.cmd_team_tp.replace("<player>", tpTo.getName()));
                } else {
		            Util.scm(player, lang.cmd_team_not_on_team.replace("<player>", args[2]));
                }
		    } else {
		        Util.scm(player, "&cWrong usage: " + sendHelpLine().replace("invite/accept/", "") + " <&rplayer&7>");
            }
		} else {
			Util.scm(player, "&c" + args[1] + " is not a valid command!");
            Util.scm(sender, sendHelpLine());
		}
		return true;
	}
}