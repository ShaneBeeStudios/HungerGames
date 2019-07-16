package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

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

		if (HG.plugin.getPlayers().containsKey(player.getUniqueId())) {
			Util.scm(player, HG.plugin.getLang().cmd_join_in_game);
		} else {
			Game g = HG.plugin.getManager().getGame(args[1]);
			if (g != null && !g.getPlayers().contains(player.getUniqueId())) {
				g.join(player);
			} else {
				Util.scm(player, HG.plugin.getLang().cmd_delete_noexist);
			}
		}
		return true;
	}
}