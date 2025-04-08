package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.api.game.Game;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetExitCommand extends SubCommand {

    public SetExitCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("setexit")
            .withPermission(Permissions.COMMAND_SET_EXIT.permission())
            .then(LiteralArgument.literal("all")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    this.gameManager.getGames().forEach(game -> setExit(game, player));
                    Util.sendPrefixedMessage(player, this.lang.command_exit_set_all);
                }))
            .then(LiteralArgument.literal("global")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    setExit(null, player);
                    Util.sendPrefixedMessage(player, this.lang.command_exit_set_global);
                }))
            .then(CustomArg.GAME.get("game")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    Game game = CustomArg.getGame(info);
                    setExit(game, player);
                    Util.sendPrefixedMessage(player, this.lang.command_exit_set_arena.replace("<arena>", game.getGameArenaData().getName()));
                }));
    }

    private void setExit(Game game, Player player) {
        Location location = player.getLocation().clone();
        if (game == null) {
            this.gameManager.setGlobalExitLocation(location);
        } else {
            game.getGameArenaData().setExitLocation(location);
            this.plugin.getArenaConfig().saveGameToConfig(game);
        }
    }

}
