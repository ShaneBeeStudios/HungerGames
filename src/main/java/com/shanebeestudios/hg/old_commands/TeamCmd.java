package com.shanebeestudios.hg.old_commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GamePlayerData;
import com.shanebeestudios.hg.game.Team;
import com.shanebeestudios.hg.util.Util;

public class TeamCmd extends BaseCmd {

    public TeamCmd() {
        forcePlayer = true;
        cmdName = "team";
        forceInGame = true;
        argLength = 2;
        usage = "<create/invite/accept/tp>";
    }

    @Override
    public boolean run() {
        PlayerData pd = playerManager.getPlayerData(player.getUniqueId());
        Game game = pd.getGame();

        if (args[1].equalsIgnoreCase("create")) {
            Team team = pd.getTeam();
            if (team == null) {
                String teamName = args.length == 3 ? args[2] : player.getName();
                GamePlayerData gamePlayerData = game.getGamePlayerData();
                if (gamePlayerData.hasTeam(teamName)) {
                    String exists = lang.team_already_exists.replace("<name>", teamName);
                    Util.scm(player, exists);
                    return true;
                }
                team = new Team(player, teamName, game);
                gamePlayerData.addTeam(team);
                String created = lang.team_created.replace("<name>", team.getName());
                Util.scm(player, created);
            } else {
                String exists = lang.team_already_have.replace("<name>", team.getName());
                Util.scm(player, exists);
            }
        } else if (args[1].equalsIgnoreCase("invite")) {
            if (args.length >= 3) {
                Team team = pd.getTeam();
                if (team == null) {
                    Util.scm(player, lang.team_none);
                    return true;
                }
                Player invitee = Bukkit.getPlayer(args[2]);

                if (invitee == null || !game.getGamePlayerData().getPlayers().contains(invitee.getUniqueId())) {
                    Util.scm(player, lang.cmd_team_not_avail.replace("<player>", args[2]));
                    return true;
                }
                if (invitee == player) {
                    Util.scm(player, lang.cmd_team_self);
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    Util.scm(player, lang.cmd_team_only_leader);
                    return true;
                }
                if (team.isOnTeam(invitee.getUniqueId())) {
                    Util.scm(player, lang.cmd_team_on_team.replace("<player>", args[2]));
                    return true;
                }
                if ((team.getPlayers().size() + team.getPenders().size()) >= Config.team_maxTeamSize) {
                    Util.scm(player, lang.cmd_team_max);
                    return true;
                }
                team.invite(invitee);
                Util.scm(player, lang.cmd_team_invited.replace("<player>", invitee.getName()));
                return true;

            } else {
                Util.scm(player, lang.cmd_team_wrong);
            }
        } else if (args[1].equalsIgnoreCase("accept")) {
            Team team = pd.getPendingTeam();

            if (team == null) {
                Util.scm(player, lang.cmd_team_no_pend);
                return true;
            }
            if (team.isPending(player.getUniqueId())) {

                team.acceptInvite(player);
                team.messageMembers("&6*&b&m                                                                             &6*");
                team.messageMembers(lang.cmd_team_joined.replace("<player>", player.getName()));
                team.messageMembers("&6*&b&m                                                                             &6*");
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
