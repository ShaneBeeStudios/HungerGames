package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.data.Language;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;

public class ToggleCommand extends SubCommand {

    private final Language lang;

    public ToggleCommand(HungerGames plugin) {
        this.lang = plugin.getLang();
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("toggle")
            .withPermission(Permissions.COMMAND_TOGGLE.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    CommandSender sender = info.sender();
                    Game game = info.args().getByClass("game", Game.class);
                    if (game != null) {
                        GameArenaData arenaData = game.getGameArenaData();
                        if (arenaData.getStatus() == Status.NOT_READY || arenaData.getStatus() == Status.BROKEN) {
                            arenaData.setStatus(Status.READY);
                            Util.sendMessage(sender, this.lang.cmd_toggle_unlocked.replace("<arena>", arenaData.getName()));
                        } else {
                            game.stop(false);
                            arenaData.setStatus(Status.NOT_READY);
                            Util.sendMessage(sender, this.lang.cmd_toggle_locked.replace("<arena>", arenaData.getName()));
                        }
                    } else {
                        String name = info.args().getOrDefaultRaw("game", "huh?");
                        Util.sendMessage(sender, this.lang.cmd_delete_noexist.replace("<arena>", name));
                    }
                }));
    }

}
