package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;

public class RefillChestNowCommand extends SubCommand {

    public RefillChestNowCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("refill_chests_now")
            .withPermission(Permissions.COMMAND_REFILL_CHESTS.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    CommandSender sender = info.sender();
                    Game game = info.args().getByClass("game", Game.class);
                    assert game != null;
                    if (game.getGameArenaData().getStatus() != Status.RUNNING) {
                        Util.sendMessage(sender, this.lang.listener_not_running);
                        return;
                    }
                    game.getGameBlockData().markChestForRefill();
                    Util.sendMessage(sender, this.lang.command_chest_refill_now.replace("<arena>", game.getGameArenaData().getName()));
                }));
    }

}
