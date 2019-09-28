package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
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
			for (Game game : HG.getPlugin().getGames()) {
				if (game.getStatus() == Status.RUNNING || game.getStatus() == Status.WAITING ||
						game.getStatus() == Status.BEGINNING || game.getStatus() == Status.COUNTDOWN) {
					game.stop(false);
				}
			}
			Util.scm(sender, HG.getPlugin().getLang().cmd_stop_all);
			return true;
		}
		Game g = HG.getPlugin().getManager().getGame(args[1]);
		if (g != null) {
			g.stop(false);
			Util.scm(sender, HG.getPlugin().getLang().cmd_stop_arena.replace("<arena>", args[1]));
		} else {
			Util.scm(sender, HG.getPlugin().getLang().cmd_stop_noexist.replace("<arena>", args[1]));
		}
		return true;
	}
}