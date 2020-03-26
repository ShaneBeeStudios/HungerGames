package tk.shanebee.hg.commands;

import org.bukkit.Location;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

public class SetExitCmd extends BaseCmd {

	public SetExitCmd() {
		forcePlayer = true;
		cmdName = "setexit";
		forceInGame = false;
		argLength = 1;
	}

	@Override
	public boolean run() {
		Location l = player.getLocation();
		String loc = l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() + ":" + l.getYaw() + ":" + l.getPitch();
		plugin.getConfig().set("settings.globalexit", loc);
		plugin.saveConfig();
		Util.scm(player, lang.cmd_exit_set + " " + loc.replace(":", "&6,&c "));
		
		for (Game g : plugin.getGames())
			g.setExit(l);
		return true;
	}
}
