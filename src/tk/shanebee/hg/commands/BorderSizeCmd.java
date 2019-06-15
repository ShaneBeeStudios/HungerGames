package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class BorderSizeCmd extends BaseCmd {

	public BorderSizeCmd() {
		forcePlayer = true;
		cmdName = "bordersize";
		forceInGame = false;
		argLength = 3;
		usage = "<arena-name> <size=radius>";
	}

	@Override
	public boolean run() {
		Game game = HG.manager.getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			int radius = Integer.valueOf(args[2]);
			HG.arenaconfig.getCustomConfig().set("arenas." + name + ".border.size", radius);
		} else {
			Util.scm(player, HG.lang.cmd_delete_noexist);
		}
		return true;
	}

}
