package me.minebuilders.hg.listeners;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.commands.BaseCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandListener implements CommandExecutor, TabCompleter {

	private final HG p;

	public CommandListener(HG plugin) {
		this.p = plugin;
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !p.cmds.containsKey(args[0])) {
			Util.scm(s, "&4*&c&m                         &7*( &3&lHungerGames &7)*&c&m                          &4*");
			for (BaseCmd cmd : p.cmds.values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hg." + cmd.cmdName)) Util.scm(s, "  &7&l- " + cmd.sendHelpLine());
			}
			Util.scm(s, "&4*&c&m                                                                             &4*");
		} else p.cmds.get(args[0]).processCmd(p, s, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 1) {
			ArrayList<String> matches = new ArrayList<>();
			for (String name : HG.plugin.cmds.keySet()) {
				if (StringUtil.startsWithIgnoreCase(name, args[0])) {
					if (sender.hasPermission("hg." + name))
						matches.add(name);
				}
			}
			return matches;
		} else if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("team")) {
				if (args.length == 2) {
					String[] listTeam = {"invite", "accept"};
					ArrayList<String> matchesTeam = new ArrayList<>();
					for (String name : listTeam) {
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesTeam.add(name);
						}
					}
					return matchesTeam;
				}
				return null;
			} else if (args[0].equalsIgnoreCase("delete") ||
					args[0].equalsIgnoreCase("debug") ||
					args[0].equalsIgnoreCase("stop") ||
					(args[0].equalsIgnoreCase("forcestart")) ||
					(args[0].equalsIgnoreCase("leave")) ||
					(args[0].equalsIgnoreCase("join")) ||
					(args[0].equalsIgnoreCase("setlobbywall")) ||
					(args[0].equalsIgnoreCase("toggle"))) {
				ArrayList<String> matchesDelete = new ArrayList<>();
				if (args.length == 2) {
					for (Game name : HG.plugin.games) {
						if (StringUtil.startsWithIgnoreCase(name.getName(), args[1])) {
							matchesDelete.add(name.getName());
						}
					}
					return matchesDelete;
				}
			} else if (args[0].equalsIgnoreCase("kit")) {
				if (args.length == 2) {
					ArrayList<String> matchesKit = new ArrayList<>();
					for (String name : HG.plugin.kit.kititems.keySet()) {
						if (StringUtil.startsWithIgnoreCase(name, args[1])) {
							matchesKit.add(name);
						}
					}
					return matchesKit;
				}
			} else if (args[0].equalsIgnoreCase("create")) {
				ArrayList<String> matchesCreate = new ArrayList<>();
				switch (args.length) {
					case 2:
						if (StringUtil.startsWithIgnoreCase("<arena-name>", args[1]))
							matchesCreate.add("<arena-name>");
						break;
					case 3:
						if (StringUtil.startsWithIgnoreCase("<min-players>", args[2]))
							matchesCreate.add("<min-players>");
						break;
					case 4:
						if (StringUtil.startsWithIgnoreCase("<max-players>", args[3]))
							matchesCreate.add("<max-players>");
						break;
					case 5:
						if (StringUtil.startsWithIgnoreCase("<time-seconds>", args[4]))
							matchesCreate.add("<time-seconds>");
						break;
				}
				return matchesCreate;
			}
		}
		return Collections.emptyList();
	}

}