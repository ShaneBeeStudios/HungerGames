package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class KitCommand extends SubCommand {

    public KitCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("kit")
            .withPermission(Permissions.COMMAND_KIT.permission())
            .then(new StringArgument("kit_name")
                .includeSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    Game game = this.plugin.getPlayerManager().getGame(((Player) info.sender()));
                    if (game == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    Status status = game.getGameArenaData().getStatus();
                    if (status != Status.WAITING && status != Status.COUNTDOWN) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return CompletableFuture.supplyAsync(() -> game.getKitManager().getKitList());
                }))
                .executesPlayer(info -> {
                    Player player = info.sender();
                    Game game = this.plugin.getPlayerManager().getGame(player);
                    if (game != null) {
                        String kitName = info.args().getByClass("kit_name", String.class);
                        if (kitName != null) {
                            Status status = game.getGameArenaData().getStatus();
                            if (status == Status.WAITING || status == Status.COUNTDOWN) {
                                game.getKitManager().setKit(player, kitName);
                            } else {
                                Util.sendPrefixedMessage(player, this.lang.command_kit_game_running);
                            }
                        } else {
                            Util.sendPrefixedMessage(player, this.lang.command_kit_invalid_name);
                        }
                    } else {
                        Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                    }
                }));
    }
}
