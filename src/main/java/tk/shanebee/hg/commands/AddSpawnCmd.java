package tk.shanebee.hg.commands;

import java.util.List;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

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
		Game g = HG.getPlugin().getManager().getGame(player.getLocation());
		int num = g.getSpawns().size() + 1;
		Configuration c = HG.getPlugin().getArenaConfig().getCustomConfig();
		List<String> d = c.getStringList("arenas."+g.getName() + ".spawns");
		Location l = player.getLocation();
		for (Location lb : g.getSpawns()) {
			if (lb.getBlock().equals(l.getBlock())) {
				Util.scm(player, HG.getPlugin().getLang().cmd_spawn_same);
				return true;
			}
		}
		d.add(l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch());
		c.set("arenas."+g.getName()+".spawns", d);
		g.addSpawn(l);
		HG.getPlugin().getArenaConfig().saveCustomConfig();
		Util.scm(player, HG.getPlugin().getLang().cmd_spawn_set.replace("<number>", String.valueOf(num)));
		
        HG.getPlugin().getManager().checkGame(g, player);
		return true;
	}
}
