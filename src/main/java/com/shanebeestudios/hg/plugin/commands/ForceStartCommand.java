package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class ForceStartCommand extends SubCommand {

    public ForceStartCommand(HungerGames plugin) {
        super(plugin);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("forcestart")
            .withPermission(Permissions.COMMAND_FORCE_START.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    CommandSender sender = info.sender();
                    CommandArguments args = info.args();
                    Game game = args.getByClass("game", Game.class);
                    if (game != null) {
                        Status status = game.getGameArenaData().getStatus();
                        if (status == Status.WAITING || status == Status.READY) {
                            game.startPreGameCountdown();
                            Util.sendMessage(sender, lang.cmd_start_starting.replace("<arena>", game.getGameArenaData().getName()));
                        } else if (status == Status.COUNTDOWN) {
                            game.getStartingTask().stop();
                            game.startFreeRoam();
                            Util.sendMessage(sender, "<green>Game starting now");
                        } else {
                            Util.sendMessage(sender, "<red>Game has already started");
                        }
                    } else {
                        Util.sendPrefixedMessage(sender, this.lang.command_delete_noexist.replace("<arena>", args.getRaw("game")));
                    }
                }));
    }

}
