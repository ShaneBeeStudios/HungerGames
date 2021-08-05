package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.GamePlayerData;
import tk.shanebee.hg.util.Util;

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
			Util.scm(player, lang.cmd_join_in_game);
		} else {
			Game game = gameManager.getGame(args[1]);
			if (game != null) {
				GamePlayerData gamePlayerData = game.getGamePlayerData();
				if (!gamePlayerData.getPlayers().contains(player.getUniqueId()) && !gamePlayerData.getSpectators().contains(player.getUniqueId())) {
					Status status = game.getGameArenaData().getStatus();
					if (status == Status.RUNNING || status == Status.BEGINNING) {
						gamePlayerData.spectate(player);
					} else {
						Util.scm(player, "This game is not running, status: " + status);
					}
				}
			} else {
				Util.scm(player, lang.cmd_delete_noexist);
			}
		}
		return true;
	}

}
