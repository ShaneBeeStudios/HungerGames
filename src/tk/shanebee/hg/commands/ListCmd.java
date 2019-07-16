package tk.shanebee.hg.commands;

import tk.shanebee.hg.Game;
import tk.shanebee.hg.HG;
import tk.shanebee.hg.Util;

public class ListCmd extends BaseCmd {

	public ListCmd() {
		forcePlayer = true;
		cmdName = "list";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		StringBuilder p = new StringBuilder();
		Game g = HG.plugin.getPlayers().get(player.getUniqueId()).getGame();
		for (String s : Util.convertUUIDListToStringList(g.getPlayers())) {
			p.append("&6, &c").append(s);
		}
		p = new StringBuilder(p.substring(3));
		Util.scm(player, "&6Players:" + p);
		return true;
	}
}