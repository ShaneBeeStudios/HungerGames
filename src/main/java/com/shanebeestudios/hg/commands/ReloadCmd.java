package com.shanebeestudios.hg.commands;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.util.Text;
import com.shanebeestudios.hg.util.Util;

public class ReloadCmd extends BaseCmd {

    public ReloadCmd() {
        forcePlayer = false;
        cmdName = "reload";
        argLength = 1;
        forceInRegion = false;
    }

    @Override
    public boolean run() {
        String prefix = lang.prefix;
        if (args.length == 2 && args[1].equalsIgnoreCase("cancel")) {
            Util.scm(sender, prefix + "&cReload cancelled");
        } else if ((args.length == 2 && args[1].equalsIgnoreCase("confirm")) || gamesNotRunning()) {
            long start = System.currentTimeMillis();
            Util.scm(sender, prefix + "&6Reloading plugin... observe console for errors!");
            HG.getPlugin().reloadPlugin();
            Util.scm(sender, prefix + "&7Reloaded &asuccessfully &7in &b" +
                    (System.currentTimeMillis() - start) + "&7 milliseconds");
        }
        return true;
    }

    public boolean gamesNotRunning() {
        int running = plugin.getManager().gamesRunning();
        if (running > 0) {
            TextComponent msg1 = Text.message("&6There are still &b" + running + "&6 games running.");
            TextComponent msg2 = Text.message("&6Do you wish to stop all games and reload?");
            Text.sendMessage(sender, msg1);
            Text.sendMessage(sender, msg2);
            if (sender instanceof Player) {
                TextComponent yes = Text.clickableCommand("&aYES", "/hg reload confirm", lines -> {
                    lines.add("&7Click &aYES");
                    lines.add("&7to stop all games");
                    lines.add("&7and reload");
                });
                TextComponent space = Text.message(" &7or ");
                TextComponent no = Text.clickableCommand("&cNO", "/hg reload cancel", lines -> {
                    lines.add("&7Click &cNO");
                    lines.add("&7to cancel reload");
                });
                Text.sendMessage(sender, yes, space, no);
            } else {
                Util.log("&6Type &bhg reload confirm &6to force reload.");
            }
            return false;
        }
        return true;
    }

}
