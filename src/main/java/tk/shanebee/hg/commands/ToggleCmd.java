package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Status;
import tk.shanebee.hg.game.GameArenaData;
import tk.shanebee.hg.util.Util;

public class ToggleCmd extends BaseCmd {

	public ToggleCmd() {
		forcePlayer = false;
		cmdName = "toggle";
		forceInGame = false;
		argLength = 2;
		usage = "<game>";
	}

	@Override
	public boolean run() {
		Game game = gameManager.getGame(args[1]);
		GameArenaData gameArenaData = game.getGameArenaData();
		if (game != null) {
			if (gameArenaData.getStatus() == Status.NOTREADY || gameArenaData.getStatus() == Status.BROKEN) {
				gameArenaData.setStatus(Status.READY);
				Util.scm(sender, lang.cmd_toggle_unlocked.replace("<arena>", gameArenaData.getName()));
			} else {
				game.stop(false);
				gameArenaData.setStatus(Status.NOTREADY);
				Util.scm(sender, lang.cmd_toggle_locked.replace("<arena>", gameArenaData.getName()));
			}
		} else {
			Util.scm(sender, lang.cmd_delete_noexist);
		}
		return true;
	}

}
