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
		if (!HG.getPlugin().getPlayerSessions().containsKey(player.getUniqueId())) {
			Util.scm(player, HG.getPlugin().getLang().cmd_create_need_selection);
		} else {
			PlayerSession s = HG.getPlugin().getPlayerSessions().get(player.getUniqueId());
			if (!s.hasValidSelection()) {
				Util.scm(player, HG.getPlugin().getLang().cmd_create_need_selection);
			} else {
				if (!Util.isInt(args[2]) || !Util.isInt(args[3]) || !Util.isInt(args[4])) {
					player.sendMessage(HG.getPlugin().getLang().cmd_base_wrongusage + " " + sendHelpLine());
				} else if (Integer.parseInt(args[4]) % 30 != 0) {
					Util.scm(player, HG.getPlugin().getLang().cmd_create_divisible_1);
					Util.scm(player, HG.getPlugin().getLang().cmd_create_divisible_2);
					return true;
				} else if (Integer.parseInt(args[2]) > Integer.parseInt(args[3])) {
					Util.scm(player, HG.getPlugin().getLang().cmd_create_minmax);
					sendHelpLine();
				} else {
					Location l = s.getLoc1();
					Location l2 = s.getLoc2();
					int freeroam = HG.getPlugin().getConfig().getInt("settings.free-roam");
					int cost = Integer.parseInt(args[5]);
					Configuration c = HG.getPlugin().getArenaConfig().getCustomConfig();
					c.set("arenas." + args[1] +".bound.world", player.getWorld().getName());
					c.set("arenas." + args[1] +".bound.x", l.getBlockX());
					c.set("arenas." + args[1] +".bound.y", l.getBlockY());
					c.set("arenas." + args[1] +".bound.z", l.getBlockZ());
					c.set("arenas." + args[1] +".bound.x2", l2.getBlockX());
					c.set("arenas." + args[1] +".bound.y2", l2.getBlockY());
					c.set("arenas." + args[1] +".bound.z2", l2.getBlockZ());
					c.set("arenas." + args[1] +".info.cost", cost);
					c.set("arenas." + args[1] +".info.timer", Integer.parseInt(args[4]));
					c.set("arenas." + args[1] +".info.min-players", Integer.parseInt(args[2]));
					c.set("arenas." + args[1] +".info.max-players", Integer.parseInt(args[3]));
					c.set("arenas." + args[1] + ".commands", Collections.singletonList("none"));
					HG.getPlugin().getArenaConfig().saveCustomConfig();
					HG.getPlugin().getArenaConfig().reloadCustomConfig();

					Bound b = new Bound(player.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
					HG.getPlugin().getGames().add(new Game(args[1], b, Integer.parseInt(args[4]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), freeroam, cost));
					Util.scm(player, HG.getPlugin().getLang().cmd_create_created.replace("<arena>", args[1]));
					return true;
				}
			}
		}
		return true;
	}
}
