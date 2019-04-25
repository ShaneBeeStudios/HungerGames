package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class ListGamesCmd extends BaseCmd {

	public ListGamesCmd() {
		forcePlayer = false;
		cmdName = "listgames";
		forceInGame = false;
		argLength = 1;
	}

	@Override
	public boolean run() {
		Util.scm(sender, "&6&l Games:");
		for (Game g : HG.plugin.games) {
			Util.scm(sender, " &4 - &6" + g.getName() + "&4:&6" + g.getStatus().getName());
		}
		return true;
	}
}