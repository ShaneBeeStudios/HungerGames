package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.command.CustomArg;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
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
                    this.plugin.getGameManager().getGames().forEach(game ->
                        setExit(game, player, false));
                    Util.sendPrefixedMessage(player, this.lang.command_exit_set_all);
                }))
            .then(LiteralArgument.literal("global")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    this.plugin.getGameManager().getGames().forEach(game ->
                        setExit(game, player, true));
                    Util.sendPrefixedMessage(player, this.lang.command_exit_set_global);
                }))
            .then(CustomArg.GAME.get("game")
                .executesPlayer(info -> {
                    Player player = info.sender();
                    Game game = info.args().getByClass("game", Game.class);
                    if (game == null) {
                        String raw = info.args().getOrDefaultRaw("game", "huh?");
                        Util.sendPrefixedMessage(player, this.lang.cmd_delete_noexist.replace("<arena>", raw));
                    } else {
                        setExit(game, player, false);
                        Util.sendPrefixedMessage(player, this.lang.command_exit_set_arena.replace("<arena>", game.getGameArenaData().getName()));
                    }

                }));
    }

    private void setExit(Game game, Player player, boolean global) {
        GameArenaData gameArenaData = game.getGameArenaData();
        Location location = player.getLocation().clone();
        gameArenaData.setExit(location, !global);
        if (!global) {
            //this.plugin.getArenaConfig().saveGameToConfig(gameArenaData.getName(), game);
        } else {
            this.plugin.getArenaConfig().setGlobalExit(location);
        }
    }

}
