package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import org.bukkit.ChatColor;

public class ToggleCmd extends BaseCmd {

	public ToggleCmd() {
		forcePlayer = false;
		cmdName = "toggle";
		forceInGame = false;
		argLength = 2;
		usage = "<game>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			if (g.getStatus() == Status.NOTREADY || g.getStatus() == Status.BROKEN) {
				g.setStatus(Status.WAITING);
				Util.scm(sender, HG.lang.cmd_toggle_unlocked);
			} else {
				g.setStatus(Status.NOTREADY);
				Util.scm(sender, HG.lang.cmd_toggle_locked);
			}
		} else {
			sender.sendMessage(HG.lang.cmd_delete_noexist);
		}
		return true;
	}
}