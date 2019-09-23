package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.util.Util;

import org.bukkit.Location;

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
		HG.plugin.getConfig().set("settings.globalexit", loc);
		HG.plugin.saveConfig();
		Util.scm(player, HG.plugin.getLang().cmd_exit_set + " " + loc.replace(":", "&6,&c "));
		
		for (Game g : HG.plugin.getGames())
			g.setExit(l);
		return true;
	}
}