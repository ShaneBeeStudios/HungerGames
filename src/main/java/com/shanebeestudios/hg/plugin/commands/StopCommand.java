package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class StopCommand extends SubCommand {

    public StopCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("stop")
            .withPermission(Permissions.COMMAND_STOP.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    if (game != null) {
                        game.stop();
                    }

                }));
    }

}
