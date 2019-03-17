package me.minebuilders.hg.commands;

import java.util.List;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

public class AddSpawnCmd extends BaseCmd {

	public AddSpawnCmd() {
		forcePlayer = true;
		cmdName = "addspawn";
		argLength = 1;
		forceInRegion = true;
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(player.getLocation());
		int num = g.getSpawns().size() + 1;
		Configuration c = HG.arenaconfig.getCustomConfig();
		List<String> d = c.getStringList("arenas."+g.getName() + ".spawns");
		Location l = player.getLocation();
		for (Location lb : g.getSpawns()) {
			if (lb.getBlock().equals(l.getBlock())) {
				player.sendMessage(ChatColor.RED + "You cannot have two spawns in the same location!");
				return true;
			}
		}
		d.add(l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch());
		c.set("arenas."+g.getName()+".spawns", d);
		g.addSpawn(l);
		HG.arenaconfig.saveCustomConfig();
		Util.msg(player, "You set HungerGames spawn #" + num + "!");
		
        HG.manager.checkGame(g, player);
		return true;
	}
}
