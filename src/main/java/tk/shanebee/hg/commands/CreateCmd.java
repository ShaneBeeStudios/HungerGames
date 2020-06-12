package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Bound;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.data.PlayerSession;
import tk.shanebee.hg.util.Util;

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
			Util.scm(player, lang.cmd_create_need_selection);
		} else {
			PlayerSession selection = plugin.getPlayerSessions().get(player.getUniqueId());
			if (!selection.hasValidSelection()) {
				Util.scm(player, lang.cmd_create_need_selection);
			} else {
				if (!Util.isInt(args[2]) || !Util.isInt(args[3]) || !Util.isInt(args[4])) {
					player.sendMessage(lang.cmd_base_wrongusage + " " + sendHelpLine());
				} else if (Integer.parseInt(args[4]) % 30 != 0) {
					Util.scm(player, lang.cmd_create_divisible_1);
					Util.scm(player, lang.cmd_create_divisible_2);
					return true;
				} else if (Integer.parseInt(args[2]) > Integer.parseInt(args[3])) {
					Util.scm(player, lang.cmd_create_minmax);
					sendHelpLine();
				} else {
					Location location1 = selection.getLoc1();
					Location location2 = selection.getLoc2();
					int freeroam = plugin.getConfig().getInt("settings.free-roam");
					int cost = Integer.parseInt(args[5]);
					Configuration config = arenaConfig.getCustomConfig();
					config.set("arenas." + args[1] +".bound.world", player.getWorld().getName());
					config.set("arenas." + args[1] +".bound.x", location1.getBlockX());
					config.set("arenas." + args[1] +".bound.y", location1.getBlockY());
					config.set("arenas." + args[1] +".bound.z", location1.getBlockZ());
					config.set("arenas." + args[1] +".bound.x2", location2.getBlockX());
					config.set("arenas." + args[1] +".bound.y2", location2.getBlockY());
					config.set("arenas." + args[1] +".bound.z2", location2.getBlockZ());

					config.set("arenas." + args[1] +".care-package-bound.x", location1.getBlockX());
					config.set("arenas." + args[1] +".care-package-bound.y", location1.getBlockY());
					config.set("arenas." + args[1] +".care-package-bound.z", location1.getBlockZ());
					config.set("arenas." + args[1] +".care-package-bound.x2", location2.getBlockX());
					config.set("arenas." + args[1] +".care-package-bound.y2", location2.getBlockY());
					config.set("arenas." + args[1] +".care-package-bound.z2", location2.getBlockZ());
					
					config.set("arenas." + args[1] +".info.cost", cost);
					config.set("arenas." + args[1] +".info.timer", Integer.parseInt(args[4]));
					config.set("arenas." + args[1] +".info.min-players", Integer.parseInt(args[2]));
					config.set("arenas." + args[1] +".info.max-players", Integer.parseInt(args[3]));
					config.set("arenas." + args[1] + ".commands", Collections.singletonList("none"));
					arenaConfig.saveCustomConfig();
					arenaConfig.reloadCustomConfig();

					Bound arenaBound = new Bound(player.getWorld().getName(), location1.getBlockX(), location1.getBlockY(), location1.getBlockZ(), location2.getBlockX(), location2.getBlockY(), location2.getBlockZ());
					
					plugin.getGames().add(new Game(args[1], arenaBound, Integer.parseInt(args[4]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), freeroam, cost, arenaBound));
					Util.scm(player, lang.cmd_create_created.replace("<arena>", args[1]));
					return true;
				}
			}
		}
		return true;
	}
}
