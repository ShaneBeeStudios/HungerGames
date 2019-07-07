package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.Util;

public class SpectateCmd extends BaseCmd {

	public SpectateCmd() {
		forcePlayer = true;
		cmdName = "spectate";
		forceInGame = false;
		argLength = 2;
	}

	@Override
	public boolean run() {
		if (HG.plugin.getPlayers().containsKey(player.getUniqueId()) || HG.plugin.getSpectators().containsKey(player.getUniqueId())) {
			Util.scm(player, HG.plugin.lang.cmd_join_in_game);
		} else {
			Game game = HG.plugin.getManager().getGame(args[1]);
			if (game != null && !game.getPlayers().contains(player.getUniqueId()) && !game.getSpectators().contains(player)) {
				Status status = game.getStatus();
				if (status == Status.RUNNING || status == Status.BEGINNING) {
					game.spectate(player);
				} else {
					Util.scm(player, "This game is not running, status: " + status);
				}
			} else {
				Util.scm(player, HG.plugin.lang.cmd_delete_noexist);
			}
		}
		return true;
	}

}
