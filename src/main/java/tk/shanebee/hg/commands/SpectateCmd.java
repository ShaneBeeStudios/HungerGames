package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
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
		if (HG.getPlugin().getPlayers().containsKey(player.getUniqueId()) || HG.getPlugin().getSpectators().containsKey(player.getUniqueId())) {
			Util.scm(player, HG.getPlugin().getLang().cmd_join_in_game);
		} else {
			Game game = HG.getPlugin().getManager().getGame(args[1]);
			if (game != null && !game.getPlayers().contains(player.getUniqueId()) && !game.getSpectators().contains(player.getUniqueId())) {
				Status status = game.getStatus();
				if (status == Status.RUNNING || status == Status.BEGINNING) {
					game.spectate(player);
				} else {
					Util.scm(player, "This game is not running, status: " + status);
				}
			} else {
				Util.scm(player, HG.getPlugin().getLang().cmd_delete_noexist);
			}
		}
		return true;
	}

}
