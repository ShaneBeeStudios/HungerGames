package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.util.Util;

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
		Game g = gameManager.getGame(args[1]);
		if (g != null) {
			if (g.getStatus() == Status.NOTREADY || g.getStatus() == Status.BROKEN) {
				g.setStatus(Status.READY);
				Util.scm(sender, lang.cmd_toggle_unlocked.replace("<arena>", g.getName()));
			} else {
				g.stop(false);
				g.setStatus(Status.NOTREADY);
				Util.scm(sender, lang.cmd_toggle_locked.replace("<arena>", g.getName()));
			}
		} else {
			Util.scm(sender, lang.cmd_delete_noexist);
		}
		return true;
	}

}