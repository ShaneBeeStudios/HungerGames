package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsCommand extends SubCommand {

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("permissions")
            .withPermission(Permissions.COMMAND_PERMISSIONS.permission())
            .executes(info -> {
                CommandSender sender = info.sender();
                if (sender instanceof Player) {
                    Util.sendPrefixedMessage(sender, "Permissions sent to console.");
                }
                Permissions.debug();
            });
    }

}
