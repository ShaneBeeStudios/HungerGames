package tk.shanebee.hg.commands;

import java.util.List;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

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
				Util.scm(player, HG.plugin.lang.cmd_spawn_same);
				return true;
			}
		}
		d.add(l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch());
		c.set("arenas."+g.getName()+".spawns", d);
		g.addSpawn(l);
		HG.arenaconfig.saveCustomConfig();
		Util.scm(player, HG.plugin.lang.cmd_spawn_set.replace("<number>", String.valueOf(num)));
		
        HG.manager.checkGame(g, player);
		return true;
	}
}
