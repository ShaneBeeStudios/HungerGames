package tk.shanebee.hg.commands;

import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

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
			for (Game game : plugin.getGames()) {
			    Status status = game.getStatus();
				if (status == Status.RUNNING || status == Status.WAITING || status == Status.FREE_ROAM || status == Status.COUNTDOWN) {
					game.stop(false);
				}
			}
			Util.scm(sender, lang.cmd_stop_all);
			return true;
		}
		Game g = gameManager.getGame(args[1]);
		if (g != null) {
			g.stop(false);
			Util.scm(sender, lang.cmd_stop_arena.replace("<arena>", args[1]));
		} else {
			Util.scm(sender, lang.cmd_stop_noexist.replace("<arena>", args[1]));
		}
		return true;
	}
}
