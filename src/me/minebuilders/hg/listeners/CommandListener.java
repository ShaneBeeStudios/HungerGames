package me.minebuilders.hg.listeners;

import com.google.common.collect.ImmutableList;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.commands.BaseCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements CommandExecutor, TabCompleter {

	private final HG p;

	public CommandListener(HG plugin) {
		this.p = plugin;
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !p.cmds.containsKey(args[0])) {
			Util.scm(s, "&4*&c&m                           &4*( &6Hungergames &4)*&c&m                           &4*");
			for (BaseCmd cmd : p.cmds.values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hg." + cmd.cmdName)) Util.scm(s, "  &4- " + cmd.sendHelpLine());
			}
			Util.scm(s, "&4*&c&m                                                                             &4*");
		} else p.cmds.get(args[0]).processCmd(p, s, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (args.length >= 2) {
			return ImmutableList.of();
		}
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}
		String[] list = {"debug", "toggle", "team", "list", "delete", "setlobbywall", "listgames", "reload", "addspawn", "stop",
				"forcestart", "leave", "wand", "kit", "setexit", "create", "join"};
		String arg = builder.toString().trim();
		ArrayList<String> matches = new ArrayList<>();
		for (String name : list) {
			if (StringUtil.startsWithIgnoreCase(name, arg)) {
				if (sender.hasPermission("hg." + name))
					matches.add(name);
			}
		}
		return matches;
	}

}