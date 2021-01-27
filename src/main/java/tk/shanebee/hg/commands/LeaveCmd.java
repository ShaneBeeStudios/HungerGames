package tk.shanebee.hg.commands;

import org.bukkit.permissions.PermissionDefault;
import tk.shanebee.hg.*;
import tk.shanebee.hg.data.Config;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.game.GameArenaData;
import tk.shanebee.hg.util.Util;
import tk.shanebee.hg.util.Vault;

public class LeaveCmd extends BaseCmd {

	public LeaveCmd() {
		forcePlayer = true;
		cmdName = "leave";
		forceInGame = true;
		argLength = 1;
		permissionDefault = PermissionDefault.TRUE;
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
					Util.sendPrefixedMessage(player, lang.cmd_leave_refund.replace("<cost>",
                            String.valueOf(gameArenaData.getCost())));
				}
			}
			game.getGamePlayerData().leave(player, false);
		} else {
			game = playerManager.getSpectatorData(player).getGame();
			game.getGamePlayerData().leaveSpectate(player);
		}
		Util.sendPrefixedMessage(player, lang.cmd_leave_left.replace("<arena>", game.getGameArenaData().getName()));
		return true;
	}
}
