package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

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
		Util.msg(player, "&6Global Exit Spawn Set: " + loc.replace(":", "&6,&c "));
		
		for (Game g : HG.plugin.games)
			g.setExit(l);
		return true;
	}
}