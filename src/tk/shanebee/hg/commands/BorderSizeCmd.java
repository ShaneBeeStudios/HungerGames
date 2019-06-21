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
		usage = "<arena-name> <size=diameter>";
	}

	@Override
	public boolean run() {
		Game game = HG.manager.getGame(args[1]);
		if (game != null) {
			String name = game.getName();
			int radius;
			try {
				radius = Integer.valueOf(args[2]);
			} catch (NumberFormatException e) {
				Util.scm(player, sendHelpLine());
				return false;
			}
			HG.arenaconfig.getCustomConfig().set("arenas." + name + ".border.size", radius);
			game.setBorderSize(radius);
			HG.arenaconfig.saveCustomConfig();
			Util.scm(player, HG.plugin.lang.cmd_border_size.replace("<arena>", name).replace("<size>", String.valueOf(radius)));
		} else {
			Util.scm(player, HG.plugin.lang.cmd_delete_noexist);
		}
		return true;
	}

}
