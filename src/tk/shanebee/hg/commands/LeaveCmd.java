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
		Game g = HG.plugin.players.get(player.getUniqueId()).getGame();
		g.leave(player, false);
		Util.scm(player, HG.plugin.lang.prefix + HG.plugin.lang.cmd_leave_left.replace("<arena>", g.getName()));
		return true;
	}
}