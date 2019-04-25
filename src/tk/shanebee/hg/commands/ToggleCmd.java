package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.Util;

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