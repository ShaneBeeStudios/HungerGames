package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

public class StopCmd extends BaseCmd {

	public StopCmd() {
		forcePlayer = false;
		cmdName = "stop";
		forceInGame = false;
		argLength = 2;
		usage = "<game>";
	}

	@Override
	public boolean run() {
		if (args[1].equalsIgnoreCase("all")) {
			for (Game game : HG.plugin.games) {
				if (game.getStatus() == Status.RUNNING || game.getStatus() == Status.WAITING ||
						game.getStatus() == Status.BEGINNING || game.getStatus() == Status.COUNTDOWN) {
					game.stop();
				}
			}
			Util.scm(sender, HG.lang.cmd_stop_all);
			return true;
		}
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			g.stop();
			Util.scm(sender, HG.lang.cmd_stop_arena.replace("<arena>", args[1]));
		} else {
			Util.scm(sender, HG.lang.cmd_stop_noexist.replace("<arena>", args[1]));
		}
		return true;
	}
}