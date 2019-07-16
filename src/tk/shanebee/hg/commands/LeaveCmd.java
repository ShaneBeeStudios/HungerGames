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
		if (HG.plugin.getPlayers().containsKey(player.getUniqueId())) {
			game = HG.plugin.getPlayers().get(player.getUniqueId()).getGame();
			game.leave(player, false);
		} else {
			game = HG.plugin.getSpectators().get(player.getUniqueId()).getGame();
			game.leaveSpectate(player);
		}
		Util.scm(player, HG.plugin.getLang().prefix + HG.plugin.getLang().cmd_leave_left.replace("<arena>", game.getName()));
		return true;
	}
}