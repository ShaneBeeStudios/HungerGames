package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class StopAllCommand extends SubCommand {

    public StopAllCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("stopall")
            .withPermission(Permissions.COMMAND_STOP_ALL.permission())
            .executes(info -> {
                HungerGames.getPlugin().getGameManager().getGames().forEach(Game::stop);
            });
    }

}
