package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

public class SpectateCommand extends SubCommand {

    public SpectateCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("spectate")
            .withPermission(Permissions.COMMAND_SPECTATE.permission())
            .then(CustomArg.GAME.get("game")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    if (this.playerManager.hasPlayerData(player) || this.playerManager.hasSpectatorData(player)) {
                        Util.sendMessage(player, this.lang.command_join_already_in_game);
                    } else {
                        Game game = info.args().getByClass("game", Game.class);
                        assert game != null;
                        GameArenaData arenaData = game.getGameArenaData();
                        Status status = arenaData.getStatus();
                        if (status == Status.RUNNING || status == Status.FREE_ROAM) {
                            game.getGamePlayerData().spectate(player);
                        } else {
                            Util.sendPrefixedMessage(player, this.lang.listener_not_running);
                            Util.sendMessage(player, this.lang.command_base_status
                                .replace("<arena>", arenaData.getName())
                                .replace("<status>", status.getStringName()));
                        }
                    }
                }));
    }

}
