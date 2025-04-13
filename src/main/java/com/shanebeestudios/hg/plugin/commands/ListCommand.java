package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class ListCommand extends SubCommand {

    public ListCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("list")
            .withPermission(Permissions.COMMAND_LIST.permission())
            .executesPlayer(info -> {
                Player player = info.sender();
                Game game = this.playerManager.getGame(player);
                if (game == null) {
                    Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                    return;
                }

                List<Player> players = game.getGamePlayerData().getPlayers();
                StringJoiner joiner = new StringJoiner(this.lang.command_list_players_delimiter);
                if (players.isEmpty()) {
                    joiner.add("<red>none");
                } else {
                    players.forEach(p -> joiner.add(p.getName()));
                }
                Util.sendPrefixedMessage(player, this.lang.command_list_players
                    .replace("<arena>", game.getGameArenaData().getName())
                    .replace("<players>", joiner.toString()));
            }).then(CustomArg.GAME.get("game")
                .withPermission(Permissions.COMMAND_LIST_GAME.permission())
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    assert game != null;
                    List<Player> players = game.getGamePlayerData().getPlayers();
                    StringJoiner joiner = new StringJoiner(this.lang.command_list_players_delimiter);
                    if (players.isEmpty()) {
                        joiner.add("<red>none");
                    } else {
                        players.forEach(p -> joiner.add(p.getName()));
                    }
                    Util.sendPrefixedMessage(info.sender(), this.lang.command_list_players
                        .replace("<arena>", game.getGameArenaData().getName())
                        .replace("<players>", joiner.toString()));
                }));
    }

}
