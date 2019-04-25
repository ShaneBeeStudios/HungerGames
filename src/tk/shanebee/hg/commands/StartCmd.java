package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.Util;

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
			if (g.getStatus() == Status.WAITING || g.getStatus() == Status.READY) {
				g.startPreGame();
				Util.scm(sender, HG.lang.cmd_start_starting.replace("<arena>", args[1]));
			} else if (g.getStatus() == Status.COUNTDOWN) {
				g.starting.stop();
				g.startFreeRoam();
				Util.scm(sender, "&aGame starting now");
			} else {
				Util.scm(sender, "&cGame has already started");
			}
		} else {
			sender.sendMessage(HG.lang.cmd_delete_noexist);
		}
		return true;
	}
}