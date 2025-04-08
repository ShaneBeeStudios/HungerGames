package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

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

                StringJoiner joiner = new StringJoiner(this.lang.command_list_players_delimiter);
                game.getGamePlayerData().getPlayers().forEach(p -> joiner.add(p.getName()));
                Util.sendPrefixedMessage(player, this.lang.command_list_players
                    .replace("<arena>", game.getGameArenaData().getName())
                    .replace("<players>", joiner.toString()));
            });
    }

}
