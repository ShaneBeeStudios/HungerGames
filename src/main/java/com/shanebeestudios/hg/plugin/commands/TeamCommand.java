package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.data.PlayerData;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameTeam;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

public class TeamCommand extends SubCommand {

    public TeamCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("team")
            .withPermission(Permissions.COMMAND_TEAM.permission())
            .then(LiteralArgument.literal("create")
                .then(new StringArgument("name")
                    .executesPlayer(info -> {
                        Player player = info.sender();
                        PlayerData playerData = this.playerManager.getPlayerData(player);
                        if (playerData == null) {
                            Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                            return;
                        }
                        GameTeam gameTeam = playerData.getTeam();
                        if (gameTeam != null) {
                            String exists = this.lang.command_team_already_have.replace("<name>", gameTeam.getTeamName());
                            Util.sendMessage(player, exists);
                        } else {
                            String teamName = info.args().getByClass("name", String.class);
                            assert teamName != null;

                            Game game = playerData.getGame();
                            if (game.getGameScoreboard().hasGameTeam(teamName)) {
                                String exists = this.lang.command_team_already_exists.replace("<name>", teamName);
                                Util.sendMessage(player, exists);
                                return;
                            }
                            game.getGameScoreboard().createGameTeam(player, teamName);
                            String created = this.lang.command_team_created.replace("<name>", teamName);
                            Util.sendMessage(player, created);
                        }
                    })))
            .then(LiteralArgument.literal("invite")
                .then(new EntitySelectorArgument.OnePlayer("player")
                    .executesPlayer(info -> {
                        Player teamLeader = info.sender();
                        PlayerData playerData = this.playerManager.getPlayerData(teamLeader);
                        if (playerData == null) {
                            // Not in a game
                            Util.sendPrefixedMessage(teamLeader, this.lang.command_base_not_in_valid_game);
                            return;
                        }

                        GameTeam gameTeam = playerData.getTeam();
                        if (gameTeam == null) {
                            // Don't current have a team
                            Util.sendMessage(teamLeader, this.lang.command_team_none);
                            return;
                        }

                        Game game = playerData.getGame();
                        Player invitee = info.args().getByClass("player", Player.class);
                        assert invitee != null;
                        if (!game.getGamePlayerData().getPlayers().contains(invitee)) {
                            // Invitee is not in game
                            Util.sendMessage(teamLeader, this.lang.command_team_player_not_available.replace("<player>", teamLeader.getName()));
                            return;
                        }

                        if (invitee == teamLeader) {
                            // Cannot invite yourself to a team
                            Util.sendMessage(teamLeader, this.lang.command_team_self);
                            return;
                        }
                        if (gameTeam.getLeader() != teamLeader) {
                            // Only team leader can invite others
                            Util.sendMessage(teamLeader, this.lang.command_team_only_leader);
                            return;
                        }
                        if (gameTeam.isOnTeam(invitee)) {
                            // Invite is already on a team
                            Util.sendMessage(teamLeader, this.lang.command_team_on_team.replace("<player>", invitee.getName()));
                            return;
                        }
                        if ((gameTeam.getPlayers().size() + gameTeam.getPendingPlayers().size()) >= Config.team_maxTeamSize) {
                            // Size check
                            Util.sendMessage(teamLeader, this.lang.command_team_max);
                            return;
                        }
                        // Finally invite player to team
                        gameTeam.invite(invitee);
                        Util.sendMessage(teamLeader, this.lang.command_team_invited.replace("<player>", invitee.getName()));
                    })))
            .then(LiteralArgument.literal("accept")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    PlayerData playerData = this.playerManager.getPlayerData(player);
                    if (playerData == null) {
                        // Not in a game
                        Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                        return;
                    }
                    GameTeam gameTeam = playerData.getPendingTeam();
                    if (gameTeam == null || !gameTeam.isPending(player)) {
                        // No team currently pending an invitation
                        Util.sendMessage(player, this.lang.command_team_no_pend);
                        return;
                    }
                    gameTeam.acceptInvite(player);
                    gameTeam.messageMembers("<gold>*<aqua><strikethrough>                                                                             <gold>*");
                    gameTeam.messageMembers(this.lang.command_team_joined.replace("<player>", player.getName()));
                    gameTeam.messageMembers("<gold>*<aqua><strikethrough>                                                                             <gold>*");
                }))
            .then(LiteralArgument.literal("deny")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    PlayerData playerData = this.playerManager.getPlayerData(player);
                    if (playerData == null) {
                        Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                        return;
                    }
                    GameTeam gameTeam = playerData.getPendingTeam();
                    if (gameTeam == null || !gameTeam.isPending(player)) {
                        // No team currently pending an invitation
                        Util.sendMessage(player, this.lang.command_team_no_pend);
                        return;
                    }
                    gameTeam.declineInvite(player);
                }))
            .then(LiteralArgument.literal("teleport")
                .withPermission(Permissions.COMMAND_TEAM_TELEPORT.permission())
                .then(new EntitySelectorArgument.OnePlayer("player")
                    .executesPlayer(info -> {
                        Player player = info.sender();
                        PlayerData playerData = this.playerManager.getPlayerData(player);
                        if (playerData == null) {
                            Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                            return;
                        }

                        GameTeam gameTeam = playerData.getTeam();
                        if (gameTeam == null) {
                            Util.sendMessage(player, this.lang.command_team_none);
                            return;
                        }
                        Player tpTo = info.args().getByClass("player", Player.class);
                        assert tpTo != null;
                        if (gameTeam.isOnTeam(tpTo)) {
                            player.teleport(tpTo);
                            Util.sendMessage(player, this.lang.command_team_tp.replace("<player>", tpTo.getName()));
                        } else {
                            Util.sendMessage(player, this.lang.command_team_not_on_team.replace("<player>", tpTo.getName()));
                        }
                    })));
    }

}
