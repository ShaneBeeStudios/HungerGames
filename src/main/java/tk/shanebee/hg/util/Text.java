package tk.shanebee.hg.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.hg.HG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Text {

    /**
     * Get a TextComponent with colors applied
     *
     * @param message Message to translate with colors
     * @return new TextComponent with colors
     */
    public static TextComponent message(@NotNull String message) {
        return new TextComponent(Util.getColString(message));
    }

    /**
     * Create a clickable message with a hover message
     * <p>Clicking the message will run a command</p>
     *
     * @param message Message to be clicked
     * @param command Command to run when clicked
     * @param hover   Consumer to add lines to the hover message
     * @return new clickable TextComponent
     */
    public static TextComponent clickableCommand(@NotNull String message, @NotNull String command, Consumer<List<String>> hover) {
        TextComponent msg = message(message);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        if (hover != null) {
            List<String> hovers = new ArrayList<>();
            hover.accept(hovers);
            BaseComponent[] comp = new BaseComponent[hovers.size()];
            for (int i = 0; i < hovers.size(); i++) {
                comp[i] = message(hovers.get(i) + (i == hovers.size() - 1 ? "" : "\n"));
            }
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, comp));
        }
        return msg;
    }

    /**
     * Send a TextComponent message to a player/console
     * <p>Message will be prefixed with the hunger games prefix</p>
     *
     * @param sender Whom to send message to
     * @param msgs   Messages to send
     */
    public static void sendMessage(CommandSender sender, BaseComponent... msgs) {
        TextComponent prefix = new TextComponent(Util.getColString(HG.getPlugin().getLang().prefix));
        BaseComponent[] comps = new BaseComponent[msgs.length + 1];
        comps[0] = prefix;
        System.arraycopy(msgs, 0, comps, 1, msgs.length);
        sender.spigot().sendMessage(comps);
    }
}
