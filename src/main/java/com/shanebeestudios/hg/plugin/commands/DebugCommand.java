package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class DebugCommand extends SubCommand {

    public DebugCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("debug")
            .withPermission(Permissions.COMMAND_DEBUG.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    Game game = CustomArg.getGame(info);
                    this.gameManager.checkGame(game, info.sender());
                }));
    }

}
