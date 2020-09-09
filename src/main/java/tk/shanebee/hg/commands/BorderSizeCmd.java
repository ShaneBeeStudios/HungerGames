package tk.shanebee.hg.commands;

import tk.shanebee.hg.game.Game;
import tk.shanebee.hg.util.Util;

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
		Game game = gameManager.getGame(args[1]);
		if (game != null) {
			String name = game.getGameArenaData().getName();
			int radius;
			try {
				radius = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				Util.scm(player, sendHelpLine());
				return false;
			}
			arenaConfig.getCustomConfig().set("arenas." + name + ".border.size", radius);
			game.getGameBorderData().setBorderSize(radius);
			arenaConfig.saveCustomConfig();
			Util.scm(player, lang.cmd_border_size.replace("<arena>", name).replace("<size>", String.valueOf(radius)));
		} else {
			Util.scm(player, lang.cmd_delete_noexist);
		}
		return true;
	}

}
