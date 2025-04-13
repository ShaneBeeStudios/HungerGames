package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.data.KitData;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
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
                    Player player = (Player) info.sender();
                    Game game = this.plugin.getPlayerManager().getGame(player);
                    if (game == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    Status status = game.getGameArenaData().getStatus();
                    if (status != Status.WAITING && status != Status.COUNTDOWN) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return CompletableFuture.supplyAsync(() -> game.getGameItemData().getKitData().getKitNameList(player));
                }))
                .executesPlayer(info -> {
                    Player player = info.sender();
                    Game game = this.plugin.getPlayerManager().getGame(player);
                    // Invalid game
                    if (game == null) {
                        Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                        return;
                    }
                    String kitName = info.args().getByClass("kit_name", String.class);
                    // Invalid kit name
                    if (kitName == null) {
                        Util.sendPrefixedMessage(player, this.lang.command_kit_invalid_name);
                        return;
                    }
                    Status status = game.getGameArenaData().getStatus();
                    // Can't get a kit right now
                    if (status != Status.WAITING && status != Status.COUNTDOWN) {
                        Util.sendPrefixedMessage(player, this.lang.command_kit_game_running);
                        return;
                    }
                    KitData kitData = game.getGameItemData().getKitData();
                    // No permission
                    if (!kitData.hasKitPermission(player, kitName)) {
                        Util.sendPrefixedMessage(player, this.lang.command_kit_no_permission);
                        return;
                    }
                    // Set kit
                    kitData.setKit(player, kitName);
                }));
    }

}
