package tk.shanebee.hg.commands;

import tk.shanebee.hg.*;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

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
				Status status = game.getStatus();
				if ((status == Status.WAITING || status == Status.COUNTDOWN) && game.getCost() > 0) {
					Vault.economy.depositPlayer(player, game.getCost());
					Util.scm(player, lang.prefix +
							lang.cmd_leave_refund.replace("<cost>", String.valueOf(game.getCost())));
				}
			}
			game.leave(player, false);
		} else {
			game = playerManager.getSpectatorData(player).getGame();
			game.leaveSpectate(player);
		}
		Util.scm(player, lang.prefix + lang.cmd_leave_left.replace("<arena>", game.getName()));
		return true;
	}
}