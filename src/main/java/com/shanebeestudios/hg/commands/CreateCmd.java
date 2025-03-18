package com.shanebeestudios.hg.commands;

import com.shanebeestudios.hg.game.Bound;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.data.PlayerSession;
import com.shanebeestudios.hg.util.Util;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

import java.util.Collections;


public class CreateCmd extends BaseCmd {

	public CreateCmd() {
		forcePlayer = true;
		cmdName = "create";
		argLength = 6;
		usage = "<name> <min-player> <max-player> <time> <cost>";
	}

	@Override
	public boolean run() {
		if (!plugin.getPlayerSessions().containsKey(player.getUniqueId())) {
			Util.sendPrefixedMessage(player, lang.cmd_create_need_selection);
		} else {
			PlayerSession session = plugin.getPlayerSessions().get(player.getUniqueId());
			if (!session.hasValidSelection()) {
				Util.sendPrefixedMessage(player, lang.cmd_create_need_selection);
			} else {
				if (args[1].equalsIgnoreCase("all")) {
					Util.scm(player, "&cYou can not name an arena '&ball&c'");
					return true;
				}
				if (!Util.isInt(args[2]) || !Util.isInt(args[3]) || !Util.isInt(args[4])) {
					player.sendMessage(lang.cmd_base_wrongusage + " " + sendHelpLine());
				} else if (Integer.parseInt(args[4]) % 30 != 0) {
					Util.sendPrefixedMessage(player, lang.cmd_create_divisible_1);
					Util.sendPrefixedMessage(player, lang.cmd_create_divisible_2);
					return true;
				} else if (Integer.parseInt(args[2]) > Integer.parseInt(args[3])) {
					Util.sendPrefixedMessage(player, lang.cmd_create_minmax);
					sendHelpLine();
				} else {
					boolean less = session.getLoc1().getBlockY() < session.getLoc2().getBlockY();
					Location lesser = less ? session.getLoc1() : session.getLoc2();
					Location greater = less ? session.getLoc2() : session.getLoc1();
					int freeroam = plugin.getConfig().getInt("settings.free-roam");
					int cost = Integer.parseInt(args[5]);
					Configuration config = arenaConfig.getCustomConfig();
					config.set("arenas." + args[1] + ".bound.world", player.getWorld().getName());
					config.set("arenas." + args[1] + ".bound.x", lesser.getBlockX());
					config.set("arenas." + args[1] + ".bound.y", lesser.getBlockY() -  1); // we go down 1 to prevent 'out of bound' issues
					config.set("arenas." + args[1] + ".bound.z", lesser.getBlockZ());
					config.set("arenas." + args[1] + ".bound.x2", greater.getBlockX());
					config.set("arenas." + args[1] + ".bound.y2", greater.getBlockY());
					config.set("arenas." + args[1] + ".bound.z2", greater.getBlockZ());
					config.set("arenas." + args[1] + ".info.cost", cost);
					config.set("arenas." + args[1] + ".info.timer", Integer.parseInt(args[4]));
					config.set("arenas." + args[1] + ".info.min-players", Integer.parseInt(args[2]));
					config.set("arenas." + args[1] + ".info.max-players", Integer.parseInt(args[3]));
					config.set("arenas." + args[1] + ".commands", Collections.singletonList("none"));
					arenaConfig.saveCustomConfig();
					arenaConfig.reloadCustomConfig();

					Bound b = new Bound(player.getWorld().getName(), lesser.getBlockX(), lesser.getBlockY(), lesser.getBlockZ(), greater.getBlockX(), greater.getBlockY(), greater.getBlockZ());
					plugin.getGames().add(new Game(args[1], b, Integer.parseInt(args[4]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), freeroam, cost));
					Util.sendPrefixedMessage(player, lang.cmd_create_created.replace("<arena>", args[1]));
					Util.sendPrefixedMessage(player, lang.cmd_create_add_spawn);
					return true;
				}
			}
		}
		return true;
	}
}
