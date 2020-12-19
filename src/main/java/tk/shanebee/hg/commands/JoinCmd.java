package tk.shanebee.hg.commands;

import tk.shanebee.hg.HG;
import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

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
			Util.sendPrefixedMessage(player, HG.getPlugin().getLang().cmd_join_in_game);
		} else {
			Game g = gameManager.getGame(args[1]);
			if (g != null && !g.getGamePlayerData().getPlayers().contains(player.getUniqueId())) {
				g.getGamePlayerData().join(player, true);
			} else {
				Util.sendPrefixedMessage(player, lang.cmd_delete_noexist);
			}
		}
		return true;
	}

}
