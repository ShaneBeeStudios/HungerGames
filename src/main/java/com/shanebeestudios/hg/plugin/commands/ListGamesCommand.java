package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;

public class ListGamesCommand extends SubCommand {

    private final HungerGames plugin;

    public ListGamesCommand(HungerGames plugin) {
        this.plugin = plugin;
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("listgames")
            .executes(info -> {
                CommandSender sender = info.sender();
                Util.sendPrefixedMessage(sender, "<gold><bold>Games:");
                for (Game game : this.plugin.getGameManager().getGames()) {
                    GameArenaData gameArenaData = game.getGameArenaData();
                    Util.sendMessage(sender, " <grey> - <aqua>" + gameArenaData.getName() + " <white>: " + gameArenaData.getStatus().getStringName());
                }
            });
    }

}
