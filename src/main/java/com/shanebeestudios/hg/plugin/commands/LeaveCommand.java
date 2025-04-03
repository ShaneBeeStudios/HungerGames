package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.status.Status;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.util.Vault;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.entity.Player;

public class LeaveCommand extends SubCommand {

    public LeaveCommand(HungerGames plugin) {
        super(plugin);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("leave")
            .withPermission(Permissions.COMMAND_LEAVE.permission())
            .executesPlayer(info -> {
                Player player = info.sender();
                Game game;
                if (this.playerManager.hasPlayerData(player)) {
                    game = this.playerManager.getPlayerData(player).getGame();
                    if (Config.economy) {
                        GameArenaData gameArenaData = game.getGameArenaData();
                        Status status = gameArenaData.getStatus();
                        int cost = gameArenaData.getCost();
                        if ((status == Status.WAITING || status == Status.COUNTDOWN) && cost > 0) {
                            Vault.economy.depositPlayer(player, cost);
                            Util.sendMessage(player, this.lang.command_leave_refund.replace("<cost>", String.valueOf(cost)));
                        }
                    }
                    game.getGamePlayerData().leaveGame(player, false);
                } else if (this.playerManager.hasSpectatorData(player)) {
                    game = playerManager.getSpectatorData(player).getGame();
                    game.getGamePlayerData().leaveSpectate(player);
                } else {
                    Util.sendMessage(player, this.lang.command_base_not_in_valid_game);
                    return;
                }
                Util.sendMessage(player, this.lang.command_leave_left.replace("<arena>", game.getGameArenaData().getName()));
            });
    }

}
