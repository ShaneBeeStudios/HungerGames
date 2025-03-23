package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import com.shanebeestudios.hg.util.Util;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

import java.util.Collection;

public class JoinCommand extends SubCommand {
    @SuppressWarnings("unchecked")
    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("join")
            .withPermission(Permissions.COMMAND_JOIN.permission())
            .then(CustomArg.GAME.get("game")
                .then(new EntitySelectorArgument.ManyPlayers("players")
                    .setOptional(true)
                    .withPermission(Permissions.COMMAND_JOIN_OTHERS.permission())
                    .executesPlayer(info -> {
                        Player player = info.sender();
                        CommandArguments args = info.args();
                        Game game = args.getByClass("game", Game.class);
                        if (game != null) {
                            Collection<Player> players = (Collection<Player>) args.getByClass("players", Collection.class);
                            if (players != null) {
                                players.forEach(p -> game.joinGame(p, true));
                            } else {
                                game.joinGame(player, true);
                            }
                        } else {
                            Util.sendPrefixedMini(player, "Invalid game: %s", args.getRaw("game"));
                        }
                    })));
    }

}
