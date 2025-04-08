package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.api.game.GameArenaData;
import com.shanebeestudios.hg.api.game.GamePlayerData;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteArenaCommand extends SubCommand {

    public DeleteArenaCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("delete-arena")
            .withPermission(Permissions.COMMAND_DELETE.permission())
            .then(CustomArg.GAME.get("game")
                .executes(info -> {
                    CommandSender sender = info.sender();
                    Game game = CustomArg.getGame(info);
                    GamePlayerData gamePlayerData = game.getGamePlayerData();
                    GameArenaData gameArenaData = game.getGameArenaData();
                    String name = gameArenaData.getName();

                    try {
                        Util.sendPrefixedMessage(sender, this.lang.command_delete_attempt.replace("<arena>", name));

                        switch (gameArenaData.getStatus()) {
                            case WAITING, COUNTDOWN, FREE_ROAM, RUNNING -> {
                                Util.sendMessage(sender, this.lang.command_delete_stopping);
                                game.getGameBlockData().forceRollback();
                                game.stop(false);
                            }
                            case ROLLBACK -> {
                                Util.sendMessage(sender, this.lang.command_delete_rollback);
                                return;
                            }
                        }

                        // This shouldn't happen, why is it here?
                        if (!gamePlayerData.getPlayers().isEmpty()) {
                            Util.sendMessage(sender, this.lang.command_delete_kicking);
                            for (Player player : gamePlayerData.getPlayers()) {
                                gamePlayerData.leaveGame(player, false);
                            }
                        }

                        this.gameManager.deleteGame(game);
                        Util.sendMessage(sender, this.lang.command_delete_deleted.replace("<arena>", name));
                    } catch (Exception e) {
                        Util.sendMessage(sender, this.lang.command_delete_failed);
                        Util.sendMessage(sender, "Error Message: <red>" + e.getMessage());
                    }
                }));
    }

}
