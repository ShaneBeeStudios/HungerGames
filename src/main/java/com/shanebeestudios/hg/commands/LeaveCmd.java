package com.shanebeestudios.hg.commands;

import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.data.Config;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.game.GameArenaData;
import com.shanebeestudios.hg.util.Util;
import com.shanebeestudios.hg.util.Vault;

public class LeaveCmd extends BaseCmd {

	public LeaveCmd() {
		forcePlayer = true;
		cmdName = "leave";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		Game game;
		if (playerManager.hasPlayerData(player)) {
			game = playerManager.getPlayerData(player).getGame();
			if (Config.economy) {
				GameArenaData gameArenaData = game.getGameArenaData();
				Status status = gameArenaData.getStatus();
				if ((status == Status.WAITING || status == Status.COUNTDOWN) && gameArenaData.getCost() > 0) {
					Vault.economy.depositPlayer(player, gameArenaData.getCost());
					Util.scm(player, lang.prefix +
							lang.cmd_leave_refund.replace("<cost>", String.valueOf(gameArenaData.getCost())));
				}
			}
			game.getGamePlayerData().leave(player, false);
		} else {
			game = playerManager.getSpectatorData(player).getGame();
			game.getGamePlayerData().leaveSpectate(player);
		}
		Util.scm(player, lang.prefix + lang.cmd_leave_left.replace("<arena>", game.getGameArenaData().getName()));
		return true;
	}
}
