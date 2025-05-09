package com.shanebeestudios.hg.plugin.commands;

import com.shanebeestudios.hg.api.util.Util;
import com.shanebeestudios.hg.plugin.HungerGames;
import com.shanebeestudios.hg.plugin.permission.Permissions;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(HungerGames plugin) {
        super(plugin);
    }

    @Override
    protected Argument<?> register() {
        return LiteralArgument.literal("reload")
            .withPermission(Permissions.COMMAND_RELOAD.permission())
            .then(LiteralArgument.literal("confirm")
                .executes(info -> {
                    reload(info.sender());
                }))
            .then(LiteralArgument.literal("cancel")
                .executes(info -> {
                    Util.sendPrefixedMessage(info.sender(), "<red>Reload cancelled");
                }))
            .executes(info -> {
                CommandSender sender = info.sender();
                if (gamesNotRunning(sender)) {
                    reload(sender);
                }
            });
    }

    private void reload(CommandSender sender) {
        long start = System.currentTimeMillis();
        Util.sendPrefixedMessage(sender, "<gold>Reloading plugin... observe console for errors!");
        this.plugin.reloadPlugin();
        Util.sendPrefixedMessage(sender, "<grey>Reloaded <green>successfully <grey>in <aqua>" +
            (System.currentTimeMillis() - start) + "<grey> milliseconds");
    }

    public boolean gamesNotRunning(CommandSender sender) {
        int running = this.gameManager.gamesRunning();
        if (running > 0) {
            Util.sendPrefixedMessage(sender, "<gold>There are still <aqua>" + running + "<gold> games running.");
            Util.sendMessage(sender, "<gold>Do you wish to stop all games and reload?");

            if (sender instanceof Player) {
                Component yes = clickableCommand("<green>YES", "/hg reload confirm", lines -> {
                    lines.add("<grey>Click <green>YES");
                    lines.add("<grey>to stop all games");
                    lines.add("<grey>and reload");
                });

                Component space = Util.getMini(" <grey>or ");
                Component no = clickableCommand("<red>NO", "/hg reload cancel", lines -> {
                    lines.add("<grey>Click <red>NO");
                    lines.add("<grey>to cancel reload");
                });
                TextComponent msg = Component.text().append(yes).append(space).append(no).build();
                sender.sendMessage(msg);
            } else {
                Util.log("<gold>Type <aqua>hg reload confirm <gold>to force reload.");
            }
            return false;
        }
        return true;
    }

    private Component clickableCommand(@NotNull String message, @NotNull String command, Consumer<List<String>> hover) {
        Component msg = Util.getMini(message);

        msg = msg.clickEvent(ClickEvent.runCommand(command));
        if (hover != null) {
            List<String> hovers = new ArrayList<>();
            hover.accept(hovers);
            StringJoiner joiner = new StringJoiner("<newline>");
            hovers.forEach(joiner::add);
            Component mini = Util.getMini(joiner.toString());

            msg = msg.hoverEvent(HoverEvent.showText(mini));
        }
        return msg;
    }

}
