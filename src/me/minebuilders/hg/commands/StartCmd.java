package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.ChatColor;

public class StartCmd extends BaseCmd {

	public StartCmd() {
		forcePlayer = false;
		cmdName = "forcestart";
		forceInGame = false;
		argLength = 2;
		usage = "<game>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			g.startPreGame();
			Util.scm(sender, HG.lang.cmd_start_starting.replace("<arena>", args[1]));
		} else {
			sender.sendMessage(HG.lang.cmd_delete_noexist);
		}
		return true;
	}
}