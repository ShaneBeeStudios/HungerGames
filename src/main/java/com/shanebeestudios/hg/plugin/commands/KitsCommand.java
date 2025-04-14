package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.gui.KitsGUI;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

public class KitsCommand extends SubCommand {

    public KitsCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("kits")
            .withPermission(Permissions.COMMAND_KIT.permission())
            .executesPlayer(info -> {
                Player player = info.sender();
                Game game = this.plugin.getPlayerManager().getGame(player);
                // Invalid game
                if (game == null) {
                    Util.sendPrefixedMessage(player, this.lang.command_base_not_in_valid_game);
                    return;
                }
                Status status = game.getGameArenaData().getStatus();
                // Can't get a kit right now
                if (status != Status.WAITING && status != Status.COUNTDOWN) {
                    Util.sendPrefixedMessage(player, this.lang.command_kit_game_running);
                    return;
                }
                KitsGUI kitsGUI = new KitsGUI(game, player);
                kitsGUI.open();
            });
    }

}
