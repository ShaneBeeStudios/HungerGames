package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.configs.Config;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class SettingsCommand extends SubCommand {

    public SettingsCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("settings")
            .withPermission(Permissions.COMMAND_SETTINGS.permission())
            .then(debug());
    }

    private Argument<?> debug() {
        return LiteralArgument.literal("debug")
            .then(new StringArgument("enable")
                .setOptional(true)
                .includeSuggestions(ArgumentSuggestions.stringsWithTooltips(
                    List.of(
                        BukkitStringTooltip.ofString("enable", "Enable the debug setting"),
                        BukkitStringTooltip.ofString("disable", "Disable the debug setting"))))
                .executes(info -> {
                    CommandSender sender = info.sender();
                    Optional<String> enable = info.args().getOptionalByClass("enable", String.class);
                    if (enable.isPresent()) {
                        String s = enable.get();
                        if (s.equalsIgnoreCase("enable")) {
                            Config.SETTINGS_DEBUG = true;
                            Util.sendPrefixedMessage(sender, "The debug setting has been <green>enabled<grey>!");
                        } else if (s.equalsIgnoreCase("disable")) {
                            Config.SETTINGS_DEBUG = false;
                            Util.sendPrefixedMessage(sender, "The debug setting has been <red>disabled<grey>!");
                        } else {
                            Util.sendPrefixedMessage(sender, "Invalid option '%s'", s);
                        }
                    } else {
                        String setting = Config.SETTINGS_DEBUG ? "<green>enabled" : "<red>disabled";
                        Util.sendPrefixedMessage(sender, "The debug setting is %s", setting);
                    }
                }));
    }

}
