package me.minebuilders.hg.commands;

import me.minebuilders.hg.Bound;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerSession;
import me.minebuilders.hg.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;


public class CreateCmd extends BaseCmd {

	public CreateCmd() {
		forcePlayer = true;
		cmdName = "create";
		argLength = 5;
		usage = "<name> <min-player> <max-player> <time>";
	}

	@Override
	public boolean run() {
		if (!HG.plugin.playerses.containsKey(player.getUniqueId())) {
			Util.msg(player, ChatColor.RED + "You need to make a selection before making an arena!");
		} else {
			PlayerSession s = HG.plugin.playerses.get(player.getUniqueId());
			if (!s.hasValidSelection()) {
				Util.msg(player, ChatColor.RED + "You need to make a selection before making an arena!");	
			} else {
				if (!Util.isInt(args[2]) || !Util.isInt(args[3]) || !Util.isInt(args[4])) {
					player.sendMessage(ChatColor.RED + "Wrong usage: " + sendHelpLine());
				} else if (Integer.parseInt(args[4]) % 30 != 0) {
					player.sendMessage(ChatColor.RED + "time-in-seconds must be divisible by 30!");
					player.sendMessage(ChatColor.RED + "Ex: 90 is divisible by 30");
					return true;
				} else if (Integer.parseInt(args[2]) > Integer.parseInt(args[3])) {
					player.sendMessage(ChatColor.RED + "min-players cannot be more then max-players!");
					sendHelpLine();
				} else {
					Location l = s.getLoc1();
					Location l2 = s.getLoc2();
					int freeroam = HG.plugin.getConfig().getInt("settings.free-roam");
					Configuration c = HG.arenaconfig.getCustomConfig();
					c.set("arenas." + args[1] +".bound.world", player.getWorld().getName());
					c.set("arenas." + args[1] +".bound.x", l.getBlockX());
					c.set("arenas." + args[1] +".bound.y", l.getBlockY());
					c.set("arenas." + args[1] +".bound.z", l.getBlockZ());
					c.set("arenas." + args[1] +".bound.x2", l2.getBlockX());
					c.set("arenas." + args[1] +".bound.y2", l2.getBlockY());
					c.set("arenas." + args[1] +".bound.z2", l2.getBlockZ());
					c.set("arenas." + args[1] +".info." + "timer", Integer.parseInt(args[4]));
					c.set("arenas." + args[1] +".info." + "min-players", Integer.parseInt(args[2]));
					c.set("arenas." + args[1] +".info." + "max-players", Integer.parseInt(args[3]));
					HG.arenaconfig.saveCustomConfig();
					HG.arenaconfig.reloadCustomConfig();

					Bound b = new Bound(player.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());
					HG.plugin.games.add(new Game(args[1], b, Integer.parseInt(args[4]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), freeroam));
					Util.msg(player, ChatColor.GREEN+"You created HungerGames arena " + args[1] + "!");
					return true;
				}
			}
		}
		return true;
	}
}
