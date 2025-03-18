package com.shanebeestudios.hg.commands;

import com.shanebeestudios.hg.HG;
import com.shanebeestudios.hg.game.Game;
import com.shanebeestudios.hg.util.Util;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		forceInGame = false;
		argLength = 2;
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {

		if (playerManager.hasPlayerData(player) || playerManager.hasSpectatorData(player)) {
			Util.scm(player, HG.getPlugin().getLang().cmd_join_in_game);
		} else {
			Game g = gameManager.getGame(args[1]);
			if (g != null && !g.getGamePlayerData().getPlayers().contains(player.getUniqueId())) {
				g.getGamePlayerData().join(player, true);
			} else {
				Util.scm(player, lang.cmd_delete_noexist);
			}
		}
		return true;
	}

}
