package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

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
		if (HG.plugin.players.containsKey(player.getUniqueId())) {
			game = HG.plugin.players.get(player.getUniqueId()).getGame();
			game.leave(player, false);
		} else {
			game = HG.plugin.getSpectators().get(player.getUniqueId()).getGame();
			game.leaveSpectate(player);
		}
		Util.scm(player, HG.plugin.lang.prefix + HG.plugin.lang.cmd_leave_left.replace("<arena>", game.getName()));
		return true;
	}
}