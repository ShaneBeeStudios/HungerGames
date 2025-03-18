package com.shanebeestudios.hg.commands;

import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.util.Util;
public class KitCmd extends BaseCmd {

	public KitCmd() {
		forcePlayer = true;
		cmdName = "kit";
		forceInGame = true;
		argLength = 2;
		usage = "<kit>";
	}

	@Override
	public boolean run() {
		Game game = playerManager.getPlayerData(player).getGame();
		Status st = game.getGameArenaData().getStatus();
		if (!game.getKitManager().hasKits()) {
		    Util.scm(player, lang.kit_disabled);
		    return false;
        }
		if (st == Status.WAITING || st == Status.COUNTDOWN) {
			game.getKitManager().setKit(player, args[1]);
		} else {
			Util.scm(player, lang.cmd_kit_no_change);
		}
		return true;
	}

}
