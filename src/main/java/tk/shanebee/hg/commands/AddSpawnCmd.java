package tk.shanebee.hg.commands;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

import java.util.List;

public class AddSpawnCmd extends BaseCmd {

	public AddSpawnCmd() {
		forcePlayer = true;
		cmdName = "addspawn";
		argLength = 1;
		forceInRegion = true;
	}

	@Override
	public boolean run() {
		Game g = gameManager.getGame(player.getLocation());
		int num = g.getSpawns().size() + 1;
		Configuration c = arenaConfig.getCustomConfig();
		List<String> d = c.getStringList("arenas." + g.getName() + ".spawns");
		Location l = player.getLocation();
		for (Location lb : g.getSpawns()) {
			if (lb.getBlock().equals(l.getBlock())) {
				Util.scm(player, lang.cmd_spawn_same);
				return true;
			}
		}
		d.add(l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch());
		c.set("arenas." + g.getName() + ".spawns", d);
		g.addSpawn(l);
		arenaConfig.saveCustomConfig();
		Util.scm(player, lang.cmd_spawn_set.replace("<number>", String.valueOf(num)));

		gameManager.checkGame(g, player);
		return true;
	}
}
