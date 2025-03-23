package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.Game;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class KitCommand extends SubCommand {
    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("kit")
            .then(new StringArgument("kit_name")
                .includeSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> {
                    Game game = HungerGames.getPlugin().getPlayerManager().getGame(((Player) info.sender()));
                    if (game == null) return CompletableFuture.completedFuture(null);
                    return CompletableFuture.supplyAsync(() -> game.getKitManager().getKitList());
                }))
                .executesPlayer(info -> {
                    Player player = info.sender();
                    Game game = HungerGames.getPlugin().getPlayerManager().getGame(player);
                    if (game != null) {
                        String kitName = info.args().getByClass("kit_name", String.class);
                        if (kitName != null) {
                            Status status = game.getGameArenaData().getStatus();
                            if (status == Status.WAITING || status == Status.COUNTDOWN) {
                                game.getKitManager().setKit(player, kitName);
                            } else {
                                // TODO no kit for you
                            }
                        } else {
                            // tODO invalid kit
                        }
                    } else {
                        // TODO msg not in game
                    }
                }));
    }
}
