package com.shanebeestudios.hg.commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.util.Util;

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
		Game g = gameManager.getGame(args[1]);
		if (g != null) {
			Status status = g.getGameArenaData().getStatus();
			if (status == Status.WAITING || status == Status.READY) {
				g.startPreGame();
				Util.scm(sender, lang.cmd_start_starting.replace("<arena>", args[1]));
			} else if (status == Status.COUNTDOWN) {
				g.getStartingTask().stop();
				g.startFreeRoam();
				Util.scm(sender, "&aGame starting now");
			} else {
				Util.scm(sender, "&cGame has already started");
			}
		} else {
			sender.sendMessage(lang.cmd_delete_noexist);
		}
		return true;
	}
}
