package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;

public class StatusCommand extends SubCommand {

    public StatusCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("status")
            .withPermission(Permissions.COMMAND_STATUS.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    Game game = info.args().getByClass("game", Game.class);
                    CommandSender sender = info.sender();
                    if (game != null) {
                        GameArenaData arenaData = game.getGameArenaData();
                        Util.sendPrefixedMessage(sender, this.lang.command_base_status
                            .replace("<arena>", arenaData.getName())
                            .replace("<status>", arenaData.getStatus().getStringName()));

                    } else {
                        String name = info.args().getOrDefaultRaw("game", "huh?");
                        Util.sendMessage(sender, this.lang.command_delete_no_exist.replace("<arena>", name));
                    }
                }));
    }

}
