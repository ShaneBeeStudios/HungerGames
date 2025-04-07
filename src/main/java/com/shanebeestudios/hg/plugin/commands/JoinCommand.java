package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class JoinCommand extends SubCommand {

    public JoinCommand(HungerGames plugin) {
        super(plugin);
    }

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
                        Player sender = info.sender();
                        CommandArguments args = info.args();
                        Game game = args.getByClass("game", Game.class);
                        if (game != null) {
                            Collection<Player> players = (Collection<Player>) args.getByClass("players", Collection.class);
                            if (players != null) {
                                players.forEach(player -> joinGame(sender, player, game));
                            } else {
                                joinGame(sender, sender, game);
                            }
                        } else {
                            Util.sendPrefixedMessage(sender, "Invalid game: %s", args.getRaw("game"));
                        }
                    })));
    }

    private void joinGame(CommandSender sender, Player player, Game game) {
        if (this.playerManager.isInGame(player)) {
            if (sender == player) {
                Util.sendPrefixedMessage(sender, this.lang.command_join_already_in_game);
            } else {
                Util.sendPrefixedMessage(sender, this.lang.command_join_already_in_game_other.replace("<player>", player.getName()));
            }
            return;
        }
        game.joinGame(player, true);
    }

}
