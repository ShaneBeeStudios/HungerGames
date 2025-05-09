package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class ForceStartCommand extends SubCommand {

    public ForceStartCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("forcestart")
            .withPermission(Permissions.COMMAND_FORCE_START.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    CommandSender sender = info.sender();
                    CommandArguments args = info.args();
                    Game game = args.getByClass("game", Game.class);
                    assert game != null;
                    Status status = game.getGameArenaData().getStatus();
                    String name = game.getGameArenaData().getName();
                    if (status == Status.READY) {
                        Util.sendPrefixedMessage(sender, this.lang.command_force_start_no_players);
                    } else if (status == Status.WAITING) {
                        game.startPreGameCountdown();
                        Util.sendPrefixedMessage(sender, this.lang.command_force_start_starting.replace("<arena>", name));
                    } else if (status == Status.COUNTDOWN) {
                        game.getStartingTask().stop();
                        game.startFreeRoam();
                        Util.sendPrefixedMessage(sender, this.lang.command_force_start_starting.replace("<arena>", name));
                    } else if (status == Status.FREE_ROAM || status == Status.RUNNING){
                        Util.sendPrefixedMessage(sender, this.lang.command_force_start_game_already_running);
                    } else {
                        Util.sendPrefixedMessage(sender, this.lang.command_force_start_cannot_start);
                    }
                }));
    }

}
