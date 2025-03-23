package com.shanebeestudios.hg.old_commands;

import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.Status;
import com.shanebeestudios.hg.game.GamePlayerData;
import com.shanebeestudios.hg.util.Util;

public class SpectateCmd extends BaseCmd {

	public SpectateCmd() {
		forcePlayer = true;
		cmdName = "spectate";
		forceInGame = false;
		argLength = 2;
        usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		if (playerManager.hasPlayerData(player) || playerManager.hasSpectatorData(player)) {
			Util.scm(player, lang.cmd_join_already_in_game);
		} else {
			Game game = gameManager.getGame(args[1]);
			GamePlayerData gamePlayerData = game.getGamePlayerData();
			if (game != null && !gamePlayerData.getPlayers().contains(player.getUniqueId()) && !gamePlayerData.getSpectators().contains(player.getUniqueId())) {
				Status status = game.getGameArenaData().getStatus();
				if (status == Status.RUNNING || status == Status.FREE_ROAM) {
					gamePlayerData.spectate(player);
				} else {
					Util.scm(player, "This game is not running, status: " + status);
				}
			} else {
				Util.scm(player, lang.cmd_delete_noexist);
			}
		}
		return true;
	}

}
