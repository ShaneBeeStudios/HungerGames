package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.HungerGames;
import com.shanebeestudios.hg.plugin.tasks.ChestDropChestTask;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ChestDropCommand extends SubCommand {

    public ChestDropCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("chest_drop")
            .executesPlayer(info -> {
                Player player = info.sender();
                Location location = player.getLocation().getBlock().getLocation();
                new ChestDropChestTask(location);
            });
    }

}
