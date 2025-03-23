package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.api.util.Util;

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
				g.startPreGameCountdown();
				Util.sendMessage(sender, lang.cmd_start_starting.replace("<arena>", args[1]));
			} else if (status == Status.COUNTDOWN) {
				g.getStartingTask().stop();
				g.startFreeRoam();
				Util.sendMessage(sender, "&aGame starting now");
			} else {
				Util.sendMessage(sender, "&cGame has already started");
			}
		} else {
			sender.sendMessage(lang.cmd_delete_noexist);
		}
		return true;
	}
}
